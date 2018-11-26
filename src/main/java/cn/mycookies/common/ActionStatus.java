 package cn.mycookies.common;

 /**
  * @description 状态码
  * @author Jann Lee
  * @date 2018-11-17 20:29
  */
 public class ActionStatus {
    public static final ActionStatus NORMAL_RETURNED = new ActionStatus(1000, "OK");
    public static final ActionStatus REQUEST_TYPE_ERROR = new ActionStatus(1001, "Request Type Error");
    public static final ActionStatus PARAMAS_ERROR = new ActionStatus(1002, "Parameters Error");
    public static final ActionStatus DATABASE_ERROR = new ActionStatus(1003, "Database Error");
    public static final ActionStatus NOT_LOGIN = new ActionStatus(1004, "NOT Login");
    public static final ActionStatus FORBIDDEN = new ActionStatus(1005, "Access Forbidden");
    public static final ActionStatus UNKNOWN = new ActionStatus(1006, "Unknow Error");
    public static final ActionStatus SERVER_ERROR = new ActionStatus(1007, "Server Error");
    public static final ActionStatus SERVICE_SUSPEND = new ActionStatus(1008, "Server Busy,Try Later!");
    public static final ActionStatus OAUTH_ERROR = new ActionStatus(1009, "Oauth error");
    public static final ActionStatus NICK_NAME_HAD_EXIST = new ActionStatus(1010, "Nick Name Had Exist");
    public static final ActionStatus DATA_REPEAT = new ActionStatus(1010, "DATA_REPEAT");
    public static final ActionStatus PARAM_ERROR_WITH_ERR_DATA = new ActionStatus(1012, "param error");
    public static final ActionStatus NO_RESULT = new ActionStatus(1020, "NO_RESULT");
    public static final ActionStatus EXPIRE_TIMESTAMP = new ActionStatus(1101, "Expire timestamp");
    public static final ActionStatus INVALID_PHONE_NUM = new ActionStatus(1201, "Invalid Telephone");
    public static final ActionStatus MUST_REVALIDATE = new ActionStatus(2000, "Must_ReValidate");
    public static final ActionStatus USER_NOT_EXIST = new ActionStatus(2001, "User Not Exist");
    public static final ActionStatus REACH_REQUEST_LIMIT = new ActionStatus(2002, "Reach Upper Limit Per Hour");
    public static final ActionStatus TOO_FREQUENCY = new ActionStatus(2003, "Request Too Frequent");
    public static final ActionStatus CHAT_GROUP_MAXMEM_OVERFLOW = new ActionStatus(3001, "Exceeds the maximum limit,maximum is 50.");
    public static final ActionStatus FRIEND_FOLLOW_MAXMEM_OVERFLOW = new ActionStatus(3002, "Exceeds the maximum limit,user follow max number is 500.");
    public static final ActionStatus USER_ACCOUNT_FORBIDDEN = new ActionStatus(3101, "The user account forbidden.");
    public static final ActionStatus REQUEST_LOCKED = new ActionStatus(4003, "request too fast");
    private int status = -1;
    private String desc = null;

    public int value() {
        return this.status;
    }

    public int inValue() {
        return this.status;
    }

    public String getDescription() {
        return this.desc;
    }

    public ActionStatus(int status, String description) {
        this.status = status;
        this.desc = description;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}