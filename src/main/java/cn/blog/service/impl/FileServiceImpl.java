package cn.blog.service.impl;

import cn.blog.service.IFileService;
import cn.blog.util.FTPUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
  * @Description: 文件上传业务逻辑
  * Created by Jann Lee on 2018/1/21  2:29.
  */
@Slf4j
@Service("iFileService")
public class FileServiceImpl  implements IFileService{

    /**
     * 文件上传：先上传本地，在由本地上传到文件服务器
     * @param file 前端传来的数据
     * @param path 要存储的路径
     * @return
     */
    @Override
    public String upload(MultipartFile file, String path) {
        //获取文件名
        String fileName = file.getOriginalFilename();

        //获取扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);

        //重命名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        log.info("开始上传文件，上传文件名:{},上传路径:{},新文件名{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            //设置文件写权限而后创建文件夹
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            //上传到本地文件
            file.transferTo(targetFile);
            //上传到文件服务器
            boolean flag = FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传成功后删除本地文件
            if(flag){
                targetFile.delete();
            }
        } catch (IOException e) {
            log.error("上传文件异常！",e);
            return null;
        }

        return targetFile.getName();
    }
}
