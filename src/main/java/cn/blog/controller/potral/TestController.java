package cn.blog.controller.potral;

import cn.blog.common.ServerResponse;
import cn.blog.service.CacheService.CacheService;
import cn.blog.service.IFileService;
import cn.blog.util.PropertiesUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/22 0022.
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private IFileService iFileService;
    @Autowired
    private CacheService tagCacheService;

    @RequestMapping("init.do")
    @ResponseBody
    public ServerResponse init(HttpSession session) {
            tagCacheService.initCache();

        return ServerResponse.createBySuccess();

    }


    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {

            //发布之后相当于在WEB-INF下创建一个upload文件夹
//            String path = request.getSession().getServletContext().getRealPath("upload");
            String path = this.getClass().getResource("/").getPath();
            ServletContext context = session.getServletContext();

            InputStream input = context.getResourceAsStream("web.xml");
            System.out.println(context.getRealPath("/"));
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);

    }

}
