package com.changgou;/**
 * @author hongchen
 * @date 2020/4/12 18:46
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName SearchWebApplication
 * @Description search启动类服务
 * @Author hongchen
 * @Date 18:46 2020/4/12
 * @Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.changgou.search.feign")
public class SearchWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchWebApplication.class, args);
    }
}
