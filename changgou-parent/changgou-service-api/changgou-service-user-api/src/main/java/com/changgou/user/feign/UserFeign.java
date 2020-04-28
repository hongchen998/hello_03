package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hongchen
 * @date 2020/4/17 14:55
 */
@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {
    /**
     * @author hongchen
     * @Description 加载用户信息
     * @Date 14:58 2020/4/17
     * @param id
     * @return entity.Result<com.changgou.user.pojo.User>
     **/
    @GetMapping("/{id}")
    Result<User> findById(@PathVariable(value = "id") String id);
/**
 * @author hongchen
 * @Description 给用户添加积分
 * @Date 1:18 2020/4/19
 * @param username
 * @param points
 * @return entity.Result
 **/
    @GetMapping("/incrUserPoints/{username}/{points}")
    Result incrUserPoints(@PathVariable(value = "username") String username,
                          @PathVariable(value = "points") Integer points);
}
