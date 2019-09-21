package cn.mycookies.service;

import cn.mycookies.common.BaseService;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.UserMapper;
import cn.mycookies.pojo.po.UserDO;
import cn.mycookies.pojo.po.UserExample;
import cn.mycookies.security.SecurityUserDetail;
import cn.mycookies.utils.JwtTokenUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 访客信息管理servcie
 *
 * @author Jann Lee
 * @date 2019-07-05 23:30
 **/
@Service
public class UserService extends BaseService {

    @Resource
    private UserMapper userMapper;

    @Value("${cookie.name}")
    private String cookieName;

    @Value("${cookie.max-age}")
    private Integer maxAge;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 根据邮箱获取访客信息
     * @param visitorEmail
     * @return
     */
    public UserDO getUserDOByEmail(String visitorEmail){
        Preconditions.checkArgument(StringUtils.isNotEmpty(visitorEmail), "邮箱不能为空");

        UserExample visitorExample = new UserExample();
        visitorExample.createCriteria().andUserEmailEqualTo(visitorEmail);
        List<UserDO> visitorDOS = userMapper.selectByExample(visitorExample);
        if (CollectionUtils.isNotEmpty(visitorDOS)) {
            return visitorDOS.get(0);
        }
        return null;
    }

     public ServerResponse<Boolean> createUserInfo(UserDO userDO){
        Preconditions.checkNotNull(userDO);
        if (StringUtils.isEmpty(userDO.getUserEmail()) || StringUtils.isEmpty(userDO.getUserName())) {
            return resultError4Param("添加访客失败,参数" + userDO.toString());
        }
        fillCreateTime(userDO);
        if (userMapper.insert(userDO) == 0) {
            return resultError4DB("添加访客失败,参数" + userDO.toString());
        }
        return resultOk();
     }

    public ServerResponse<Boolean> updateVisitorInfo(UserDO userDO){
        Preconditions.checkNotNull(userDO);
        if (StringUtils.isEmpty(userDO.getUserEmail()) || StringUtils.isEmpty(userDO.getUserName())) {
            return resultError4Param("更新访客失败,参数" + userDO.toString());
        }
        fillUpdateTime(userDO);
        if (userMapper.updateByPrimaryKeySelective(userDO) == 0) {
            return resultError4DB("更用户信息失败,参数" + userDO.toString());
        }
        return resultOk();
    }

    public ServerResponse<String> login( ) {
        // 2.2 将合法的权限重新赋值到用户信息中，生成token并返回
        SecurityUserDetail securityUserDetail = new SecurityUserDetail();
        securityUserDetail.setId(1L);
        securityUserDetail.setUserName("李强");
        securityUserDetail.setRole("ROLE_ADMIN");
        String token = jwtTokenUtil.generateToken(securityUserDetail);
        return resultOk(token);
    }

}
