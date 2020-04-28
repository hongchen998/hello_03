package com.changgou.userCenter;/**
 * @author hongchen
 * @date 2020/4/28 16:58
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *@ClassName CommentController
 *@Description 评价管理
 *@Author hongchen
 *@Date 16:58 2020/4/28
 *@Version 2.1
 **/
@Controller
@RequestMapping("/comment")
public class CommentController {
    @RequestMapping("/mycomment")
    public String userCenter(){
        return "mycomment";
    }
}
