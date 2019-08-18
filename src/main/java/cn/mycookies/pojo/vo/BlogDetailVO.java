package cn.mycookies.pojo.vo;

import cn.mycookies.pojo.dto.CatalogItem;
import cn.mycookies.pojo.po.BlogWithBLOBs;
import cn.mycookies.utils.JsonUtil;
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

    private List<CatalogItem> catalogs;

    public String imgUrl;

    public Integer code;

    public Integer categoryId;

    public List<Integer> tags;

    private Byte status;

    /**
     * do 转换成vo
     */
    public static BlogDetailVO createFrom(BlogWithBLOBs blogDO, List<Integer> tagIds){
        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BeanUtils.copyProperties(blogDO, blogDetailVO);
        List<CatalogItem> catalogItems = JsonUtil.stringToObj(blogDO.getBlogCatalog(), List.class, CatalogItem.class);
        blogDetailVO.setCatalogs(catalogItems);
        blogDetailVO.setTags(tagIds);
        blogDetailVO.setStatus(blogDO.getBlogStatus());
        return blogDetailVO;
    }
}
