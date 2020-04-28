package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hongchen
 * @date 2020/4/13 15:21
 */
@FeignClient(name = "search")
@RequestMapping("/search")
public interface SkuInfoFeign {
    /*/**
     * @author hongchen
     * @Description检索
     * @Date 15:22 2020/4/13
     * @Param  * @param searchMap
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    @GetMapping
    Map<String,Object> search(@RequestParam(required = false) Map<String,String> searchMap);
}
