package com.changgou.order.dao;
import com.changgou.order.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:传智播客
 * @Description:Order的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface OrderMapper extends Mapper<Order> {
    /**
     * @author hongchen
     * @Description 通过用户名查找订单列表
     * @Date 12:09 2020/4/29
     * @param username
     * @return java.util.List<com.changgou.order.pojo.Order>
     **/
    @Select("select * from tb_order where username = #{username}")
    List<Order> findByUsername(@Param("username") String username);
}
