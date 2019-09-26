package cn.mycookies.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录参数
 *
 * @author liqiang
 * @datetime 2019/8/7 15:36
 **/
@Data
public class UserLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 16,message = "用户名长度应在1-20之间")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Length(min = 3, max = 16,message = "密码长度应在3-16之间")
    private String password;

}
