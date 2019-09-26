package cn.mycookies.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * k-v结果封装
 *
 * @author Jann Lee
 * @date 2019-04-19 22:57
 **/
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyValueVO<K,V> {
    private K k;
    private V v;
}
