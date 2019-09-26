package cn.mycookies.common.security.components;

import cn.mycookies.common.base.ServerResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出完成后的handler
 *
 * @author liqiang
 * @datetime 2019/8/7 17:54
 **/
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ServerResponse.createBySuccess("登出成功", Boolean.TRUE).toString());
        response.getWriter().flush();
    }
}
