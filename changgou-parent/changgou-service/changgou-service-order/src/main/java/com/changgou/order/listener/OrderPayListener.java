package com.changgou.order.listener;/**
 * @author hongchen
 * @date 2020/4/20 0:56
 */

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName OrderPayListener
 * @Description 订单支付的消息监听器
 * @Author hongchen
 * @Date 0:56 2020/4/20
 * @Version 2.1
 **/
@Component
@RabbitListener(queues = {"${mq.pay.queue.order}"})
public class OrderPayListener {

    @Autowired
    private OrderService orderService;

    /**
     * @param msg 队列中的消息
     * @return void
     * @author hongchen
     * @Description
     * @Date 1:00 2020/4/20
     **/
    @RabbitHandler  // 监听队列，触发该方法执行
    public void readMessage(String msg) {
        // 将json转成Map
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        String return_code = map.get("return_code");    // 通信标识

        if ("SUCCESS".equals(return_code)) {
            String result_code = map.get("result_code"); // 业务标识
            String out_trade_no = map.get("out_trade_no");

            if ("SUCCESS".equals(result_code)) {
                String transaction_id = map.get("transaction_id");  // 交易流水号
                // 支付成功：更新订单
                orderService.updateStatus(out_trade_no, transaction_id);
            } else {
                // 支付失败：删除订单
                orderService.deleteOrder(out_trade_no);
            }
        }
    }
}
