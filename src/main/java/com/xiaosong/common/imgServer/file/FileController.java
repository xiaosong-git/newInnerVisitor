package com.xiaosong.common.imgServer.file;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.common.imgServer.img.ImageService;
import com.xiaosong.compose.Result;
import com.xiaosong.util.ConsantCode;

import java.util.concurrent.*;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2020-01-08 16:56
 **/
public class FileController extends Controller {
    Log log =Log.getLog(FileController.class);
    public void uploadMore() {
        try {
            renderJson(FileService.me.uploadMore(getFiles("imgMore"),get("resource"),get("suffix")));
        }catch (Exception e){
            log.error(e.getMessage());
            renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
        }
    }
}
