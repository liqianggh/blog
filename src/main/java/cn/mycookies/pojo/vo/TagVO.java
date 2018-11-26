package cn.mycookies.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @className TagVO
 * @description TODO
 * @author Jann Lee
 * @date 2018-11-21 0:02
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
@ToString
public class TagVO {

    public Integer id;

    public String tagName;

    public Integer count;
}
