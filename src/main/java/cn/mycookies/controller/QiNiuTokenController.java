package cn.mycookies.controller;

import cn.mycookies.common.CommonService;
import cn.mycookies.common.ServerResponse;
import com.google.gson.annotations.SerializedName;
import com.qiniu.http.Client;
import com.qiniu.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.GeneralSecurityException;

/**
 * 生成七牛云token
 *
 * @author liqiang
 * @datetime 2019/7/10 17:34
 **/
@RestController
@RequestMapping("/upload/token")
public class QiNiuTokenController {
    @Resource
    private CommonService commonService;
    @GetMapping
    public ServerResponse<String> generateUploadToken(){
        return commonService.getQiniuUploadToken();
    }

}
