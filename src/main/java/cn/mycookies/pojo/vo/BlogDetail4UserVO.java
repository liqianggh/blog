package cn.mycookies.pojo.vo;

import cn.mycookies.pojo.dto.CatalogItem;
import cn.mycookies.pojo.dto.TagVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * 博客类， 查看博客详情时使用
 *
 * @author Jann Lee
 * @date 2019-09-27 0:17
 */
@Data
@NoArgsConstructor
@ApiModel("博客vo")
public class BlogDetail4UserVO {

    public Integer id;

    public String title;

    public String summary;

    public String htmlContent;

    List<CatalogItem> catalogs;

    public String imgUrl;

    public Integer code;

    public Integer viewCount;

    public Integer likeCount;

    public String createTime;

    public String updateTime;

    public String calcTime;

    public String categoryName;

    public Integer categoryId;

    public List<TagVO> tagList;

    public BlogDetail4AdminVO last;

    public BlogDetail4AdminVO next;

    PageInfo<CommentListItemVO> comments;
}
