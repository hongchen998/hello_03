package com.changgou.oauth.controller;/**
 * @author hongchen
 * @date 2020/4/18 20:15
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *@ClassName LoginRedirect
 *@Description 跳转用户登录
 *@Author hongchen
 *@Date 20:15 2020/4/18
 *@Version 2.1
 **/
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {
    /**
     * @author hongchen
     * @Description 跳转用户登录页面
     * @Date 20:16 2020/4/18
     * @param
     * @return java.lang.String
     **/
    @RequestMapping("/login")
    public String login(@RequestParam(value = "ReturnUrl") String ReturnUrl, Model model){
        // 将登录前的地址放入model域中
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "login";
    }
}
