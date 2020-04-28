package com.changgou.pay.controller;/**
 * @author hongchen
 * @date 2020/4/19 21:32
 */

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName WeixinPayController
 * @Description 微信支付控制层
 * @Author hongchen
 * @Date 21:32 2020/4/19
 * @Version 2.1
 **/
@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {
    @Autowired
    private WeiXinPayService weiXinPayService;
    
    @Autowired
    private Environment env;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param request 微信通知的结果数据
     * @return void
     * @author hongchen
     * @Description 支付成功，微信通知
     * @Date 23:40 2020/4/19
     **/
    @RequestMapping("/notify/url")
    public void notifyUrl(HttpServletRequest request) throws Exception {
        //获取requst中微信的请求数据（微信把结果数据给我们了）
        ServletInputStream is = request.getInputStream();   // 获取网络传输的数据
        ByteArrayOutputStream os = new ByteArrayOutputStream();// 内存级别操作 输出

        //缓冲区 参数设置为2n次方
        byte[] buffer = new byte[1024];
        int len = 0;
        //读取
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        //刷新
        os.flush();
        //关流
        is.close();
        os.close();
        //将字节流数据转成对象
        String strXml = new String(os.toByteArray(), "UTF-8");
        Map<String, String> map = WXPayUtil.xmlToMap(strXml);
        System.out.println(map);
        //System.out.println("支付成功，微信通知我们了。。。");
        
        /*//将微信通知的结果信息发送到mq
        String exchange = env.getProperty("mq.pay.exchange.order");
        String routingKey = env.getProperty("mq.pay.routing.key");
        rabbitTemplate.convertAndSend(exchange,routingKey, JSON.toJSONString(map));// 坑 第三个参数：json串*/

        // 将消息发送到对应的队列中（取决于我们获取到的附加数据）
        String attach = map.get("attach");
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);
        // attachMap：{exchange：mq.pay.exchange.order}
        // attachMap：{exchange：mq.pay.exchange.seckillorder}
        String exchange = env.getProperty(attachMap.get("exchange"));
        String routingKey = env.getProperty(attachMap.get("routingKey"));
        System.out.println("exchange:" + exchange + "---routingKey:" + routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(map));

    }

    /**
     * @param parameters 封装多个条件
     * @return java.util.Map<java.lang.String               ,               java.lang.String>
     * @author hongchen
     * @Description 生成支付二维码
     * @Date 21:08 2020/4/19
     **/
    // String outtradeno, String money, exchange, routingKey, username：需要传递的数据有这些  直接通过map封装
    @RequestMapping("/create/native")
    public Result createNative(@RequestParam Map<String,String> parameters) {
        Map<String, String> map = weiXinPayService.createNative(parameters);
        return new Result(true, StatusCode.OK, "生成支付二维码成功", map);

    }

    /**
     * @param outtradeno
     * @return java.util.Map<java.lang.String       ,       java.lang.String>
     * @author hongchen
     * @Description 查询支付状态
     * @Date 22:44 2020/4/19
     **/
    @RequestMapping("/query/status")
    public Result queryPayStatus(String outtradeno) {
        Map<String, String> map = weiXinPayService.queryPayStatus(outtradeno);
        return new Result(true, StatusCode.OK, "查询支付状态结果", map);
    }
}
