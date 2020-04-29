package com.changgou.userCenter.controller;/**
 * @author hongchen
 * @date 2020/4/28 16:34
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *@ClassName UserCenterController
 *@Description 用户中心视图解析
 *@Author hongchen
 *@Date 16:34 2020/4/28
 *@Version 2.1
 **/
@Controller
@RequestMapping("/userCenter")
public class UserCenterController {

    @RequestMapping("/home")
    public String userCenter(){
        return "home";
    }
}
