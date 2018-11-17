package cn.mycookies.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Description 测试
 * @Author Jann Lee
 * @Date 2018-11-17 21:09
 **/
@RestController
@RequestMapping("blog")
@Api("helloworld")
public class HelloController {

    @GetMapping("hello")
    @ApiOperation(value = "hello", notes = "A demo of myblog")
    public String helloworld(){
        return "hello !";
    }
}
