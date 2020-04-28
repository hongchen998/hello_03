package com.changgou.userCenter;/**
 * @author hongchen
 * @date 2020/4/28 16:56
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *@ClassName OrderCenter
 *@Description 订单中心
 *@Author hongchen
 *@Date 16:56 2020/4/28
 *@Version 2.1
 **/
@Controller
@RequestMapping("/orderCenter")
public class OrderCenterController {
    @RequestMapping("/myOrder")
    public String userCenter(){
        return "myOrder";
    }
}
