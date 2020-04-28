package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.github.pagehelper.PageInfo;

import java.text.ParseException;
import java.util.List;

/****
 * @Author:传智播客
 * @Description:SeckillOrder业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SeckillOrderService {
    /**
     * @param userId
     * @return com.changgou.seckill.pojo.SeckillStatus
     * @author hongchen
     * @Description 查询抢购的订单状态
     * @Date 2:59 2020/5/24
     **/
    SeckillStatus queryStatus(String userId);

    /**
     * @param time      秒杀商品redis中的key当前时间段
     * @param seckillId 秒杀商品的id
     * @param userId    用户名
     * @return boolean
     * @author hongchen
     * @Description 下单操作
     * @Date 1:03 2020/5/24
     **/
    boolean add(String time, Long seckillId, String userId);

    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /***
     * 新增SeckillOrder
     * @param seckillOrder
     */
    void add(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();

    /**
    * @author hongchen
    * @Description 支付成功，更新订单状态
    * @Date 2:24 2020/5/24
    * @param out_trade_no      订单号
    * @param transaction_id    交易流水号
    * @param username          用户名
    * @param time_end          支付完成时间
    * @return void
    **/
    void updateStatus(String out_trade_no, String transaction_id, String username, String time_end) throws ParseException;
    /**
     * @author hongchen
     * @Description 支付支付失败，删除订单
     * @Date 2:24 2020/5/24
     * @param username          用户名
     * @return void
     **/
    void deleteOrder(String username);
}
