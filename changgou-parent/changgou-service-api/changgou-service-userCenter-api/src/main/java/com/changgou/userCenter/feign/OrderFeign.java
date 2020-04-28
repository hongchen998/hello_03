package com.changgou.userCenter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author hongchen
 * @date 2020/4/28 15:53
 */
@FeignClient(name = "orderCenter")
@RequestMapping("/center")
public interface OrderFeign {

}
