package cn.mycookies;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 使用war包启动的必须配置
 *
 * @author liqiang
 * @datetime 2019/7/25 14:18
 **/
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyblogApplication.class);
    }
}
