package cn.blog.common;

import lombok.extern.slf4j.slf4j;
import org.springframework.stereotype.component;
import org.springframework.web.servlet.handlerexceptionresolver;
import org.springframework.web.servlet.modelandview;
import org.springframework.web.servlet.view.json.mappingjacksonjsonview;

import javax.servlet.http.httpservletrequest;
import javax.servlet.http.httpservletresponse;

@slf4j
@component
public class exceptionresolver implements handlerexceptionresolver {

    @override
    public modelandview resolveexception(httpservletrequest httpservletrequest, httpservletresponse httpservletresponse, object o, exception e) {
        modelandview modelandview  = new modelandview(new mappingjacksonjsonview());
        modelandview.addobject("status",responsecode.error.getcode());
        modelandview.addobject("msg","接口异常信息");
        modelandview.addobject("data",e.tostring());
        return modelandview;
    }
}
