package cn.blog.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

 /**
  * @Description: joda-time实现的日期转换工具类
  * Created by Jann Lee on 2018/1/20  16:30.
  */
public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static Date strToDate(String dateTimeStr ) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

     public static String dateToStr(Date date,String regex) {
         if(date==null){
             return StringUtils.EMPTY;
         }
         DateTime dateTime = new DateTime(date);
         return dateTime.toString(regex);
     }


    public static String dateToStr(Date date) {
         if(date==null){
             return StringUtils.EMPTY;
         }
         DateTime dateTime = new DateTime(date);
         return dateTime.toString(STANDARD_FORMAT);
     }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateToStr(new Date()));
        System.out.println(DateTimeUtil.strToDate("2017-12-07 11:22:33"));
    }

}
