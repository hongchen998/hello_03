package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:传智播客
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Autowired(required = false)
    private CartService cartService;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private IdWorker idWorker;

    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired(required = false)
    private UserFeign userFeign;

    /**
     * @param username
     * @return java.util.List<com.changgou.order.pojo.Order>
     * @author hongchen
     * @Description 通过用户名查找订单列表
     * @Date 12:09 2020/4/29
     **/
    @Override
    public List<Order> findByUsername(String username) {
        List<Order> list = orderMapper.findByUsername(username);
        return list;
    }

    /**
     * @param orderId
     * @return void
     * @author hongchen
     * @Description 支付失败，删除订单（更新订单）
     * @Date 23:19 2020/4/19
     **/
    @Override
    public void deleteOrder(String orderId) {
        //从redis中获取订单对象
        Order order = (Order) redisTemplate.boundHashOps("Order").get(orderId);

        //从数据库中查询订单
        if (order == null) {
            order = orderMapper.selectByPrimaryKey(orderId);
        }

        //更新订单状态
        order.setUpdateTime(new Date());     // 订单的更新日期
        order.setPayStatus("2");            // 支付状态：支付失败
        order.setIsDelete("1");             // 订单是否删除：0，未删除  1，已删除

        orderMapper.updateByPrimaryKeySelective(order);

        //更新完成后，删除redis中订单信息
        redisTemplate.boundHashOps("Order").delete(orderId);

    }

    /**
     * @param orderId       订单id
     * @param transactionId 第三方生成的交易流水号
     * @return void
     * @author hongchen
     * @Description 支付成功，更新订单状态
     * @Date 23:09 2020/4/19
     **/
    @Override
    public void updateStatus(String orderId, String transactionId) {
        //获取订单
        Order order = (Order) redisTemplate.boundHashOps("Order").get(orderId);
        if (order == null) {
            // 从数据库中查询订单
            order = orderMapper.selectByPrimaryKey(orderId);
        }
        if (order != null) {
            //更新订单状态
            order.setTransactionId(transactionId);   // 交易流水号
            order.setUpdateTime(new Date());         // 订单的更新日期
            order.setPayStatus("1");                // 支付状态：已支付
            order.setPayTime(new Date());           // 支付完成日期
            orderMapper.updateByPrimaryKeySelective(order);
            //更新完成后，删除redis中订单信息
            redisTemplate.boundHashOps("Order").delete(orderId);
        }
    }

    /**
     * Order条件+分页查询
     *
     * @param order 查询条件
     * @param page  页码
     * @param size  页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Order> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     *
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order) {
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     *
     * @param order
     * @return
     */
    public Example createExample(Order order) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (order != null) {
            // 订单id
            if (!StringUtils.isEmpty(order.getId())) {
                criteria.andEqualTo("id", order.getId());
            }
            // 数量合计
            if (!StringUtils.isEmpty(order.getTotalNum())) {
                criteria.andEqualTo("totalNum", order.getTotalNum());
            }
            // 金额合计
            if (!StringUtils.isEmpty(order.getTotalMoney())) {
                criteria.andEqualTo("totalMoney", order.getTotalMoney());
            }
            // 优惠金额
            if (!StringUtils.isEmpty(order.getPreMoney())) {
                criteria.andEqualTo("preMoney", order.getPreMoney());
            }
            // 邮费
            if (!StringUtils.isEmpty(order.getPostFee())) {
                criteria.andEqualTo("postFee", order.getPostFee());
            }
            // 实付金额
            if (!StringUtils.isEmpty(order.getPayMoney())) {
                criteria.andEqualTo("payMoney", order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if (!StringUtils.isEmpty(order.getPayType())) {
                criteria.andEqualTo("payType", order.getPayType());
            }
            // 订单创建时间
            if (!StringUtils.isEmpty(order.getCreateTime())) {
                criteria.andEqualTo("createTime", order.getCreateTime());
            }
            // 订单更新时间
            if (!StringUtils.isEmpty(order.getUpdateTime())) {
                criteria.andEqualTo("updateTime", order.getUpdateTime());
            }
            // 付款时间
            if (!StringUtils.isEmpty(order.getPayTime())) {
                criteria.andEqualTo("payTime", order.getPayTime());
            }
            // 发货时间
            if (!StringUtils.isEmpty(order.getConsignTime())) {
                criteria.andEqualTo("consignTime", order.getConsignTime());
            }
            // 交易完成时间
            if (!StringUtils.isEmpty(order.getEndTime())) {
                criteria.andEqualTo("endTime", order.getEndTime());
            }
            // 交易关闭时间
            if (!StringUtils.isEmpty(order.getCloseTime())) {
                criteria.andEqualTo("closeTime", order.getCloseTime());
            }
            // 物流名称
            if (!StringUtils.isEmpty(order.getShippingName())) {
                criteria.andEqualTo("shippingName", order.getShippingName());
            }
            // 物流单号
            if (!StringUtils.isEmpty(order.getShippingCode())) {
                criteria.andEqualTo("shippingCode", order.getShippingCode());
            }
            // 用户名称
            if (!StringUtils.isEmpty(order.getUsername())) {
                criteria.andLike("username", "%" + order.getUsername() + "%");
            }
            // 买家留言
            if (!StringUtils.isEmpty(order.getBuyerMessage())) {
                criteria.andEqualTo("buyerMessage", order.getBuyerMessage());
            }
            // 是否评价
            if (!StringUtils.isEmpty(order.getBuyerRate())) {
                criteria.andEqualTo("buyerRate", order.getBuyerRate());
            }
            // 收货人
            if (!StringUtils.isEmpty(order.getReceiverContact())) {
                criteria.andEqualTo("receiverContact", order.getReceiverContact());
            }
            // 收货人手机
            if (!StringUtils.isEmpty(order.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", order.getReceiverMobile());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(order.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (!StringUtils.isEmpty(order.getSourceType())) {
                criteria.andEqualTo("sourceType", order.getSourceType());
            }
            // 交易流水号
            if (!StringUtils.isEmpty(order.getTransactionId())) {
                criteria.andEqualTo("transactionId", order.getTransactionId());
            }
            // 订单状态,0:未完成,1:已完成，2：已退货
            if (!StringUtils.isEmpty(order.getOrderStatus())) {
                criteria.andEqualTo("orderStatus", order.getOrderStatus());
            }
            // 支付状态,0:未支付，1：已支付，2：支付失败
            if (!StringUtils.isEmpty(order.getPayStatus())) {
                criteria.andEqualTo("payStatus", order.getPayStatus());
            }
            // 发货状态,0:未发货，1：已发货，2：已收货
            if (!StringUtils.isEmpty(order.getConsignStatus())) {
                criteria.andEqualTo("consignStatus", order.getConsignStatus());
            }
            // 是否删除
            if (!StringUtils.isEmpty(order.getIsDelete())) {
                criteria.andEqualTo("isDelete", order.getIsDelete());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 提交订单
     *
     * @param order
     */
    @Override
    public void add(Order order) {
        //获取购物车信息
        List<OrderItem> list = cartService.list(order.getUsername());

        //保存订单
        long id = idWorker.nextId();     // 订单id
        order.setId(String.valueOf(id));
        int totalNum = 0;       // 商品总数量
        int totalMoney = 0;     // 商品总金额
        int payMoney = 0;       // 商品实际支付金额
        for (OrderItem orderItem : list) {
            totalNum += orderItem.getNum();
            totalMoney += orderItem.getMoney();
            payMoney += orderItem.getPayMoney();
        }
        order.setTotalNum(totalNum);
        order.setTotalMoney(totalMoney);
        order.setPayMoney(payMoney);
        order.setCreateTime(new Date());    // 订单创建日期
        order.setUpdateTime(new Date());    // 订单更新日期
        order.setBuyerRate("0");            // 是否评价  0：未评价  1：已评价
        order.setSourceType("1");           // 订单来源
        order.setOrderStatus("0");          // 订单状态：未完成
        order.setPayStatus("0");            // 订单支付状态：未支付
        order.setConsignStatus("0");        // 订单发货状态：未发货
        order.setIsDelete("0");             // 订单是否删除：0：未删除  1：已删除

        orderMapper.insertSelective(order); // 保存订单

        //保存订单明细
        for (OrderItem orderItem : list) {
            orderItem.setId(String.valueOf(idWorker.nextId()));
            orderItem.setOrderId(order.getId());

            orderItemMapper.insertSelective(orderItem);
        }
        //扣减库存
        skuFeign.decr(order.getUsername());

        //增加用户积分
        userFeign.incrUserPoints(order.getUsername(), 10);

        //提交订单成功后，将订单保存到redis中， 在线支付：保存redis 线下支付（货到付款）:暂不考虑
        if ("1".equals(order.getPayType())) {
            redisTemplate.boundHashOps("Order").put(order.getId(), order);
        }

        //删除购物车 应该删除已下单的商品
        //redisTemplate.boundHashOps("cart_" + order.getUsername()).delete(sku_id);
        redisTemplate.delete("cart_" + order.getUsername());

    }

    /**
     * 根据ID查询Order
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }
}
