package com.changgou.order.config;/**
 * @author hongchen
 * @date 2020/4/15 22:32
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @ClassName ResourceServerConfig
 * @Description 读取公钥配置类
 * @Author hongchen
 * @Date 22:32 2020/4/15
 * @Version 2.1
 **/
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)// 激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    //公钥
    private static final String PUBLIC_KEY = "public.key";

    /*/**
     * @author hongchen
     * @Description定义JwtTokenStore
     * @Date 23:13 2020/4/15
     * @Param  * @param jwtAccessTokenConverter
     * @return org.springframework.security.oauth2.provider.token.TokenStore
     **/
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /*/**
     * @author hongchen
     * @Description定义JJwtAccessTokenConverter
     * @Date 23:16 2020/4/15
     * @Param  * @param
     * @return org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
     **/
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setVerifierKey(getPutKey());//秘钥的一部分
        return jwtAccessTokenConverter;
    }

    /*/**
     * @author hongchen
     * @Description获取非对称加密公钥 Key
     * @Date 23:16 2020/4/15
     * @Param  * @param
     * @return java.lang.String
     **/
    private String getPutKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*/**
     * @author hongchen
     * @DescriptionHttp安全配置，对每个到达系统的http请求链接进行校验
     * @Date 23:30 2020/4/15
     * @Param  * @param http
     * @return void
     **/
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //所有请求必须认证通过
        http.authorizeRequests()
                .anyRequest()
                .authenticated();// 其他地址需要认证授权
    }
}
