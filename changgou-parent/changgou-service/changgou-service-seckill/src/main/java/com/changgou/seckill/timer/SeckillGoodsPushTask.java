package com.changgou.seckill.timer;/**
 * @author hongchen
 * @date 2020/5/24 23:31
 */

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SeckillGoodsPushTask
 * @Description 定时任务测试类
 * @Author hongchen
 * @Date 23:31 2020/5/24
 * @Version 2.1
 **/
@Component
public class SeckillGoodsPushTask {
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param
     * @return void
     * @author hongchen
     * @Description cron：时间表达式，该例子：每5s执行一次
     * @Date 23:32 2020/5/24
     **/
    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoodsToRedis() {
        //System.out.println("每5秒执行一次");

        //获取当前时间后的5个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            String key_rule = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
            //System.out.println(key_rule);
            //查询数据库中满足条件的秒杀商品列表数据
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //条件一：大于开始时间，小于结束时间
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu);
            criteria.andLessThanOrEqualTo("endTime", DateUtil.addDateHour(dateMenu, 2));
            //条件二：商品是审核通过的
            criteria.andEqualTo("status", 1);
            //条件三：库存大于0
            criteria.andGreaterThan("stockCount", 0);

            // 第二次以及后面查询中，无需查询重复的数据
            // select * from table where start_time > ? and end_time < ? and status = 1 and stock_content >  0 and id not in(001,002,003)
            // 获取Redis中的商品id
            Set ids = redisTemplate.boundHashOps("SeckillGoods_" + key_rule).keys();
            if (ids != null && ids.size() > 0) {
                criteria.andNotIn("id", ids);
            }
            List<SeckillGoods> list = seckillGoodsMapper.selectByExample(example);
            //System.out.println("list.size" + list.size());
            //将查询到的数据写入redis中
            if (list != null && list.size() > 0) {
                for (SeckillGoods seckillGoods : list) {
                    // 原生命令  hset  key  filed  value
                    redisTemplate.boundHashOps("SeckillGoods_" + key_rule).put(seckillGoods.getId(), seckillGoods);

                    //将商品的id放入到redis队列当中
                    Long[] idArray = pushId(seckillGoods.getStockCount(), seckillGoods.getId());
                    redisTemplate.boundListOps("SeckillGoodsList_"+seckillGoods.getId()).leftPushAll(idArray);
                    //将商品库存量存储到redis中
                    redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGoods.getId(),seckillGoods.getStockCount());
                }
            }
        }


    }

    //将商品id封装到数组中
    private Long[] pushId(Integer stockCount, Long id) {
        Long[] ids = new Long[stockCount];
        for (int i = 0; i < stockCount; i++) {
            ids[i] = id;
        }
        return ids;
    }

}
