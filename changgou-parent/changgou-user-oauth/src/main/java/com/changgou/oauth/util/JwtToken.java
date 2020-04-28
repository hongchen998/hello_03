package com.changgou.oauth.util;/**
 * @author hongchen
 * @date 2020/4/17 18:44
 */

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 *@ClassName JwtToken
 *@Description 令牌工具类
 *@Author hongchen
 *@Date 18:44 2020/4/17
 *@Version 2.1
 **/
public class JwtToken {
    public static String adminJwt(){
        // 证书文件路径
        String key_location="changgou86.jks";
        // 秘钥库密码
        String key_password="changgou86";
        // 秘钥密码
        String keypwd = "changgou86";
        // 秘钥别名
        String alias = "changgou86";
        // 访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);
        // 创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());
        // 读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());
        // 获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();
        // 定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("authorities", new String[]{"admin"}); // 给该令牌授权
        // 生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivate));
        // 取出令牌
        String encoded = jwt.getEncoded();
        return encoded;
    }

}
