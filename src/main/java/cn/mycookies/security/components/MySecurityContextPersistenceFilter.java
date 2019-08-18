package cn.mycookies.security.components;

import cn.mycookies.security.SecurityUserDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * 保存认证信息到seurityContext中
 *
 * @author liqiang
 * @datetime 2019-08-07 16:38:06
 */
//@Component
public class MySecurityContextPersistenceFilter extends SecurityContextPersistenceFilter {
    static final String FILTER_APPLIED = "__spring_security_scpf_applied";

    private SecurityContextRepository repo;

    private boolean forceEagerSessionCreation = false;

    @Value("${cookie.name}")
    private String cookieName;

    public MySecurityContextPersistenceFilter() {
        this(new HttpSessionSecurityContextRepository());
    }

    public MySecurityContextPersistenceFilter(SecurityContextRepository repo) {
        this.repo = repo;
    }

    /**
     * 处理SecurityContextHolder的过滤器
     * 将 认证信息从Cookie中 添加到SecurityContextHolder中
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getAttribute(FILTER_APPLIED) != null) {
            // ensure that filter is only applied once per request
            chain.doFilter(request, response);
            return;
        }

        final boolean debug = logger.isDebugEnabled();

        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

        if (forceEagerSessionCreation) {
            HttpSession session = request.getSession();

            if (debug && session.isNew()) {
                logger.debug("Eagerly created session: " + session.getId());
            }
        }
        // 从cookie中获取认证信息，并设置到安全上下文中
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        UsernamePasswordAuthenticationToken authentication = null;
//        if (!Objects.isNull(cookie)) {
//            final String authToken = cookie.getValue();
//             if (rolesOptional.isPresent()) {
//                UserDetails userDetails = jwtTokenUtil.parseTokenToUserDetails(authToken);
//                if (Objects.nonNull(userDetails)) {
//                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                }
//            }
//        }
        // 假数据
        if (Objects.isNull(cookie)) {
            SecurityUserDetail securityUserDetail = new SecurityUserDetail();
            securityUserDetail.setId(1L);
            securityUserDetail.setUserName("李强");
            securityUserDetail.setRole("ROLE_ADMIN");
            authentication = new UsernamePasswordAuthenticationToken(securityUserDetail, null, securityUserDetail.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request,
                response);
        SecurityContext contextBeforeChainExecution = repo.loadContext(holder);
        try {
            // 将认证信息放入context中
            if (!Objects.isNull(authentication)) {
                contextBeforeChainExecution.setAuthentication(authentication);
            }
            SecurityContextHolder.setContext(contextBeforeChainExecution);

            chain.doFilter(holder.getRequest(), holder.getResponse());

        }
        finally {
            SecurityContext contextAfterChainExecution = SecurityContextHolder
                    .getContext();
            // Crucial removal of SecurityContextHolder contents - do this before anything
            // else.
            SecurityContextHolder.clearContext();
            repo.saveContext(contextAfterChainExecution, holder.getRequest(),
                    holder.getResponse());
            request.removeAttribute(FILTER_APPLIED);

            if (debug) {
                logger.debug("SecurityContextHolder now cleared, as request processing completed");
            }
        }
    }

    @Override
    public void setForceEagerSessionCreation(boolean forceEagerSessionCreation) {
        this.forceEagerSessionCreation = forceEagerSessionCreation;
    }
}
