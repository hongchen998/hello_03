package com.changgou.search.controller;

import com.changgou.goods.pojo.Brand;
import com.changgou.search.service.SkuInfoService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hongchen
 * @date 2020/4/10 23:40
 */
@RestController
@RequestMapping("/search")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;
    @GetMapping("/import")
    public Result importData(){
        skuInfoService.importSkuInfoEs();
        return new Result(true, StatusCode.OK, "数据导入成功");
    }

    /**
     * 检索
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map<String,Object> search(@RequestParam(required = false) Map<String,String> searchMap){
        Map<String, Object> resultMap = skuInfoService.search(searchMap);
        return resultMap;
    }
}
