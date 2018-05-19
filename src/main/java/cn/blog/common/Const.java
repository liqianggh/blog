package cn.blog.common;

import com.google.common.collect.Sets;

import java.util.Set;

public interface Const {

    public interface CacheTypeName{
        String TAGS_WITHCOUNT = "tags_with_count";
        String CATEGORY_WITHCOUNT="category_with_count";
        String REMOTE_USER_LIKE_STATUS="remote_user_isLike";
        String REMOTE_VIEW_USER="remote_view_user";
    }

    public interface  CacheTime{
        int ADD_LIKE_TIME= 60*60*12;
        int VIEW_COUNT_TIME=60*30;
        // 用户停留时间
        int VISITOR_COUNT = 61*30;
    }


    public interface BlogCodeType{
        int PRIVATE_BLOG = 0;
        int PUBLIC_BLOG = 1;
        int RECOMMENDED_BLOG = 2;
        int ALL_BLOG = 3;
    }
    public interface IndexConst{
        int BLOG_NUM = 12;
        int HOT_NUM = 5;
        int NEW_PUBLISH=5;
        int RECOMMENDED = 5;

    }
    public interface BlogListOrderBy{
        Set<String> CREATETIME_ASC_DESC = Sets.newHashSet("createTime_desc","createTime_asc");
        Set<String> VIEWCOUNT_ASC_DESC = Sets.newHashSet("viewCount_desc","viewCount_asc");
        Set<String> LIKECOUNT_ASC_DESC = Sets.newHashSet("likeCount_desc","likeCount_asc");
        Set<String> SHARECOUNT_ASC_DESC = Sets.newHashSet("shareCount_desc","shareCount_asc");
        Set<String> COMMENTCOUNT_ASC_DESC = Sets.newHashSet("commentCount_desc","commentCount_asc");

    }
    public interface REDIS_LOCK{
        String REDIS_LOCK_NAME="REDIS_LOCK_NAME";
        String REDIS_VISITOR_COUNT = "REDIS_VISITOR_COUNT";
    }

    public interface VISITOR{
        String VISITOR_KEY = "VISITOR_KEY";
        String VISITOR_IP_PREFIX="VISITOR_IP_PREFIX";
        // 一个同步周期内的新增的访客量
        String  VISITOR_COUNT = "VISITOR_COUNT";
        // 原有的访客量
        String VISITOR_BASIC = "VISITOR_BASIC";
    }
}
