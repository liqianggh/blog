package cn.mycookies.pojo.vo;

import cn.mycookies.pojo.dto.CatalogItem;
import cn.mycookies.pojo.meta.BlogWithBLOBs;
import cn.mycookies.common.utils.JsonUtil;
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
public class BlogDetail4AdminVO {

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
    public static BlogDetail4AdminVO createFrom(BlogWithBLOBs blogDO, List<Integer> tagIds){
        BlogDetail4AdminVO blogDetail4AdminVO = new BlogDetail4AdminVO();
        BeanUtils.copyProperties(blogDO, blogDetail4AdminVO);
        List<CatalogItem> catalogItems = JsonUtil.stringToObj(blogDO.getBlogCatalog(), List.class, CatalogItem.class);
        blogDetail4AdminVO.setCatalogs(catalogItems);
        blogDetail4AdminVO.setTags(tagIds);
        blogDetail4AdminVO.setStatus(blogDO.getBlogStatus());
        return blogDetail4AdminVO;
    }
}
