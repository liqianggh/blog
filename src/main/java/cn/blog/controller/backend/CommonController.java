package cn.blog.controller.backend;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;
import cn.blog.service.ICategoryService;
import cn.blog.service.IFileService;
import cn.blog.service.ITagService;
import cn.blog.util.PropertiesUtil;
import cn.blog.vo.CategoryAndTags;
import cn.blog.vo.CategoryVo;
import cn.blog.vo.TagVo;
import cn.blog.vo.UploadResult;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private IFileService iFileService;
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private ITagService iTagService;

    @RequestMapping("upload.do")
    @ResponseBody
    public Map upload(@RequestParam("editormd-image-file") MultipartFile multipartFile){

        String fileName = iFileService.upload(multipartFile,"blog");
        String prefix = PropertiesUtil.getProperty("ftp.server.http.prefix");
//        UploadResult uploadResult = new UploadResult();
//        uploadResult.setSuccess(1);
//        uploadResult.setMessage("文件上传成功!");
//        uploadResult.setUrl(prefix+uploadResult);
        Map uploadResult = new HashMap<String,Object>();
        uploadResult.put("success",1);
        uploadResult.put("message","success");
        uploadResult.put("url",prefix+fileName);

        return  uploadResult;
    }

    @RequestMapping("load_category_tags.do")
    @ResponseBody
    public ServerResponse<CategoryAndTags> loadCategorysAndTags(){

        List<CategoryVo> categoryList = iCategoryService.listAllSimple();

        List<TagVo> tagVoList = iTagService.listAllSimple();

        CategoryAndTags categoryAndTags = new CategoryAndTags();
        categoryAndTags.setCategoryList(categoryList);
        categoryAndTags.setTagList(tagVoList);

        return ServerResponse.createBySuccess(categoryAndTags);
    }
}
