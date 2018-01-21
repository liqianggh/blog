package cn.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
  * @Description: editor.md文件上传结果
  * Created by Jann Lee on 2018/1/22  2:14.
  */
 @Setter
 @Getter
 @NoArgsConstructor
 @AllArgsConstructor
public class UploadResult {
    private Integer success;
    private String message;
    private String url;
}
