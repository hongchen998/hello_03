package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hongchen
 * @date 2020/4/17 15:42
 */
@FeignClient(name = "goods")
@RequestMapping("/spu")
public interface SpuFeign {
    /**
     * @author hongchen
     * @Description 根据SpuID查询Spu信息
     * @Date 15:55 2020/4/17
     * @param id
     * @return entity.Result<com.changgou.goods.pojo.Spu>
     **/
    @GetMapping("/{id}")
    Result<Spu> findById(@PathVariable(value = "id") Long id);
}
