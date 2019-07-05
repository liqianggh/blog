package cn.mycookies.service;

import cn.mycookies.common.BaseService;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.UserMapper;
import cn.mycookies.pojo.po.UserDO;
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

}
