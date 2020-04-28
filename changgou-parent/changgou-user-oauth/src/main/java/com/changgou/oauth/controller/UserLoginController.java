package com.changgou.oauth.controller;/**
 * @author hongchen
 * @date 2020/4/16 0:05
 */

import com.changgou.oauth.service.UserloginService;
import com.changgou.oauth.util.AuthToken;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description 用户登录认证控制层
 * @Author hongchen
 * @Date 0:05 2020/4/16
 * @Version 2.1
 **/
@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Value("${auth.clientId}")
    private String clientId;// 客户端id

    @Value("${auth.clientSecret}")
    private String clientSecret;// 客户端秘钥

    @Autowired(required = false)
    private UserloginService userloginService;

    /**
     * @param password
     * @return entity.Result
     * @author hongchen
     * @Description用户登录
     * @Date 0:17 2020/4/16
     * @Param * @param username
     **/
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        try {
            //默认：就是密码授权模式
            String grant_type = "password";

            AuthToken authToken = userloginService.login(username, password, clientId, clientSecret, grant_type);
            //登录成功后，将token放入cookie中
            Cookie cookie=new Cookie("Authorization",authToken.getAccessToken());
            cookie.setPath("/");
            cookie.setDomain("localhost");
            response.addCookie(cookie);
            return new Result(true, StatusCode.OK, "登陆成功", authToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.LOGINERROR, "登陆失败");
    }

}
