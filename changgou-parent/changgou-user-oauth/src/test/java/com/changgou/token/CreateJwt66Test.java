package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CreateJwt66Test
 * @Description
 * @Author 传智播客
 * @Date 18:40 2019/8/21
 * @Version 2.1
 **/
public class CreateJwt66Test {

    /***
     * 创建令牌测试
     */
    @Test
    public void testCreateToken(){
        //证书文件路径
        String key_location="changgou86.jks";
        //秘钥库密码
        String key_password="changgou86";
        //秘钥密码
        String keypwd = "changgou86";
        //秘钥别名
        String alias = "changgou86";

        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);

        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());

        //读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();

        //定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "1");
        tokenMap.put("name", "itheima");
        tokenMap.put("roles", "ROLE_VIP,ROLE_USER");

        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivate));

        //取出令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }


    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.WL3yRsrvx-F0SQmpAWL4jkqFkNLHWBh_VJvSQjO820RLb4lsJuJpPxjtisiNjMq5gj44sbw9rHxkzWMf2zoPr8iASirLj_t-pXbGhz_8XoLQNE1fzOEU89kGJ0dNfajyBdR3loWZ9Kd7rlzEqPkT0zgbFxWaI6tjY8LprD1O6w1WWpHO0n7TgMGe72XqsYWBpP_DCqoktIU7EtXGPS8J1sfOxew1GJvPC2sE4fyGsUHgDYHozO7bbbAzSsuC7EBGKvxGEfIvG1jmqy1X4p88MsJxItSjwEsUMVK86NzHDDz172NF5eK2L8aQn6nzTo876EAbRB8XvaP1fB1foFSl5Q";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl2MotDNKHzEXpV7OlUrA\n1h10FsVjc9xS3bVhrMLCj4LNWSitrprwPhTtcDhg5OslGhCdVaBiUoiX94AJhlB/OOSLu78iTflBaidisxrEvm33I/MpCq+hOPz2L25GMrVdvszdO1jVBKEDHUs2vHhUGAJRIGRDKQX9PJ5jEsN2HocQu6ZDHgbYb/cPSRdzWvreEolKlPZi7yYOxXtpdjTqddCDc2wpwEDKF0D/HtYmlUV1wnqCQHLoEEz6K7IDGNHoYOnNnrg4rzIUgeTkytKnEWGjQVsXRRyc0h2gt2+J4FgkwB+Py4NHDaKo3EiwPfk+Y6ez27leDZM7etVjCp5csQIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

}
