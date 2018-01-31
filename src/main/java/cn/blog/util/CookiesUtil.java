package cn.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookiesUtil {

    //    private static final String COOKIE_DOMAIN=".onlineshop.com";
    private static final String COOKIE_DOMAIN="onlineshop.com";//tomcat8.5以后写法
    private static final String COOKIE_NAME="onlineshop_login_token";

    //读取cookie
    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                log.info("cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    //设置cookie
    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");//设置在根目录
        //单位是秒 如果不设置，cookies不会写入硬盘 只在内存，即当前页面有效
        cookie.setMaxAge(60*60*24*365);//-1代表永久
        cookie.setHttpOnly(true);//只能通过http请求
        log.info("write cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }

    //删除cookie
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie [] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie :cookies){
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);//删除cookie
                    log.info("delete cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return ;
                }
            }
        }
    }
}
