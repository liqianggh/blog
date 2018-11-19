package cn.mycookies.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * @Description 接口返回结果包装类
 * @Author Jann Lee
 * @Date 2018-11-17 19:24
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

    private ServerResponse(int status){
      this.status=status;
    }
    private ServerResponse(int status ,T data){
      this.data=data;
      this.status=status;
    }
    private ServerResponse(int status,T data,String msg){
      this.data=data;
      this.status=status;
      this.msg=msg;
    }
    private ServerResponse(int status,String msg){
      this.status=status;
      this.msg=msg;
    }

    /**
    * 序列化时忽略
    */
    @JsonIgnore
    public  boolean isSuccess(){
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


    public static <T> ServerResponse<T> createBySuccess(){

      return new ServerResponse<T>(ActionStatus.NORMAL_RETURNED.inValue(),ActionStatus.NORMAL_RETURNED.getDescription());
    }

    public static <T> ServerResponse<T> createBySuccessMsg(String msg){

      return new ServerResponse(ActionStatus.NORMAL_RETURNED.inValue(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){

      return new ServerResponse<T>(ActionStatus.NORMAL_RETURNED.inValue(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){

      return new ServerResponse<T>(ActionStatus.NORMAL_RETURNED.inValue(),data,msg);
    }


    public static <T> ServerResponse<T> createByError(){

      return new ServerResponse<T>(ActionStatus.UNKNOWN.inValue(),ActionStatus.UNKNOWN.getDescription());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg){

      return new ServerResponse<T>(ActionStatus.NO_RESULT.inValue(),errorMsg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMsg){

      return new ServerResponse<T>(errorCode,errorMsg);
    }

}
