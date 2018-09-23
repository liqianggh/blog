package cn.mycookies.pojo;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class HelloPropertties {
    private String name;
    private String age;
    private String gender;
}
