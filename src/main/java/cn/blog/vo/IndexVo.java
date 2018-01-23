package cn.blog.vo;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
  * @Description: 初始化首页用的
  * Created by Jann Lee on 2018/1/23  20:02.
  */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexVo {

    private PageInfo blogPageInfo;

    //推荐博客
    private List<BlogVo> recommendBlog;

    //热门博客
    private List<BlogVo> hotBlogs;

    //标签云
    private List<TagVo> tagVoList;




}
