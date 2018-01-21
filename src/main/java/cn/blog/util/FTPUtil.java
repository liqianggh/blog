package cn.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
  * @Description: 基于FTPClient的文件上传工具
  * Created by Jann Lee on 2018/1/20  21:01.
  */
 @Slf4j
public class FTPUtil {
     //获取配置文件中配置的ftp服务器的ip,登录用户名,密码。
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUsername = PropertiesUtil.getProperty("ftp.username");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");

    private String ip;
    private String username;//用户名
    private int port;//端口
    private String password;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.port = port;
        this.password = password;
    }

     /**
       * @Description:连接文件服务器(使用默认端口)
       * Created by Jann Lee on 2018/1/18  18:54.
       */
    public  boolean connectFtpServer(String ip,String username,String password,Integer port){
        ftpClient = new FTPClient();
        boolean flag = false;
        try {
            ftpClient.connect(ip);
            //登录
            flag = ftpClient.login(username,password);
            log.info("登录/连接服务器结果:"+flag);
        } catch (IOException e) {
            log.error("连接/登录文件服务器报错！",e);
        }
        return flag;
    }

     /**
      * @Description: 上传文件的具体逻辑
      * Created by Jann Lee on 2018/1/20  21:20.
      */
    public   boolean uploadFile(List<File> fileList,String remotePath) throws IOException {
        boolean flag = false;
        FileInputStream inputStream = null;
        //连接ftp服务器
        flag = this.connectFtpServer(this.ip,this.username,this.password,this.port);
        if(flag){
            try {
                //文件夹设置
                ftpClient.changeWorkingDirectory(remotePath);
                //缓冲区大小
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                //指定文件类型，二进制文件
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                for(File fileItem:fileList){
                    inputStream=new FileInputStream(fileItem);

                    flag = ftpClient.storeFile(fileItem.getName(),inputStream);
                }
            } catch (IOException e) {
                log.error("上传文件异常！",e);
                flag = false;
            }finally {
                inputStream.close();
                ftpClient.disconnect();
            }
        }
        return flag;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUsername,ftpPassword);
        log.info("开始连接服务器");
        boolean result = ftpUtil.uploadFile(fileList,"blog");
        log.info("开始链接ftp服务器，结束上传，上传结果{}",result);
        return result;
    }


}
