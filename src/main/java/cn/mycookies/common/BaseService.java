package cn.mycookies.common;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * service的基类，封装通用代码，减少重复代码
 *
 * @author Jann Lee
 * @date 2019-07-02 22:36
 */
public class BaseService {

    protected Logger logger = LoggerFactory.getLogger(getClass().getName());


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
}
