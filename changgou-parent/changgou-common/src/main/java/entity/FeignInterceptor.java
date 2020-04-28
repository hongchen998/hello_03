package entity;/**
 * @author hongchen
 * @date 2020/4/17 21:36
 */

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 *@ClassName FeignInterceptor
 *@Description 微服务间的feign调用,获取所有的请求头进行封装
 *@Author hongchen
 *@Date 21:36 2020/4/17
 *@Version 2.1
 **/
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 如果其微服务工程之间相互调用，那么也需要将令牌信息放入头中
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null){
            HttpServletRequest request = attributes.getRequest();
            // 获取所有的头信息
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null){
                while (headerNames.hasMoreElements()){
                    // 头信息的：名称（key）
                    String name = headerNames.nextElement();
                    // 获取头对应的value
                    String value = request.getHeader(name);
                    // 将信息放入RestTemplate中
                    requestTemplate.header(name, value);
                }
            }
        }
    }
}
