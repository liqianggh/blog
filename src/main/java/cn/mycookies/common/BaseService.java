package cn.mycookies.common;

/**
 * @description 基础service
 * 最常用的service返回结果封装
 * @author Jann Lee
 * @date 2019-05-09 0:04
 **/
public class BaseService<T> {

    /**
     * 成功相应的结果集
     */
    protected ServerResponse<T> resultOk(){
        return ServerResponse.createBySuccess();
    }

    protected ServerResponse<T> resultOk(T data){
        return ServerResponse.createBySuccess();
    }

    protected ServerResponse<T> resultError() {
        return ServerResponse.createByError();
    }

    protected ServerResponse<T> resultError(String msg) {
        return resultError(ActionStatus.UNKNOWN.inValue(), msg);
    }

    protected ServerResponse<T> resultError(Integer errorCode, String msg) {
        return ServerResponse.createByErrorCodeMessage(errorCode, msg);
    }

}
