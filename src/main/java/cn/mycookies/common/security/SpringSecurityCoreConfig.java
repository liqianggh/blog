package cn.mycookies.common.security;

import cn.mycookies.common.security.components.MyEntryPoint;
import cn.mycookies.common.security.components.MyLogoutSuccessHandler;
import cn.mycookies.common.security.components.MySecurityContextPersistenceFilter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * spring security的拦截配置
 *
 * @author liqiang
 * @datetime 2019/8/7 14:52
 **/
@Configuration  // 标识该类为配置类
@EnableWebSecurity  // 开启Spring Security服务
public class SpringSecurityCoreConfig extends WebSecurityConfigurerAdapter {
    /**
     * 没有登录时进行的处理
     */
    @Autowired
    private MyEntryPoint myEntryPoint;
    /**
     * 每当请求进来时处理数据的过滤器
     */
    @Autowired
    private MySecurityContextPersistenceFilter mySecurityContextPersistenceFilter;
    /**
     * 登出成功后进行的后续处理handler
     */
    @Autowired
    private MyLogoutSuccessHandler logoutSuccessHandler;

    @Value("${cookie.name}")
    private String cookieName;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            /**
         * 配置跨域相关
         */
            .cors().configurationSource(corsConfigurationSource()).and()
            .csrf().disable()
            .authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/manage/**").hasRole("ADMIN")
            // 所有请求都要登录过
            //.anyRequest().authenticated()
            .and().httpBasic().authenticationEntryPoint(myEntryPoint);

        http.addFilterAt(mySecurityContextPersistenceFilter, SecurityContextPersistenceFilter.class);

//        http
//            .logout()
//            .logoutUrl("/logout")
//            .deleteCookies(cookieName)
//            .logoutSuccessHandler(logoutSuccessHandler);
    }

    /**
     * 服务端允许跨域请求配置
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Lists.newArrayList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        configuration.setAllowCredentials(Boolean.TRUE);
        return source;
    }

}
