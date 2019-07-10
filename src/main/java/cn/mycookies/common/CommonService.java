package cn.mycookies.common;

import com.google.gson.annotations.SerializedName;
import com.qiniu.http.Client;
import com.qiniu.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * 公共servcie
 *
 * @author liqiang
 * @datetime 2019/7/10 17:36
 **/
@Service
public class CommonService extends BaseService{

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
