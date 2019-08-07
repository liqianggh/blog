package cn.mycookies.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 *
 * @description 接口返回结果包装类
 * @author Jann Lee
 * @date 2018-11-17 19:24
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{
    /**
     * 状态码
     */
    private int status;

    /**
     * 消息，状态码的相关描述
     */
    private String msg;

    /**
     * 服务端响应的数据
     */
    private T data;

    private ServerResponse(int status ,T data){
        this(status, data, null);
    }
    private ServerResponse(int status,String msg){
        this(status, null, msg);
    }
    private ServerResponse(int status,T data,String msg){
      this.data=data;
      this.status=status;
      this.msg=msg;
    }

    /**
     * 成功消息体构建
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(){
      return createBySuccess(null,null);
    }

    public static <T> ServerResponse<T> createBySuccessMsg(String msg){
      return createBySuccess(msg, null);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
      return createBySuccess(null, data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
      return new ServerResponse<T>(ActionStatus.NORMAL_RETURNED.inValue(),data,msg);
    }

    /**
     * 失败消息体构建
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError(){
      return createByErrorMessage(ActionStatus.UNKNOWN.getDescription());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg){
      return createByErrorCodeMessage(ActionStatus.UNKNOWN.inValue(),errorMsg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMsg){
      return new ServerResponse<T>(errorCode,errorMsg);
    }

    /**
     * 序列化时忽略
     */
    @JsonIgnore
    public  boolean isOk(){
        return this.status==ActionStatus.NORMAL_RETURNED.inValue();
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "{" + "status: " + status + ", msg:'" + msg + '\'' + ", data:" + data + '}';
    }
}
