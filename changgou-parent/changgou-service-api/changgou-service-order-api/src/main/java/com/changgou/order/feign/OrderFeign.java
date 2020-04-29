package com.changgou.order.feign;

import com.changgou.order.pojo.Order;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/29 11:16
 */
@FeignClient(name = "order")
@RequestMapping("/order")
public interface OrderFeign {
    /**
     * @param username
     * @return java.util.List<com.changgou.order.pojo.Order>
     * @author hongchen
     * @Description 通过用户名查找订单列表
     * @Date 12:09 2020/4/29
     **/
    @GetMapping("/findByUsername/{username}")
    Result<List<Order>> findByUsername(@PathVariable(value = "username") String username);
}
