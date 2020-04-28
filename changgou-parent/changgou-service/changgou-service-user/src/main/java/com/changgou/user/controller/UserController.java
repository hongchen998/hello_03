package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/****
 * @Author:传智播客
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
/**
 * @author hongchen
 * @Description 添加用户积分
 * @Date 1:14 2020/4/19
 * @param username
 * @param points
 * @return entity.Result
 **/
@GetMapping("/incrUserPoints/{username}/{points}")
public Result incrUserPoints(@PathVariable(value = "username") String username,
                             @PathVariable(value = "points") Integer points){
    userService.incrUserPoints(username, points);
    return new Result(true, StatusCode.OK, "增加积分成功");

    }

    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        //判断用户名和密码不能为空
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            //根据用户名查找用户
            User user = userService.findById(username);
            //判断用户名和密码是否正确
            // 咱们系统：密码加密的方式 BCrypt加密
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                // 生成token
                // 自定义载荷
                Map<String, Object> map = new HashMap<>();
                map.put("username", username);
                map.put("role", "Admin");
                map.put("status", "SUCCESS");
                map.put("userinfo", user);
                String subject = JSON.toJSONString(map);
                String token = JwtUtil.createJWT(UUID.randomUUID().toString(), subject, null);
                // 用户登录成功后，将用户信息保存到cookie中（key-value【jwt】）
                Cookie cookie = new Cookie("Authorization", token);
                cookie.setPath("/");// 有效的项目路径
                cookie.setDomain("localhost");// 二级域名  jd.com   search.jd.com
                response.addCookie(cookie);// 将cookie保存到客户端
                return new Result(true, StatusCode.OK, "登陆成功");
            }
            return new Result(false, StatusCode.LOGINERROR, "账号或者密码错误");
        }
        return new Result(false, StatusCode.LOGINERROR, "账号或者密码不能为空");
    }

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<User>> findList(@RequestBody(required = false) User user) {
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody User user, @PathVariable String id) {
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyAuthority('admin')")// 必须带有admin权限的用户才能访问该方法
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable(value = "id") String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    /***
     * 查询User全部数据
     * @return
     */
    @GetMapping
    public Result<List<User>> findAll() {
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }
}
