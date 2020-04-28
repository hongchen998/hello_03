package com.changgou;/**
 * @author hongchen
 * @date 2020/4/14 18:36
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *@ClassName GatewayWebApplication
 *@Description
 *@Author hongchen
 *@Date 18:36 2020/4/14
 *@Version 2.1
 **/
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }
    /*/**
     * @author hongchen
     * @Description IP限流
     * @Date 22:17 2020/4/14
     * @Param  * @param
     * @return org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
     **/
    /***
     * IP限流
     * @return
     */
    @Bean(name="ipKeyResolver")
    public KeyResolver userKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //获取远程客户端IP
                String hostName = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                System.out.println("hostName:"+hostName);
                return Mono.just(hostName);
            }
        };
    }
}
