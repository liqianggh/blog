package cn.mycookies.service;

import cn.mycookies.common.BaseService;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.UserMapper;
import cn.mycookies.pojo.po.User;
import cn.mycookies.pojo.po.UserExample;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 根据邮箱获取访客信息
     * @param visitorEmail
     * @return
     */
    public User getVisitorDOByEmail(String visitorEmail){
        Preconditions.checkArgument(StringUtils.isEmpty(visitorEmail), "邮箱不能为空");

        UserExample visitorExample = new UserExample();
        visitorExample.createCriteria().andUserEmailEqualTo(visitorEmail);
        List<User> visitorDOS = userMapper.selectByExample(visitorExample);
        if (CollectionUtils.isNotEmpty(visitorDOS)) {
            return visitorDOS.get(0);
        }
        return null;
    }

     public ServerResponse<Boolean> createUserInfo(User user){
        Preconditions.checkNotNull(user);
        if (StringUtils.isEmpty(user.getUserEmail()) || StringUtils.isEmpty(user.getUserName())) {
            return resultError4Param("添加访客失败,参数" + user.toString());
        }
        fillCreateTime(user);
        if (userMapper.insert(user) == 0) {
            return resultError4DB("添加访客失败,参数" + user.toString());
        }
        return resultOk();
     }

    public ServerResponse<Boolean> updateVisitorInfo(User user){
        Preconditions.checkNotNull(user);
        if (StringUtils.isEmpty(user.getUserEmail()) || StringUtils.isEmpty(user.getUserName())) {
            return resultError4Param("更新访客失败,参数" + user.toString());
        }
        fillUpdateTime(user);
        if (userMapper.updateByPrimaryKeySelective(user) == 0) {
            return resultError4DB("更用户信息失败,参数" + user.toString());
        }
        return resultOk();
    }

}
