package cn.mycookies.pojo.vo;

import cn.mycookies.pojo.po.BlogWithBLOBs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 博客详情vo
 *
 * @author Jann Lee
 * @date 2019-04-19 23:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDetailVO {

    public Integer id;

    public String title;

    public String summary;

    public String content;

    public String htmlContent;

    public String imgUrl;

    public Integer code;

    public Integer categoryId;

    public List<Integer> tags;

    private Byte status;

    public static BlogDetailVO createFrom(BlogWithBLOBs blogDO, List<Integer> tagIds){
        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BeanUtils.copyProperties(blogDO, blogDetailVO);
        blogDetailVO.setTags(tagIds);
        blogDetailVO.setStatus(blogDO.getBlogStatus());
        return blogDetailVO;
    }
}
