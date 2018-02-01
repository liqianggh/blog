package cn.blog.common;

 /**
  * @Description: 响应状态的枚举类
  * Created by Jann Lee on 2018/1/20  0:03.
  */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"),
     NULL_ARGUMENT(3,"NULL_ARGUMENT"),

     LIKE_IT_SUCCESS(4,"LIKE IT SUCCESS!"),
     NOT_LIKE_SUCCESS(5,"CANCEL IT SUCCESS!");


     private final int code;
    private final String desc;
    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
