package cn.mycookies.pojo.po;

 import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
 import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@ToString
@ApiModel("评论实体类")
public class Comment {

    @ApiModelProperty(value = "用户id",required = true)
    @Required
    private Integer userId;
    /**
     * 如果targetId=-1说明是留言，如果targetId=0说明是回复，其他则是博客评论
     */
    @ApiModelProperty(value="评论主体id，可以是博客id，留言板标识（-1），回复标识（0）",required = true)
    private Integer targetId;

    @ApiModelProperty(value = "被回复用户id")
    private Integer replyUid;

    @ApiModelProperty(value="评论的内容",required = true)
    @Length(min = 1,max = 500,message = "评论内容长度应该在1-500之内")
    private String content;

    @ApiModelProperty(value = "评论的点赞数，作为显示的排序",hidden = true)
    private Integer likeCount;

    @ApiModelProperty(hidden = true)
    private Integer status;

    @ApiModelProperty(hidden = true)
    private Integer sessionId;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

    @ApiModelProperty(hidden = true)
    private Integer id;

}