package cn.mycookies.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户实体类")
public class UserDTO {

    private Integer id;

    private String email;

    private String userName;

}
