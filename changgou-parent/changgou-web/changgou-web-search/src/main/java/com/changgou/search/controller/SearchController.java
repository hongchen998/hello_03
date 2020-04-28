package com.changgou.search.controller;/**
 * @author hongchen
 * @date 2020/4/13 11:21
 * @ClassName SearchController
 * @Description 搜索页面控制层
 * @Author hongchen
 * @Date 11:21 2020/4/13
 * @Version 2.1
 * @ClassName SearchController
 * @Description 搜索页面控制层
 * @Author hongchen
 * @Date 11:21 2020/4/13
 * @Version 2.1
 **/

/**
 *@ClassName SearchController
 *@Description 搜索页面控制层
 *@Author hongchen
 *@Date 11:21 2020/4/13
 *@Version 2.1
 **/

import com.changgou.search.feign.SkuInfoFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired(required = false)
    private SkuInfoFeign skuInfoFeign;

    /*/**
     * @author hongchen
     * @Description 检索页面
     * @Date 11:24 2020/4/13
     * @Param  * @param
     * @return java.lang.String
     **/
    @GetMapping("/list")
    public String list(@RequestParam(required = false) Map<String, String> searchMap, Model model) {
        //调用搜索服务

        Map<String, Object> resultMap = skuInfoFeign.search(searchMap);

        //将检索条件和结果绑定到Model域中，用于页面回显
        model.addAttribute("searchMap", searchMap);
        model.addAttribute("resultMap", resultMap);
        //组装url
        String url = getUrl(searchMap);
        model.addAttribute("url", url);

        // 分页对象 page
        // 总条数
        String totalElements = resultMap.get("totalElements").toString();
        long total = Long.parseLong(totalElements);
        // 当前页码
        int currentpage = Integer.parseInt(resultMap.get("currentpage").toString());
        // 每页显示的条数
        int pagesize = Integer.parseInt(resultMap.get("pagesize").toString());
        Page<SkuInfo> page = new Page<>(total, currentpage, pagesize);
        model.addAttribute("page", page);

        //返回搜索页面
        return "search";
    }

    //组装Url
    private String getUrl(Map<String, String> searchMap) {
        // 例：/search/list?keywords=黑马&brand=小米
        // 默认的url
        String url = "/search/list";
        // 拼接url
        if (searchMap != null && searchMap.size() > 0) {
            url += "?";
            // 例如有关键字：http://localhost:18086/search/list?keywords=黑马
            // 有品牌：http://localhost:18086/search/list?keywords=黑马&brand=小米
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();// keywords  brand
                String value = entry.getValue();// 黑马       小米
                // 处理当前页码
                if ("pageNum".equals(key)) {
                    continue;
                }
                url += key + "=" + value + "&";
            }
            // http://localhost:18086/search/list?keywords=黑马&brand=小米&
            url = url.substring(0, url.length() - 1); // 去掉最后一个&

        }
        return url;
    }
}
