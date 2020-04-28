package com.changgou.pay.service.impl;/**
 * @author hongchen
 * @date 2020/4/19 21:09
 */

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeixinPayServiceImpl
 * @Description
 * @Author hongchen
 * @Date 21:09 2020/4/19
 * @Version 2.1
 **/
@Service
public class WeixinPayServiceImpl implements WeiXinPayService {
    @Value("${weixin.appid}")
    private String appid;   // 微信公众账号或开放平台APP的唯一标识

    @Value("${weixin.partner}")
    private String partner; // 商户号

    @Value("${weixin.partnerkey}")
    private String partnerkey;// 商户密钥

    @Value("${weixin.notifyurl}")
    private String notifyurl; // 回调地址

    /**
     * @param parameters 封装多个条件
     * @return java.util.Map<java.lang.String               ,               java.lang.String>
     * @author hongchen
     * @Description 生成支付二维码
     * @Date 21:08 2020/4/19
     **/
    @Override
    public Map<String, String> createNative(Map<String,String> parameters) {
        //统一下单地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        try {
            //通过map封装支付接口所需的数据
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);         // 公众账号
            data.put("mch_id", partner);      // 商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());// 随机字符串
            data.put("body", "畅购86期订单支付");// 商品描述
            data.put("out_trade_no", parameters.get("out_trade_no"));// 商户订单号
            data.put("total_fee", parameters.get("total_fee"));        // 标价金额
            data.put("spbill_create_ip", "127.0.0.1");// 终端IP
            data.put("notify_url", notifyurl);         // 通知地址
            data.put("trade_type", "NATIVE");          // 交易类型

            // 开始封装附加数据attach
            String username = parameters.get("username");
            String exchange = parameters.get("exchange");
            String routingKey = parameters.get("routingKey");
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("username", username);
            attachMap.put("exchange", exchange);
            attachMap.put("routingKey", routingKey);
            data.put("attach", JSON.toJSONString(attachMap));

            String requstXml = WXPayUtil.generateSignedXml(data, partnerkey);

            //创建httpclient 发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(requstXml);// 设置携带的请求参数
            httpClient.post();

            //获取响应结果
            String responseXml = httpClient.getContent();

            //将响应的数据封装到map中
            Map<String, String> map = WXPayUtil.xmlToMap(responseXml);
            map.put("out_trade_no", parameters.get("out_trade_no"));
            map.put("total_fee", parameters.get("total_fee"));
            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param out_trade_no
     * @return java.util.Map<java.lang.String   ,   java.lang.String>
     * @author hongchen
     * @Description 查询支付状态
     * @Date 22:44 2020/4/19
     **/
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        //调用查询订单的api接口
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            //通过map封装请求参数
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);        // 公众账号
            data.put("mch_id", partner);      // 商户号
            data.put("nonce_str", WXPayUtil.generateNonceStr());// 随机字符串
            data.put("out_trade_no", out_trade_no);  // 商户订单号
            String requstXml = WXPayUtil.generateSignedXml(data, partnerkey);

            //创建httpclient发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(requstXml);// 设置携带的请求参数
            httpClient.post();

            //获取响应结果
            String responseXml = httpClient.getContent();

            //将响应的数据封装到map中
            Map<String, String> map = WXPayUtil.xmlToMap(responseXml);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
