package com.xiaosong.common.imgServer.img;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.compose.Result;
import com.xiaosong.util.ConsantCode;

import java.io.File;


public class ImageController extends Controller {
	private Log log= Log.getLog(ImageController.class);
	/**
	 * 图片上传（多个）
	 * @throws Exception
	 */
	public void uploadMore() {
		try {
			renderJson(ImageService.me.uploadMore(getFiles("imgMore"),get("userId")));
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
		}
	}
	public void uploadSing(){
		try {
			renderJson(ImageService.me.uploadSing(getFile("imgSing"),get("userId")));
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
		}
	}
     
}
