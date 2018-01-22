package cn.blog.vo;

import cn.blog.pojo.Category;
import cn.blog.pojo.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAndTags {
    private List<TagVo> tagList;
    private List<CategoryVo> categoryList;
}
