package cn.mycookies.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("Tag实体类")
public class UserDTO {

    private Integer id;

    private String email;

    private String userName;


}
