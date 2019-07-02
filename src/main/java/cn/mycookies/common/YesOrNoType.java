package cn.mycookies.common;

import java.util.Objects;

/**
 * @author Jann Lee
 * @date 2019-07-02 23:35
 **/
public enum YesOrNoType {

    /**
     * 用来表示数据的有效性
     */
    YES(1, "有效/可用"), NO(0, "无效/已删除");

    private int code;
    private String desc;

    YesOrNoType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public YesOrNoType valueOf(Integer code) {
        switch (code) {
            case 0:
                return NO;
            case 1:
                return YES;
            default:
                return null;
        }
    }

    public YesOrNoType valueOfDefaultNO(Integer code) {
        YesOrNoType result = valueOf(code);
        if (Objects.isNull(result)) {
            result = NO;
        }
        return result;
    }
}

