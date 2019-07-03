package cn.mycookies.common;

/**
 * 标签类别美剧
 *
 * @author Jann Lee
 * @date 2018-11-20 23:00
 **/
public enum TagType {


    /**
     * 标签，分类
     */
    TAG(1), CATEGORY(2);

    private int code;

    TagType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public TagType valueOfCode(Integer code) {
        switch (code) {
            case 1:
                return TAG;
            case 2:
                return CATEGORY;
            default:
                return null;
        }
    }

}
