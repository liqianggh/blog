package cn.blog.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
  * @Description: 用户访问/点赞使用的类
  * Created by Jann Lee on 2018/1/31  22:52.
  */
 @Setter
 @Getter
 @NoArgsConstructor
 @AllArgsConstructor
public class RemoteUser {
    private String remoteAddr;
    private boolean isLike;
}
