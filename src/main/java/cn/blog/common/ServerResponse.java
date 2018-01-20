package cn.blog.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
  * @Description: 高服用的数据封装类
  * Created by Jann Lee on 2018/1/19  23:34.
  */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
     //状态码
     private int status;
     //状态码对应的信息
     private String msg;
     //封装的数据类型
     private T data;

    private ServerResponse(int  status) {
        this.status=status;
    }
    private ServerResponse(int status,T data) {
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg) {
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    //序列化之后忽略
    @JsonIgnore
    public  boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode() ;
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


    //用来响应的静态方法

    public static <T> ServerResponse<T> createBySuccess(){
         return new ServerResponse(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    //使用泛型解决构造方法冲突问题
    public static <T> ServerResponse<T> createBySuccess(String message,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),message,data);
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String message){
        return new ServerResponse(ResponseCode.SUCCESS.getCode(),message);
    }


    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse(ResponseCode.ERROR.getCode());
    }
    public static <T> ServerResponse<T> createByError(T data){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),data);
    }
    //使用泛型解决构造方法冲突问题
    public static <T> ServerResponse<T> createByError(String message,T data){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),message,data);
    }
    public static <T> ServerResponse<T> createByErrorMessage(String message){
        return new ServerResponse(ResponseCode.ERROR.getCode(),message);
    }
    public static <T> ServerResponse<T> createByErrorCodeAndMessage(int code,String message){
        return new ServerResponse(code,message);
    }

    
}
