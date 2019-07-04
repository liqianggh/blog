package cn.mycookies.pojo.dto;

import cn.mycookies.common.CommentTargetType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 创建评论/回复 请求信息
 *
 * @author Jann Lee
 * @date 2019-07-04 22:00
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@ApiModel("评论")
public class CommentAddRequest {

    @ApiModelProperty("被回复的评论id")
    private Integer pid;

    @ApiModelProperty("被评论主体id")
    @NotNull
    private Integer targetId;

    @ApiModelProperty("被评论主体类型，ARTICLE，COMMENT_REPLY，MESSAGE_BOARD")
    @NotNull
    private CommentTargetType targetType;

    @ApiModelProperty("用户邮箱")
    @NotEmpty
    private String userEmail;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("评论内容")
    @NotEmpty
    private String content;

}