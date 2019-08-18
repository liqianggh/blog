package cn.mycookies.controller.backend;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.basic.BaseController;
import cn.mycookies.pojo.dto.UserLoginRequest;
import cn.mycookies.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员登录controller
 *
 * @author liqiang
 * @datetime 2019/8/7 15:34
 **/
@RestController
@RequestMapping
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value ="登录")
    public ServerResponse<Boolean> login(HttpServletRequest request, HttpServletResponse response, @RequestBody UserLoginRequest userLoginRequest){
        validate(userLoginRequest);

        return userService.login(request,response,null);
    }

}
