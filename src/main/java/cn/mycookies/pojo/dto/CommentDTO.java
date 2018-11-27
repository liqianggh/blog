package cn.mycookies.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@ToString
@ApiModel("评论")
public class CommentDTO {

    @ApiModelProperty(value = "用户id",required = true)
    @NotNull
    private String email;
    /**
     * 如果targetId=-1说明是留言，如果targetId=0说明是回复，其他则是博客评论
     */
    @ApiModelProperty(value="评论主体id，可以是博客id，留言板标识（0），回复标识（-1）",required = true)
    private Integer targetId;

    @ApiModelProperty(value = "被回复用户")
    private String replyEmail;

    @ApiModelProperty(value="评论的内容",required = true)
    @Length(min = 1,max = 500,message = "评论内容长度应该在1-500之内")
    private String content;

    @ApiModelProperty(value = "对话id")
    private String sessionId;

}