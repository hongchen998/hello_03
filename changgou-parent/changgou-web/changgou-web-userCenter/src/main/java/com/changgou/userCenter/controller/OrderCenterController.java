package com.changgou.userCenter.controller;/**
 * @author hongchen
 * @date 2020/4/28 16:56
 */

import com.changgou.order.feign.OrderFeign;
import com.changgou.order.feign.OrderItemFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.userCenter.pojo.OrderInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName OrderCenter
 * @Description 订单中心
 * @Author hongchen
 * @Date 16:56 2020/4/28
 * @Version 2.1
 **/
@Controller
@RequestMapping("/orderCenter")
public class OrderCenterController {
    @Autowired(required = false)
    private OrderItemFeign orderItemFeign;

    @Autowired(required = false)
    private OrderFeign orderFeign;

    @RequestMapping("/myOrder")
    public String userCenter() {
        return "myOrder";
    }

    /**
     * @return entity.Result<java.util.List       <       com.changgou.userCenter.pojo.OrderInfo>>
     * @author hongchen
     * @Description 获取获取用户订单列表
     * @Date 23:28 2020/4/28
     **/
    @GetMapping("/userOrderList")
    public Result<OrderInfo> userOrderList() {
        //获取cookie中的用户名
        String username = "szitheima";
        OrderInfo orderInfo = new OrderInfo();
        //根据用户名从order表查询订单列表
        List<Order> orderList = orderFeign.findByUsername(username).getData();
        if (orderList != null && orderList.size() > 0) {

            //讲订单列表封装到orderInfo中
            orderInfo.setOrderList(orderList);
            for (Order order : orderList) {
                String orderId = order.getId();
                //根据订单编号查询订单明细列表
                List<OrderItem> orderItemList = orderItemFeign.findByOrderId(orderId).getData();
                if (orderItemList != null && orderItemList.size() > 0) {
                    //讲订单明细列表封装到orderInfo中
                    orderInfo.setOrderItemList(orderItemList);
                }
            }
        }
        return new Result(true, StatusCode.OK, "获取当前商品详情数据成功", orderInfo);
    }
}
