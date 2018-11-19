package cn.mycookies.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @Description 日期格式工具类
 * @Author Jann Lee
 * @Date 2018-11-18 20:11
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

}
