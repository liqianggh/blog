package cn.mycookies;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 程序入口
 *
 * @author Jann Lee
 * @date 2019-09-27 0:12
 */
@SpringBootApplication
@MapperScan("cn.mycookies.dao")
public class MyblogApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyblogApplication.class, args);
    }
}
