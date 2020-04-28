package com.changgou.seckill.listener;
/**
 * @author hongchen
 * @date 2020/5/24 2:15
 */

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

/**
 *@ClassName SeckillOrderListener
 *@Description 秒杀订单的监听器
 *@Author hongchen
 *@Date 2:15 2020/5/24
 *@Version 2.1
 **/
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderListener {
    @Autowired
    private SeckillOrderService seckillOrderService;


    /**
     * @author hongchen
     * @Description 监听队列获取消息
     * @Date 2:19 2020/5/24
     * @param text  消息体
     * @return void
     **/
    @RabbitHandler
    public void readMsgForSeckill(String text) throws ParseException {
        // 将消息体转成map
        Map<String, String> map = JSON.parseObject(text, Map.class);
        Map<String, String> attachMap = JSON.parseObject(map.get("attach"), Map.class);
        String username = attachMap.get("username");
        // 获取通信标识
        String return_code = map.get("return_code");
        if ("SUCCESS".equals(return_code)){
            // 获取交易标识
            String result_code = map.get("result_code");
            if ("SUCCESS".equals(result_code)){
                // 支付成功，更新订单
                String transaction_id = map.get("transaction_id");  // 交易流水号
                String out_trade_no = map.get("out_trade_no");      // 订单号
                String time_end = map.get("time_end");              // 支付完成日期

                seckillOrderService.updateStatus(out_trade_no, transaction_id, username, time_end);
            } else {
                // 支付失败，删除订单
                seckillOrderService.deleteOrder(username);
            }
        }

    }
}
