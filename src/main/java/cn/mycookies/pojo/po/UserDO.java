package cn.mycookies.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息表
 *
 * @author Jann Lee
 * @date 2019-07-06 0:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDO extends BaseDO{

    private String userEmail;

    private String userName;

    private Integer userStatus;

}