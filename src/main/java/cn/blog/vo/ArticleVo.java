package cn.blog.vo;


import cn.blog.pojo.Blog;
import cn.blog.pojo.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ArticleVo{
    private BlogVo blogVo;
    private List<BlogVo> blogVoList;
    private List<TagVo> tagVoList;
    private List<CategoryVo> categoryList;

    private List<BlogVo> lastAndNext;

}
