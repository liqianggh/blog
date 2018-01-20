package cn.blog.controller.backend;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Tag;
import cn.blog.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

 /**
  * @Description: 后台 标签Controlelr
  * Created by Jann Lee on 2018/1/20  15:54.
  */
@Controller
@RequestMapping("/manage/tag")
public class TagController {

    @Autowired
    ITagService iTagService;

    @RequestMapping("saveOrUpdate.do")
    @ResponseBody
    public ServerResponse saveOrUpdate(Tag tag){
        return iTagService.saveOrUpdate(tag);
    }

    @RequestMapping("listAll.do")
    @ResponseBody
    public ServerResponse listAll(){
        return iTagService.listAll();
    }

    @RequestMapping("listAllSimple.do")
    @ResponseBody
    public ServerResponse listAllSimple(){
        return iTagService.listAllSimple();
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse listAll(Integer tagId){
        return iTagService.delete(tagId);
    }


}
