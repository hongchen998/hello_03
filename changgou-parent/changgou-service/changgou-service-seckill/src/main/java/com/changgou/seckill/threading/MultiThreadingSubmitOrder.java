package com.changgou.seckill.threading;/**
 * @author hongchen
 * @date 2020/5/24 2:13
 */

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName MultiThreadingSubmitOrder
 * @Description
 * @Author hongchen
 * @Date 2:13 2020/5/24
 * @Version 2.1
 **/
@Component
public class MultiThreadingSubmitOrder {
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * @author hongchen
     * @Description 用户下单
     * @Date 1:05 2020/5/24
     **/
    public void createOrder() {

        try {
            //从队列中取出排队信息
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderStatus").rightPop();

            String time = seckillStatus.getTime();
            Long seckillId = seckillStatus.getGoodsId();
            String userId = seckillStatus.getUsername();

            //从redis队列中弹出一个商品（id）
            Object goodId = redisTemplate.boundListOps("SeckillGoodsList_" + seckillId).rightPop();
            if (goodId == null) {
                //该商品已售完
                //删除用户的抢单状态信息
                redisTemplate.boundHashOps("UserQueueStatus").delete(userId);
                //删除用户重复排队信息
                redisTemplate.boundHashOps("UserQueueCount" + seckillId).delete(userId);
            }
            //下单：购买商品，判断redis中是否有无该商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(seckillId);
            if (seckillGoods == null) {
                throw new RuntimeException("对不起，该商品已售罄");
            }
            // 该商品还有货：可以下单了
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSeckillId(seckillId);   // 商品id
            seckillOrder.setMoney(seckillGoods.getCostPrice());// 秒杀价
            seckillOrder.setUserId(userId);            // 当前用户
            seckillOrder.setCreateTime(new Date());     // 下单时间
            seckillOrder.setStatus("0");                // 支付状态：未支付

            //保存订单：存储一份到redis中，后期付款完成后再同步到mysql
            redisTemplate.boundHashOps("SeckillOrder").put(userId, seckillOrder);

            //扣减商品的库存（默认买一件） 场景一：10 场景二：1
            //Integer stockCount = seckillGoods.getStockCount() - 1;
            Long stockCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillId, -1);

            seckillGoods.setStockCount(stockCount.intValue());

            //判断库存量
            if (stockCount <= 0) {// 最后一件刚刚卖完
                //删除redis中得商品
                redisTemplate.boundHashOps("SeckillGoods_" + time).delete(seckillId);
                //将该商品同步到mysql中
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            } else {
                //更新商品得库存、
                redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillId, seckillGoods);

            }

            //如果抢单成功，需要更新订单状态
            seckillStatus.setStatus(2);     // 待支付
            seckillStatus.setOrderId(seckillOrder.getId());// 订单id
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney()));// 订单金额
            redisTemplate.boundHashOps("UserQueueStatus").put(userId, seckillStatus);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
