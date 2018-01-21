package cn.blog.controller.backend;

import cn.blog.service.IFileService;
import cn.blog.util.PropertiesUtil;
import cn.blog.vo.UploadResult;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private IFileService iFileService;

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

}
