package cn.mycookies.common.constants;

/**
 * 评论的类型，评论可以在博客，回复，留言版中
 *
 * @author Jann Lee
 * @date 2019-07-04 23:18
 */
public enum  CommentTargetType {

    /**
     * 评论的类型
     */
    ARTICLE(1, "博客"), COMMENT_REPLY(2, "评论回复"), MESSAGE_BOARD(3, "留言版");

    private int code;

    private String desc;

    CommentTargetType(int code, String desc) {
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
