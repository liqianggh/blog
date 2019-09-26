package cn.mycookies.pojo.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博客do
 *
 * @author Jann Lee
 * @date 2019-07-09 22:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogWithBLOBs extends BlogDO {

    private String content;

    private String htmlContent;
}