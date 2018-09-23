package cn.mycookies.controller;

import cn.mycookies.dao.HelloDao;
import cn.mycookies.pojo.HelloPropertties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ConfigurationProperties(prefix = "data")
public class HelloControler {

    @Autowired
    private HelloPropertties propertties;

    @Autowired
    HelloDao helloDao;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        List<String> list = helloDao.allUsers();
        System.out.println(list.toString());
        return propertties.toString();
    }
}
