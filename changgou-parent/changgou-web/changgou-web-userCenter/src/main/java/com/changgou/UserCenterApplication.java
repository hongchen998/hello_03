package com.changgou;/**
 * @author hongchen
 * @date 2020/4/28 15:36
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName UserCenterApplication
 * @Description 用户中心启动项
 * @Author hongchen
 * @Date 15:36 2020/4/28
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.changgou.userCenter.feign")
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }
}
