package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuInfoMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuInfoService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author hongchen
 * @date 2020/4/10 23:28
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired(required = false)
    private SkuFeign skuFeign;
    @Autowired(required = false)
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 将数据库数据导入索引库中
     */
    @Override
    public void importSkuInfoEs() {
        // 通过feign调用商品微服务接口
        Result<List<Sku>> result = skuFeign.findSkusByStatus("1");
        List<Sku> list = result.getData();
        if (list != null && list.size() > 0) {
            // 2、需要处理数据 List<Sku>--->List<SkuInfo>
            String text = JSON.toJSONString(list);
            List<SkuInfo> skuInfos = JSON.parseArray(text, SkuInfo.class);
            for (SkuInfo skuInfo : skuInfos) {
                // {"手机屏幕尺寸":"5寸","网络":"联通3G+移动4G"}
                String spec = skuInfo.getSpec();
                Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
                skuInfo.setSpecMap(specMap);
            }
            // 3、将该数据插入到索引库中
            skuInfoMapper.saveAll(skuInfos);
        }
    }

    /**
     * 检索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {


        // 1、封装检索条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = builderBasicQuery(searchMap);
        // 2、根据条件查询：商品列表数据
        Map<String, Object> resultMap = searchForPage(nativeSearchQueryBuilder);
        Long totalElements = (Long) resultMap.get("totalElements");
        String total=totalElements.toString();
        int size=Integer.parseInt(total);
        // 商品分类列表数据
        List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
        resultMap.put("categoryList", categoryList);
        // 品牌列表数据
        List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
        resultMap.put("brandList", brandList);
        /*// 商品价格列表数据
        List<String> priceList = searchPriceList(nativeSearchQueryBuilder);
        resultMap.put("priceList", priceList);*/
        // 商品规格列表数据
//        Map<String, Set<String>> specList = searchSpecList(nativeSearchQueryBuilder, resultMap.get总条数);
        Map<String, Set<String>> specList = searchSpecList(nativeSearchQueryBuilder,size);
        resultMap.put("specList", specList);
