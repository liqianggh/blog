package cn.blog.common.myInteceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//HandlerInterceptorAdapter
public class CrossInterceptor extends HandlerInterceptorAdapter {

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        response.addHeader("Access-Control-Allow-Origin","http://58.87.104.109:8080"); 
//         response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Methods","*");
        response.addHeader("Access-Control-Max-Age","100");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        String ip = request.getLocalAddr();
        response.setHeader("Server",ip);
        //允许客户端发送cookies
//        response.addHeader("Access-Control-Allow-Credentials","true");
        return super.preHandle(request, response, handler);
    } 

} 