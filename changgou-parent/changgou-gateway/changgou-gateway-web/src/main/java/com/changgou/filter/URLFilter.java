package com.changgou.filter;/**
 * @author hongchen
 * @date 2020/4/17 21:56
 */

/**
 * @ClassName URLFilter
 * @Description 资源放行配置
 * @Author hongchen
 * @Date 21:56 2020/4/17
 * @Version 2.1
 **/
public class URLFilter {
    private static String uri = "/api/user/add,api/user/login";//无需拦截的url

    public static boolean hasAuthorize(String url) {
        String[] uris = uri.split(",");
        for (String s : uris) {
            if (url.startsWith(uri)) {
                return true;//无需登录
            }
        }
        return false;
    }
}