//        elasticsearchTemplate.queryForPage(arg0, arg1);// arg0:封装查询条件的  arg1:指定响应的数据类型
        // 3、返回查询的结果
        return resultMap;
    }
   /* //统计商品价格列表数据
    private List<String> searchPriceList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 添加分组条件（聚合条件）
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuPrice").field("price"));
        // 开始查询
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 处理结果集：获取分组的数据
        Aggregations aggregations = page.getAggregations();
        StringTerms stringTerms = aggregations.get("skuPrice");
        List<String> priceList = new ArrayList<>();
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String priceName = bucket.getKeyAsString();
            priceList.add(priceName);
        }

        return priceList;

    }*/

    //统计商品规格列表数据
    private Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder,int size) {
        // 设置聚合条件（分组条件）
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));//size(10000)
        // 根据条件查询
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 处理结果集
        Aggregations aggregations = page.getAggregations();
        StringTerms stringTerms = aggregations.get("skuSpec");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        // list数据格式：
        // {"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}，
        // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}，
        // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}，
        // 对数据进一步处理
        Map<String, Set<String>> map = pullMap(list);
        return map;
    }

    //将specList数据组合成map
    private Map<String, Set<String>> pullMap(List<String> specList) {
        Map<String, Set<String>> map = new HashMap<>();
        for (String spec : specList) {
            // 每遍历一次，获取到的数据格式为
            // {"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
            // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}
            // 第二条加入到map后：{"电视音响效果":{"小影院","立体声"},"电视屏幕尺寸":"20英寸","尺码":{"165","170"}}
            // {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"170"}
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();        // 规格名称
                String value = entry.getValue();    // 规格选项

                Set<String> set = map.get(key);
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(value);
                map.put(key, set);
            }
        }

        return map;
    }

    // 统计商品品牌列表数据
    private List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 添加分组条件（聚合条件）
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        // 开始查询
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 处理结果集：获取分组的数据
        Aggregations aggregations = page.getAggregations();
        StringTerms stringTerms = aggregations.get("skuBrand");
        List<String> brandList = new ArrayList<>();
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            brandList.add(brandName);
        }

        return brandList;
    }

    // 统计商品分类列表数据
    private List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 添加分组条件（聚合条件）
        // SELECT category_name skuCategory FROM tb_sku WHERE NAME LIKE '%黑马%' GROUP BY category_name
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        // 开始查询
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        // 处理结果集：获取分组的数据
        Aggregations aggregations = page.getAggregations();
        StringTerms stringTerms = aggregations.get("skuCategory");
        List<String> categoryList = new ArrayList<>();
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }

        return categoryList;
    }

    // 根据条件查询并且封装数据
    private Map<String, Object> searchForPage(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        //        // 根据条件查询
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        // 对检索关键字进行高亮（条件）
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");// 对哪个字段中包含的关键字进行高亮
        field.preTags("<font color='red'>");    // 开始标签
        field.postTags("</font>");              // 结束标签
        nativeSearchQueryBuilder.withHighlightFields(field);
        // AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQuery, SkuInfo.class);

        // 对检索的关键字进行高亮
        SearchResultMapper searchResultMapper = new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<T> content = new ArrayList<>();
                // SearchResponse：获取到的结果数据
                SearchHits hits = response.getHits();
                if (hits != null) {
                    for (SearchHit hit : hits) {
                        // 获取普通结果数据（json格式）   {name:苹果手机，price:500，内存：32G}
                        String result = hit.getSourceAsString();
                        SkuInfo skuInfo = JSON.parseObject(result, SkuInfo.class);

                        HighlightField highlightField = hit.getHighlightFields().get("name");
                        if (highlightField != null) {
                            Text[] texts = highlightField.getFragments();
                            // 替换到普通的名称
                            skuInfo.setName(texts[0].toString());
                        }
                        content.add((T) skuInfo);
                    }
                }
                return new AggregatedPageImpl<>(content, pageable, hits.getTotalHits());
            }
        };
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, searchResultMapper);
        // 处理结果集
        List<SkuInfo> rows = page.getContent();//商品列表数据
        long totalElements = page.getTotalElements();// 总条数
        int totalPages = page.getTotalPages();// 总页数
        // 封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("rows", rows);
        map.put("totalElements", totalElements);
        map.put("totalPages", totalPages);
        // 前端分页对象page需要当前页码、每页显示的条数
        Pageable pageable = nativeSearchQuery.getPageable();    // 服务器端的分页对象
        int currentpage = pageable.getPageNumber() + 1;             // pageable：当前页码从0开始计算的
        int pagesize = pageable.getPageSize();
        map.put("currentpage", currentpage);
        map.put("pagesize", pagesize);
        return map;
    }

    // 封装检索条件
    private NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 通过nativeSearchQueryBuilder封装检索条件
        if (searchMap != null) {
            // 1、根据关键字检索
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }
            //  AND category_name = ?  AND brand_name = ?
            // 2、根据商品分类名称进行过滤
            String category = searchMap.get("category");
            if (!StringUtils.isEmpty(category)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("categoryName", category));
            }
            // 3、根据商品品牌名称进行过滤
            String brand = searchMap.get("brand");
            if (!StringUtils.isEmpty(brand)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("brandName", brand));
            }
            // 4、根据商品的规格进行过滤
            // url： http://localhost:8080/search?keyword=黑马&category=手机&brand=华为&spce_内存=32G
            Set<String> keys = searchMap.keySet();
            for (String key : keys) {
                if (key.startsWith("spec_")) { // 规格
                    boolQueryBuilder.must(QueryBuilders.matchQuery("specMap." + key.substring(5) + ".keyword", searchMap.get(key)));
                }
            }
            // 5、根据价格区间段过滤
            String price = searchMap.get("price"); // 价格：500-1000   3000
            if (!StringUtils.isEmpty(price)) {
                String[] prices = price.split("-");
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(prices[0]));
                if (prices.length == 2) { // 价格区间段
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(prices[1]));
                }
            }
            // 6、根据商品销量、评论数、新品、价格等进行排序（升序/降序）
            // // url： http://localhost:8080/search?keyword=黑马&sortField=price&sortRule=DESC
            String sortField = searchMap.get("sortField");  // 排序字段
            String sortRule = searchMap.get("sortRule");    // 排序规则
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
//                if ("ASC".equals(sortRule)){
//                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
//                }
//                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
                // sortRule：区分大小写  ASC/DESC 不支持小写的
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }

        }
        // 封装过滤条件
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        // 类似sql语句：select * from table where name like ? and brand = ? and ... order by id desc  limit 0, 10
        // 结果分页
        String pageNum = searchMap.get("pageNum");
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = "1";  // 默认第一页
        }
        int page = Integer.valueOf(pageNum) - 1; // page：当前页 从0开始
        int size = 3;
        Pageable pageable = PageRequest.of(page, size); // page：当前页  size：每页显示的条数
        nativeSearchQueryBuilder.withPageable(pageable);
        return nativeSearchQueryBuilder;
    }
}
