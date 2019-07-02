package cn.mycookies.common.exception;

import cn.mycookies.common.ActionStatus;

/**
 * @description 自定义业务异常
 *
 * @author Jann Lee
 * @date 2019-07-02 22:06
 **/
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 4947743645170712257L;

    private int code;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ActionStatus actionStatus) {
        super(actionStatus.getDescription());
        this.code = actionStatus.inValue();
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
