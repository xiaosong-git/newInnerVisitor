package com.xiaosong.common.imgServer.file;

import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.common.imgServer.img.ImageController;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: jfinal_demo_for_maven
 * @description: 文件上传
 * @author: cwf
 * @create: 2020-01-08 17:09
 **/
public class FileService {

    private Log log= Log.getLog(ImageController.class);
    public static final FileService me = new FileService();
    /**
     *
     * @param myfiles 支持多文件上传
     * @param resource 文件来源--根据来源修改前缀
     * @param suffix 文件后缀 --根据后缀生成目录
     * @return Result
     */
    public Result uploadMore(List<UploadFile> myfiles, String resource,String suffix) {
        List<String> list=new LinkedList<>();
        String prefix=MainConfig.p.get("prefix");
        int count=0;

        if ("app".equals(resource)){
            prefix=MainConfig.p.get("prefixApp");
            //文档也放在img文件夹中
        }else if ("img".equals(resource)){
            prefix= MainConfig.p.get("prefixImg");
        }
        String path= prefix+ File.separator+suffix;
        File file = new File(path);
        //不存在目录则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        for (UploadFile myfile:myfiles){
            if (myfile.getFile()==null) {
                log.info("文件未上传");
            } else {
                log.info("文件长度: " + myfile.getFile().length());
                log.info("文件类型: " + myfile.getContentType());
                log.info("文件名称: " + myfile.getFileName());
                log.info("文件原名: " + myfile.getOriginalFileName());
                    try {
                        FileUtils.moveFile(myfile.getFile(),new File(path+myfile.getOriginalFileName()));
                    } catch (IOException e) {
                        log.error("移动文件{}失败",myfile.getFileName(),e);
                        list.add(myfile.getOriginalFileName());
                        count++;
                    }
                }
            }
        if (count==0){
            return Result.unDataResult("success","提交成功");
        }else {
            return ResultData.dataResult("fail","未提交成功！",list);
        }

    }

}
