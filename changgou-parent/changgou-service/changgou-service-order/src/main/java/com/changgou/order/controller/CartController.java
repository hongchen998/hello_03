package com.changgou.order.controller;/**
 * @author hongchen
 * @date 2020/4/17 16:41
 */

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *@ClassName CartController
 *@Description 购物车控制层
 *@Author hongchen
 *@Date 16:41 2020/4/17
 *@Version 2.1
 **/
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * @author hongchen
     * @Description 商品加入购物车
     * @Date 16:46 2020/4/17
     * @param id
     * @param num
     * @return entity.Result
     **/
    @RequestMapping("/add")
    public Result add(Long id, Integer num){
        //String username="szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"加入购物车成功");

    }

    /**
     * @author hongchen
     * @Description 购物车列表查询
     * @Date 18:35 2020/4/17
     * @param
     * @return entity.Result<java.util.List<com.changgou.order.pojo.OrderItem>>
     **/
    @GetMapping("/list")
    public Result<List<OrderItem>> list(){
       // String username="szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItems = cartService.list(username);
        return new Result<>(true,StatusCode.OK,"购车车列表查询成功",orderItems);
    }
}
