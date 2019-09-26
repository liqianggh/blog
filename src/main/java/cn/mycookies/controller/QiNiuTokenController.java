package cn.mycookies.controller;

import cn.mycookies.service.CommonService;
import cn.mycookies.common.base.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
