package com.xiaosong.common.imgServer.img;

import com.hj.jni.itf.Constant;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.util.ConsantCode;


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
			renderJson(ImageService.me.uploadSing(getFile(),get("userId")));
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
		}
	}
	public void gainBankIcon(){
		try {
			renderJson(ImageService.me.gainBankIcon(getFile(),get("userId")));
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
		}
	}
	/**
	 * 银行卡,身份证，图片、人脸图片
	 * @throws Exception
	 *  update by cwf  2019/9/6 15:26 Reason: 增加一个type=4 其他图片
	 */
	public void gainData(){
		try {
			renderJson(ImageService.me.gainDate(getFile(),get("userId"),get("type"),get("ad")));
		}catch (Exception e){
			log.error(e.getMessage());
			renderJson( Result.unDataResult(ConsantCode.FAIL, "系统异常"));
		}
	}
     public void index(){

		 renderText(String.valueOf(Constant.TEMPLATE_ROLL_ANGL));
	 }
}
