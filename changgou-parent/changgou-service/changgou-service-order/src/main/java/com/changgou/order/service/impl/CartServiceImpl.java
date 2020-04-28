package com.changgou.order.service.impl;/**
 * @author hongchen
 * @date 2020/4/17 16:18
 */

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description 购物车业务实现类
 * @Author hongchen
 * @Date 16:18 2020/4/17
 * @Version 2.1
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired(required = false)
    private SpuFeign spuFeign;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    /**
     * @param num      购买数量
     * @param id       库存id
     * @param username 当前用户
     * @return void
     * @author hongchen
     * @Description 商品加入购物车
     * @Date 16:19 2020/4/17
     **/
    @Override
    public void add(Integer num, long id, String username) {
        //如果传入数量《=0，则删除该商品的购物车数据
        if (num<=0) {
            redisTemplate.boundHashOps("cart_"+username).delete(id);
        }

        //查询sku
        Result<Sku> skuResult = skuFeign.findById(id);

        //获取sku
        Sku sku = skuResult.getData();
        if (sku != null){
            Long spuId = sku.getSpuId();

            Result<Spu> spuResult = spuFeign.findById(spuId);

            Spu spu = spuResult.getData();
            if (spu != null){

                //将商品信息封装到购物车对象中
                OrderItem orderItem = goods2OrderIterm(sku, spu, num);

                //将购物车保存到redis中
                redisTemplate.boundHashOps("cart_" + username).put(id, orderItem);

            }
        }



    }

/**
 * @author hongchen
 * @Description 查询购物车列表
 * @Date 18:32 2020/4/17
 * @param username
 * @return java.util.List<com.changgou.order.pojo.OrderItem>
 **/
    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> list = redisTemplate.boundHashOps("cart_" + username).values();
        return list;
    }

    /**
     * @param sku
     * @param spu
     * @param num
     * @return com.changgou.order.pojo.OrderItem
     * @author hongchen
     * @Description 将商品信息封装到购物车对象中
     * @Date 16:29 2020/4/17
     **/
    private OrderItem goods2OrderIterm(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());  // 商品分类
        orderItem.setSpuId(spu.getId());                 // sku的id
        orderItem.setSkuId(sku.getId());                 // skuid
        orderItem.setName(sku.getName());                // 商品名称
        orderItem.setPrice(sku.getPrice());              // 商品单价
        orderItem.setNum(num);                           // 购买数量
        orderItem.setMoney(sku.getPrice() * num);          // 总金额
        // 实付金额=总金额 - 优惠金额（优惠券、京东e卡等等）
        orderItem.setPayMoney(orderItem.getMoney() - 0);   // 实付金额
        orderItem.setImage(sku.getImage());              // 商品图片
        orderItem.setWeight(sku.getWeight());            // 商品毛重
        orderItem.setPostFee(9);// 商品运费   运费：超级会员，免运费  普通会员：判断购买金额是否超过199元
        orderItem.setIsReturn("0");                      // 是否退货

        return orderItem;
    }
}
