package com.changgou.seckill.service.impl;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.threading.MultiThreadingSubmitOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/****
 * @Author:传智播客
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired(required = false)
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private IdWorker idWorker;

    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private MultiThreadingSubmitOrder multiThreadingSubmitOrder;

    /**
     * @param userId
     * @return com.changgou.seckill.pojo.SeckillStatus
     * @author hongchen
     * @Description 查询抢购的订单状态
     * @Date 2:59 2020/5/24
     **/
    @Override
    public SeckillStatus queryStatus(String userId) {
        return (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(userId);
    }

    /**
     * @param time      当前时间段
     * @param seckillId 商品id
     * @param userId    用户名
     * @return boolean  抢单是否成功
     * @author hongchen
     * @Description 用户下单
     * @Date 1:05 2020/5/24
     **/
    @Override
    public boolean add(String time, Long seckillId, String userId) {
        //记录用户下单信息（登记）
        Long increment = redisTemplate.boundHashOps("UserQueueCount" + seckillId).increment(userId, 1);
        //说明之前已经下单
        if (increment > 1) {
            throw new RuntimeException("对不起，您不能重复下单。。。");
        }

        //封装用户得抢单信息
        SeckillStatus seckillStatus = new SeckillStatus(userId, new Date(), 1, seckillId, time);
        redisTemplate.boundListOps("SeckillOrderStatus").leftPush(seckillStatus);

        //将用户的抢单信息储存到redis中
        redisTemplate.boundHashOps("UserQueueStatus").put(userId, seckillStatus);

        // 多线程下单
        multiThreadingSubmitOrder.createOrder();

        // TODO 后期有其他的业务该如何处理就怎么处理

        return true;
    }

    /**
     * SeckillOrder条件+分页查询
     *
     * @param seckillOrder 查询条件
     * @param page         页码
     * @param size         页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     *
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder) {
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     *
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder) {
        Example example = new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (seckillOrder != null) {
            // 主键
            if (!StringUtils.isEmpty(seckillOrder.getId())) {
                criteria.andEqualTo("id", seckillOrder.getId());
            }
            // 秒杀商品ID
            if (!StringUtils.isEmpty(seckillOrder.getSeckillId())) {
                criteria.andEqualTo("seckillId", seckillOrder.getSeckillId());
            }
            // 支付金额
            if (!StringUtils.isEmpty(seckillOrder.getMoney())) {
                criteria.andEqualTo("money", seckillOrder.getMoney());
            }
            // 用户
            if (!StringUtils.isEmpty(seckillOrder.getUserId())) {
                criteria.andEqualTo("userId", seckillOrder.getUserId());
            }
            // 创建时间
            if (!StringUtils.isEmpty(seckillOrder.getCreateTime())) {
                criteria.andEqualTo("createTime", seckillOrder.getCreateTime());
            }
            // 支付时间
            if (!StringUtils.isEmpty(seckillOrder.getPayTime())) {
                criteria.andEqualTo("payTime", seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if (!StringUtils.isEmpty(seckillOrder.getStatus())) {
                criteria.andEqualTo("status", seckillOrder.getStatus());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(seckillOrder.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if (!StringUtils.isEmpty(seckillOrder.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", seckillOrder.getReceiverMobile());
            }
            // 收货人
            if (!StringUtils.isEmpty(seckillOrder.getReceiver())) {
                criteria.andEqualTo("receiver", seckillOrder.getReceiver());
            }
            // 交易流水
            if (!StringUtils.isEmpty(seckillOrder.getTransactionId())) {
                criteria.andEqualTo("transactionId", seckillOrder.getTransactionId());
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
    public void delete(Long id) {
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }


    /**
     * @param out_trade_no   订单号
     * @param transaction_id 交易流水号
     * @param username       用户名
     * @param time_end       支付完成时间
     * @return void
     * @author hongchen
     * @Description 支付成功，更新订单状态
     * @Date 2:24 2020/5/24
     **/
    @Override
    public void updateStatus(String out_trade_no, String transaction_id, String username, String time_end) throws ParseException {
        // 取出订单（redis中）
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null) {
            // 更新订单数据并同步到MySQL中
            seckillOrder.setTransactionId(transaction_id);  // 交易流水号
            seckillOrder.setStatus("1");                    // 支付状态
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date payTime = sdf.parse(time_end);
            seckillOrder.setPayTime(payTime);              // 支付完成日期
            // 同步MySQL
            seckillOrderMapper.insertSelective(seckillOrder);

            // 删除redis中的订单信息、排队信息、重复下单信息
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
            redisTemplate.boundHashOps("UserQueueCount" + seckillOrder.getSeckillId()).delete(username);
        }

    }


    /**
     * @param username 用户名
     * @return void
     * @author hongchen
     * @Description 支付支付失败，删除订单
     * @Date 2:24 2020/5/24
     **/
    @Override
    public void deleteOrder(String username) {
        // 取出订单的状态信息
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        // 取出订单（redis中）
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null && seckillStatus != null) {
            // 1、删除订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            // 2、获取到了商品信息
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillOrder.getSeckillId());
            if (seckillGoods == null) {
                // 2-1、将商品的id重新加入到队列中
                redisTemplate.boundListOps("SeckillGoodsList_" + seckillOrder.getSeckillId()).leftPush(seckillOrder.getSeckillId());
                // 2-2、回滚商品的库存
                Long stockCount = redisTemplate.boundHashOps("SecillGoodsCount").increment(seckillGoods.getId(), 1);
                seckillGoods.setStockCount(stockCount.intValue());
                redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillGoods.getId(), seckillGoods);
                // 之前完成下单：库存只有一件，减了一件    将数据同步到了MySQL中（现在库存0件）
                // 如果stockCount = 1
                if (stockCount == 1) {
                    // 将数据同步到MySQL中
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                }
            }

            // 3、删除排队信息、重复下单信息
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
            redisTemplate.boundHashOps("UserQueueCount" + seckillOrder.getSeckillId()).delete(username);
        }
    }
}
