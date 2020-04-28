package com.changgou;/**
 * @author hongchen
 * @date 2020/4/15 0:01
 */

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JwtDemo
 * @Description
 * @Author hongchen
 * @Date 0:01 2020/4/15
 * @Version 2.1
 **/

public class JwtDemo {
    /*/**
     * @author hongchen
     * @Description 生成令牌
     * @Date 0:02 2020/4/15
     * @Param  * @param
     * @return void
     **/
    @Test
    public void testCreatToken() {
        //创建Jwt对象
        JwtBuilder builder = Jwts.builder();
        //构建head
        Map<String, Object> header = new HashMap<>();
        header.put("age", 20);
        header.put("sex", "男");
        builder.setHeader(header);
        //构建playload
        builder.setId("001");
        builder.setIssuer("赵四");
        builder.setIssuedAt(new Date());
        //设置singnaure
        builder.signWith(SignatureAlgorithm.HS256,"hongchen");
        //设置过期时间
        builder.setExpiration(new Date(System.currentTimeMillis()+30000));
        //生成令牌
        String token = builder.compact();
        //打印输出
        System.out.println(token);
        //eyJzZXgiOiLnlLciLCJhbGciOiJIUzI1NiIsImFnZSI6MjB9.eyJqdGkiOiIwMDEiLCJpc3MiOiLotbXlm5siLCJpYXQiOjE1ODY4ODA3OTd9.aCDGNBeXdggFPuYI3mYsq0vxHn9q_CoGnoOP6SNeSkg
    }
    /*/**
     * @author hongchen
     * @Description 解析Token
     * @Date 0:14 2020/4/15
     * @Param  * @param
     * @return void
     **/
    @Test
    public void testParseToken(){
        //被解析得令牌
        String token="eyJzZXgiOiLnlLciLCJhbGciOiJIUzI1NiIsImFnZSI6MjB9.eyJqdGkiOiIwMDEiLCJpc3MiOiLotbXlm5siLCJpYXQiOjE1ODY4ODEyOTQsImV4cCI6MTU4Njg4MTMyNH0.3hpsWToNTP4_Y2IdQnrVvHS2cPyNp10q2kzKAjHje84";
        //创建解析对象
        JwtParser parser = Jwts.parser();
        parser.setSigningKey("hongchen");
        //解析令牌
        Claims claims = parser.parseClaimsJws(token).getBody();
        Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        //打印输出
        System.out.println(claims);
        System.out.println(claimsJws);
    }
}
