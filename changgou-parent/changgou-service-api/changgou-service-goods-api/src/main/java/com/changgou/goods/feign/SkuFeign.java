package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/10 23:11
 */
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /**
     * @author hongchen
     * @Description 将正常状态的库存信息保存到索引库
     * @Date 15:39 2020/4/17
     * @param status
     * @return entity.Result<java.util.List<com.changgou.goods.pojo.Sku>>
     **/
    @GetMapping("/findSkusByStatus/{status}")
    Result<List<Sku>> findSkusByStatus(@PathVariable(value = "status") String status);

/**
 * @author hongchen
 * @Description 获取库存信息
 * @Date 15:40 2020/4/17
 * @param id
 * @return entity.Result<com.changgou.goods.pojo.Sku>
 **/
    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable(value = "id") Long id);
/**
 * @author hongchen
 * @Description 减库存操作
 * @Date 0:55 2020/4/19
 * @param username
 * @return entity.Result
 **/
    @GetMapping("/decr/{username}")
    Result decr(@PathVariable(value = "username", required = true) String username);
}
