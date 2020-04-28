package com.changgou.filter;/**
 * @author hongchen
 * @date 2020/4/15 0:43
 */

import com.changgou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName AuthorizeFilter
 * @Description 自定义全局过滤器
 * @Author hongchen
 * @Date 0:43 2020/4/15
 * @Version 2.1
 **/
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //常量定义
    private static final String AUTHORIZE_TOKEN = "Authorization";
    //登录页面
    private static final String LOGIN_URL = "http://localhost:9001/oauth/login";

    /*/**
     * @author hongchen
     * @Description 判断用户是否登录
     * @Date 0:46 2020/4/15
     * @Param  * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取requst,response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //发送请求，首先要经过过滤器，如果客户进行登录操作直接放行
        String uri = request.getURI().getPath();//获取用户请求
        if (URLFilter.hasAuthorize(uri)) {
            //if (uri.startsWith("/api/user/login")) {
            return chain.filter(exchange);
        }
        //如果不是登陆操作，需要判断用户是否登录
        //获取token得值（携带token得方式：url请求参数中，请求头，cookie） 这里key错了  我昨天也犯过一样的
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        if (StringUtils.isEmpty(token)) {
            //从cookie中获取
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }
        if (StringUtils.isEmpty(token)) {
            // 从请求头获取
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }
        // 判断token是否为null
        if (StringUtils.isEmpty(token)) {
            // 如果没有token不能放行
            System.out.println("哥们你还没有登陆。。。");
            /*response.setStatusCode(HttpStatus.UNAUTHORIZED);// 设置响应的状态码 401, "Unauthorized" 无效认证
            return response.setComplete(); // 不放行*/
            // 获取当前用户的请求地址 http://localhost:8001/api/user
            String url = request.getURI().toString();
            //用户未登录，踢回登陆页面
            String path = LOGIN_URL + "?ReturnUrl=" + url;
            response.setStatusCode(HttpStatus.SEE_OTHER);   //设置响应的状态码, 跳转到其他的地址上
            response.getHeaders().add("Location", path);
            return response.setComplete();
        }
        //解析token
        try {
            /*// 解析成功
            Claims claims = JwtUtil.parseJWT(token);*/
            //这里不在解析，因为加密令牌的方式变了，之前：HS256，现在：RSA
            //将tooken放入请求头中，交给对应的微服务去解析
            request.mutate().header(AUTHORIZE_TOKEN, "bearer " + token);
        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败
            // 不能放行
            response.setStatusCode(HttpStatus.UNAUTHORIZED);// 设置响应的状态码 401, "Unauthorized" 无效认证
            return response.setComplete(); // 不放行
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
