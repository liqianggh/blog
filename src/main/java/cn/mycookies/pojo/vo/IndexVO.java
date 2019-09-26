package cn.mycookies.pojo.vo;

import cn.mycookies.pojo.dto.TagWithCountVO;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页要展示的内容
 *
 * @author Jann Lee
 * @date 2018-11-25 22:07
 */
@Data
@NoArgsConstructor
public class IndexVO {

    public PageInfo<BlogDetail4UserVO> blogList;

    public List<BlogDetail4UserVO> recommendList;

    public List<BlogDetail4UserVO> clickRankList;

    public List<TagWithCountVO> categoryList;

    public List<TagWithCountVO> tagList;

}
