package cn.mycookies.service;

import cn.mycookies.common.base.BaseService;
import cn.mycookies.common.base.ServerResponse;
import com.qiniu.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 公共servcie
 *
 * @author liqiang
 * @datetime 2019/7/10 17:36
 **/
@Service
public class CommonService extends BaseService {

    @Value("${qiniu.ak}")
    private String accessKey;

    @Value("${qiniu.sk}")
    private String secreKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    /**
     * 生成token
     * @return
     */
    public ServerResponse<String> getQiniuUploadToken() {

        Auth auth = Auth.create(accessKey, secreKey);
        return resultOk(auth.uploadToken(bucket));
    }
}
