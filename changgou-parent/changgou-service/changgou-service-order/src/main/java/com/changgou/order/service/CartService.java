package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/17 16:17
 */
public interface CartService {
    /**
     * @author hongchen
     * @Description商品加入购物车
     * @Date 16:17 2020/4/17
     * @param num 购买数量
     * @param id 库存id
     * @param username 当前用户
     * @return void
     **/
    void add(Integer num,long id, String username);
    /**
     * @author hongchen
     * @Description 查询购物车列表
     * @Date 18:31 2020/4/17
     * @param username
     * @return java.util.List<com.changgou.order.pojo.OrderItem>
     **/
    List<OrderItem> list(String username);
}
