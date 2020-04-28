package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author hongchen
 * @date 2020/4/10 23:26
 */
public interface SkuInfoMapper extends ElasticsearchRepository<SkuInfo,Integer> {
}
