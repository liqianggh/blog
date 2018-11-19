package cn.mycookies.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
       objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
       //忽略空bean转json的错误
       objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
       //日期格式化的形式统一
       objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));


       //反序列化的设置
       //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况，防止错误
       objectMapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,false);
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
