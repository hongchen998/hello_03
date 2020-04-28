package com.changgou.search.service;

import java.util.Map;

/**
 * @author hongchen
 * @date 2020/4/10 23:27
 */
public interface SkuInfoService {
    /**
     * 将数据库数据导入索引库中
     */
    void importSkuInfoEs();

    /**
     * 检索
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,String> searchMap);
}
