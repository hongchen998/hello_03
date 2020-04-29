package com.changgou.order.feign;

import com.changgou.order.pojo.OrderItem;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/29 12:27
 */
@FeignClient(name = "order")
@RequestMapping("/orderItem")
public interface OrderItemFeign {
    /**
     * @param orderId
     * @return java.util.List<com.changgou.order.pojo.OrderItem>
     * @author hongchen
     * @Description 根据订单id查询订单详情列表
     * @Date 12:33 2020/4/29
     **/
    @GetMapping("/findByOrderId/{orderId}")
    Result<List<OrderItem>> findByOrderId(@PathVariable(value = "orderId") String orderId);
}
