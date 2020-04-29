package com.changgou.userCenter.pojo;/**
 * @author hongchen
 * @date 2020/4/28 23:02
 */

import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 *@ClassName OrderInfo
 *@Description 用户订单
 *@Author hongchen
 *@Date 23:02 2020/4/28
 *@Version 2.1
 **/
public class OrderInfo implements Serializable {
    private List<Order> orderList;//订单列表

    private List<OrderItem> orderItemList;//订单明细列表

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
