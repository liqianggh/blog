package cn.blog.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
  * @Description: 获取项目配置文件内容的工具类
  * Created by Jann Lee on 2018/1/20  0:06.
  */
@Slf4j
public class PropertiesUtil {

    private static Properties props;

    static{
        String fileName = "blog.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常!(PropertiesUtil)",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(org.apache.commons.lang.StringUtils.isBlank(value)){
            return null;
        }

        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){
        String value = props.getProperty(key.trim());

        if(org.apache.commons.lang.StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }
}
