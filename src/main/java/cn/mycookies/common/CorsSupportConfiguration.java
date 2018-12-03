package cn.mycookies.common;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @className CorsSupportConfiguration
 * @description 跨域支持
 * @author Jann Lee
 * @date 2018-11-25 18:14
 * @update 2018-12-03 15:43
 **/

@Configuration
public class CorsSupportConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*/**");
            }
        };
    }
}