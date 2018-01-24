package cn.blog.vo;


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
}
