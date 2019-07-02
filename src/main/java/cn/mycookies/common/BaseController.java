package cn.mycookies.common;

import cn.mycookies.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 所有Controller继承的基类Controller
 *
 * @author Jann Lee
 * @date 2019-07-02 22:05
 */
public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired(required = false)
    Validator validator;

    public void validate(Object params) {
        if (validator == null) {
            logger.warn("没有可用的validator");
            return;
        }
        Set<ConstraintViolation<Object>> validate = this.validator.validate(params);
        for (ConstraintViolation<Object> objectConstraintViolation : validate) {
            throw new BusinessException(ActionStatus.PARAMAS_ERROR.inValue(), objectConstraintViolation.getMessage());
        }
    }

    /**
     * 获取HttpServletRequest
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取HttpServletResponse
     */
    protected HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

}
