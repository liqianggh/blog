package cn.mycookies.common.base;

import cn.mycookies.common.constants.ActionStatus;
import cn.mycookies.common.cache.impl.CacheService4LocalCache;
import cn.mycookies.common.exception.BusinessException;
import cn.mycookies.pojo.meta.BaseDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Preconditions;
import com.mysql.jdbc.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * service的基类，封装通用代码，减少重复代码
 *
 * @author Jann Lee
 * @date 2019-07-02 22:36
 */
public class BaseService {

    @Resource
    private CacheService4LocalCache cacheService4LocalCache;

    protected boolean isValidViewOrLike(String targetId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip =request.getHeader("X-Real-Ip") + "";
        if (StringUtils.isNullOrEmpty(targetId)) {
            return Boolean.FALSE;
        }
        return cacheService4LocalCache.isValidViewOrLike(String.join("_", ip, targetId));
    }

    protected boolean isValidComment() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip =request.getHeader("X-Real-Ip");
        if (StringUtils.isNullOrEmpty(ip)) {
            return Boolean.FALSE;
        }
        return cacheService4LocalCache.isValidComment(ip);
    }

    private String getIP(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader("X-Real-Ip");
    }

    /**
     * 分页信息
     */
    public Page getPage(PageInfo4Request pageInfo4Request){
        if (Objects.isNull(pageInfo4Request)) {
            pageInfo4Request = new PageInfo4Request();
        }
        Page page = PageHelper.startPage(pageInfo4Request.getPageNum(), pageInfo4Request.getPageSize());
        page.setOrderBy(pageInfo4Request.getOrderBy());
        return page;
    }


    /**
     * 成功相应的结果集
     */
    protected ServerResponse resultOk() {
        return resultOk(Boolean.TRUE);
    }

    protected <T> ServerResponse resultOk(T data) {
        return ServerResponse.createBySuccess(data);
    }

    /**
     * 有问题的响应
     *
     * @return
     */
    protected ServerResponse resultError() {
        return resultError(ActionStatus.UNKNOWN.inValue(), ActionStatus.UNKNOWN.getDescription());
    }

    protected ServerResponse resultError(String msg) {
        return resultError(ActionStatus.UNKNOWN.inValue(), msg);
    }

    protected ServerResponse resultError(Integer errorCode, String msg) {
        return ServerResponse.createByErrorCodeMessage(errorCode, msg);
    }

    /**
     * 参数异常
     *
     * @return
     */
    protected ServerResponse resultError4Param() {
        return resultError(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
    }

    protected ServerResponse resultError4Param(String msg) {
        return resultError(ActionStatus.PARAMAS_ERROR.inValue(), msg);
    }

    /**
     * 数据库异常
     *
     * @return
     */
    protected ServerResponse resultError4DB() {
        return resultError(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
    }

    protected ServerResponse resultError4DB(String msg) {
        return resultError(ActionStatus.DATABASE_ERROR.inValue(), msg);
    }

    protected void fillCreateTime(BaseDO baseDO){
        Preconditions.checkNotNull(baseDO, "创建的类不能为nul");
        baseDO.setCreateTime(System.currentTimeMillis());
    }
    protected void fillUpdateTime(BaseDO baseDO){
        Preconditions.checkNotNull(baseDO, "要修改的类不能为nul");
        baseDO.setUpdateTime(System.currentTimeMillis());
    }

    /**
     * 判断对象是否存在，不存在抛出异常
     * @param obj
     */
    public void checkExists(Object obj){
        if (Objects.isNull(obj)) {
            throw new BusinessException("数据不存在");
        }
    }
}
