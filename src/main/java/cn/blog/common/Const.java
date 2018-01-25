package cn.blog.common;

import com.google.common.collect.Sets;

import java.util.Set;

public interface Const {
    public interface BlogCodeType{
        int PRIVATE_BLOG = 0;
        int PUBLIC_BLOG = 1;
        int RECOMMENDED_BLOG = 2;
        int ALL_BLOG = 3;


    }
    public interface IndexConst{
        int BLOG_NUM = 10;
        int HOT_NUM = 5;

        int RECOMMENDED = 5;

    }

    public interface BlogListOrderBy{
        Set<String> CREATETIME_ASC_DESC = Sets.newHashSet("createTime_desc","createTime_asc");
        Set<String> VIEWCOUNT_ASC_DESC = Sets.newHashSet("viewCount_desc","viewCount_asc");
        Set<String> LIKECOUNT_ASC_DESC = Sets.newHashSet("likeCount_desc","likeCount_asc");
        Set<String> SHARECOUNT_ASC_DESC = Sets.newHashSet("shareCount_desc","shareCount_asc");
        Set<String> COMMENTCOUNT_ASC_DESC = Sets.newHashSet("commentCount_desc","commentCount_asc");

    }
}
