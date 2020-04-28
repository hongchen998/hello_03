package com.changgou;/**
 * @author hongchen
 * @date 2020/4/14 23:02
 */

import entity.TokenDecode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 *@ClassName UserApplication
 *@Description
 *@Author hongchen
 *@Date 23:02 2020/4/14
 *@Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.user.dao"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    public TokenDecode tokenDecode(){
        return new TokenDecode();
    }
}
