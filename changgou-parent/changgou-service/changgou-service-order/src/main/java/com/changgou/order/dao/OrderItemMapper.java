package com.changgou.order.dao;
import com.changgou.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:OrderItem的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface OrderItemMapper extends Mapper<OrderItem> {
    /**
     * @param orderId
     * @return java.util.List<com.changgou.order.pojo.OrderItem>
     * @author hongchen
     * @Description 根据订单id查询订单详情列表
     * @Date 12:33 2020/4/29
     **/
    @Select("SELECT * FROM tb_order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(@Param("orderId") String orderId);
}
