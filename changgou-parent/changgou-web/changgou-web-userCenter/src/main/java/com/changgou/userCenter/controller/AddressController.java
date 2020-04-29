package com.changgou.userCenter.controller;/**
 * @author hongchen
 * @date 2020/4/28 16:57
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *@ClassName AddressController
 *@Description 收货地址管理
 *@Author hongchen
 *@Date 16:57 2020/4/28
 *@Version 2.1
 **/
@Controller
@RequestMapping("/address")
public class AddressController {
    @RequestMapping("/addresslist")
    public String userCenter(){
        return "addresslist";
    }
}
