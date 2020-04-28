package com.changgou.oauth.service.impl;/**
 * @author hongchen
 * @date 2020/4/16 0:36
 */

import com.changgou.oauth.service.UserloginService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

/**
 * @ClassName UserLoginServiceImpl
 * @Description
 * @Author hongchen
 * @Date 0:36 2020/4/16
 * @Version 2.1
 **/
@Service
public class UserLoginServiceImpl implements UserloginService {
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    

    /**
     * @author hongchen
     * @Description
     * @Date 16:42 2020/4/16
     * @param username  账号
     * @param password  密码
     * @param clientId  客户端id
     * @param clientSecret  客户端密钥
     * @param grant_type    授权方式
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret, String grant_type) {
        try {
            // oauth的提供的生成token的接口地址
//        String url = "http://localhost:9001/oauth/token";
            ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
            String uri = serviceInstance.getUri().toString();   // 数据：http://ip:port
            String url = uri + "/oauth/token";

            // 封装请求数据
            LinkedMultiValueMap body = new LinkedMultiValueMap();       // 封装请求体
            body.add("grant_type", grant_type);
            body.add("username", username);
            body.add("password", password);
            LinkedMultiValueMap headers = new LinkedMultiValueMap();    // 封装头信息
//        headers.add("Authorization", "Basic " + 客户端id:客户端秘钥并且进行base64编码);
            byte[] encode = Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes());
            String base64Encode = new String(encode, "UTF-8");
            headers.add("Authorization", "Basic " + base64Encode);
            HttpEntity requestEntity = new HttpEntity(body, headers);

            // 发送请求并且响应数据
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> map = responseEntity.getBody();
            AuthToken authToken=new AuthToken();
            authToken.setAccessToken(map.get("access_token").toString());
            authToken.setRefreshToken(map.get("refresh_token").toString());
            authToken.setJti(map.get("jti").toString());

            return authToken;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
