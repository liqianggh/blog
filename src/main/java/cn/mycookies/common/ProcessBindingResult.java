package cn.mycookies.common;

import cn.mycookies.utils.JsonUtil;
import com.google.common.collect.Lists;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Objects;

public class ProcessBindingResult {

    /**
     * 处理数据校验异常结果，返回字符串
     * @param bindingResult
     * @return
     */
    public static String process(BindingResult bindingResult) {

        List<String> resultList = Lists.newArrayList();
        if (bindingResult.getAllErrors().size() == 1) {
            return bindingResult.getAllErrors().get(0).getDefaultMessage();
        }
        if (Objects.nonNull(bindingResult) && bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                resultList.add(error.getDefaultMessage());
            }
        }
        return JsonUtil.objToString(resultList);
    }
}
