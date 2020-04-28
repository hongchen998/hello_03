package com.changgou.pay.service;

import java.util.Map;

/**
 * @author hongchen
 * @date 2020/4/19 21:07
 */
public interface WeiXinPayService {

    /**
     * @param parameters 封装多个条件
     * @return java.util.Map<java.lang.String               ,               java.lang.String>
     * @author hongchen
     * @Description 生成支付二维码
     * @Date 21:08 2020/4/19
     **/
    Map<String,String> createNative(Map<String,String> parameters);

    /**
     * @author hongchen
     * @Description     查询支付状态
     * @Date 22:44 2020/4/19
     * @param out_trade_no
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    Map<String,String> queryPayStatus(String out_trade_no);
}
