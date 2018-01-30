package cn.blog.util;

import com.github.pagehelper.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
 /**
  * @Description: 高可用的Json工具类
  * Created by Jann Lee on 2018/1/30  1:01.
  */
@Slf4j
public class JsonUtil {

    public static ObjectMapper objectMapper= new ObjectMapper();

    static{
        //对象的所有字段全部列入 NON_DEFAULT如果是值为默认值不进行序列化
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认将日期转换为timestamps（毫秒）形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //日期格式化的形式统一
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));


        //反序列化的设置
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    //对象转换成字符串,没有格式化好的
    public static <T> String objToString(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    //返回格式化的字符串
    public static <T> String objToStringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    public static  <T> T stringToObj(String str, Class<T> clazz){
        if(StringUtil.isEmpty(str)|| clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public  static <T> T  stringToObj(String str , TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str)||typeReference==null) {
            return null;
        }
            try {
                return (T)(typeReference.getType().equals(String.class)?str:objectMapper.readValue(str,typeReference));
            } catch (IOException e) {
                log.warn("Parse String to Object error",e);
                return null;
            }
    }

    public  static <T> T  stringToObj(String str , Class<?> collectionClass ,Class<?>... elementClass){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);

        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

}
