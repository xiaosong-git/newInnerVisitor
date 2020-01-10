package com.xiaosong.common.imgServer.img;

import com.alibaba.fastjson.JSONObject;
import com.hj.biz.bean.RetMsg;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.FaceModuleUtil;
import com.xiaosong.util.FilesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 图片
 **/
public class ImageService {
	Log log=Log.getLog(ImageService.class);
    public static final ImageService me = new ImageService();

    public Result uploadMore(List<UploadFile> myfiles, String userId) {
       List<String> list=new LinkedList<>();
        int count=0;
        for (UploadFile myfile:myfiles){
            if (myfile.getFile()==null) {
					log.info("文件未上传");
				} else {
				log.info("文件长度: " + myfile.getFile().length());
				log.info("文件类型: " + myfile.getContentType());
				log.info("文件名称: " + myfile.getFileName());
				log.info("文件原名: " + myfile.getOriginalFileName());
				String originalFilename = myfile.getOriginalFileName();
				if (!originalFilename
						.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$")) {
					count++;
					list.add(myfile.getOriginalFileName());
					myfile.getFile().delete();
				}else {
					File file = myfile.getFile();
					try {
						FileUtils.moveFile(file,new File(MainConfig.p.get("imageSaveDir") + File.separator + userId + File.separator + originalFilename));
					} catch (IOException e) {
						log.error("移动文件{}失败",myfile.getFileName(),e);
						list.add(myfile.getOriginalFileName());
					}

				}
			}
        }
		if (count==0){
			return Result.unDataResult("success","提交成功");
		}else {
			return ResultData.dataResult("fail","未提交成功！",list);
		}

    }
	public Result uploadSing(UploadFile myfile, String userId) {
    	String newPath=MainConfig.p.get("imageSaveDir") + File.separator + userId + File.separator;
		return uploadImg(myfile, userId, newPath);

	}

	public Result gainBankIcon(UploadFile myfile, String userId) {
		String newPath=MainConfig.p.get("imageSaveDir") + File.separator + userId + File.separator;
		return uploadImg(myfile, userId, newPath);
	}
	//上传图片
	public Result uploadImg(UploadFile myfile, String userId,String newPath ){
    	return upload(myfile, userId, newPath,".JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png");
	}
	//上传txt
	public Result uploadTxt(UploadFile myfile, String userId,String newPath ){
		return upload(myfile, userId, newPath,".txt");
	}
	public Result upload(UploadFile myfile, String userId,String newPath,String matches ){
		String realFileName = "";
		Map<String,Object> map=new HashMap<>();
		if (myfile.getFile()==null) {
			log.info("{}文件未上传",userId);
			return Result.unDataResult("fail","提交失败,文件未上传");
		} else {
			log.info("文件长度: " + myfile.getFile().length());
			log.info("文件类型: " + myfile.getContentType());
			log.info("文件名称: " + myfile.getFileName());
			log.info("文件原名: " + myfile.getOriginalFileName());
			String originalFilename = myfile.getOriginalFileName();
			if (!originalFilename
					.matches(".+("+matches+")$")) {
				myfile.getFile().delete();
				return Result.unDataResult("fail","提交失败,请确认提交"+matches+"格式");
			}else {
				File file = myfile.getFile();
				String newFileName = System.currentTimeMillis() + "";
				String fileNameType = originalFilename.substring(originalFilename.lastIndexOf("."));
				realFileName = newFileName + fileNameType;
				try {
					FileUtils.moveFile(file,new File(newPath+File.separator+realFileName));
					log.info("文件原名{},文件现名{}",myfile.getOriginalFileName(),realFileName);
				} catch (IOException e) {
					log.error("移动文件{}失败",myfile.getFileName(),e);
					return Result.unDataResult("fail","提交失败");
				}
			}
			map.put("imageFileName",realFileName);
			return ResultData.dataResult("success", "提交成功",map );
		}
	}
	/**
	 * @param file 文件
	 * @param userId 用户id
	 * @param type 1:银行卡 2.身份证 3.人脸图片 4.其他图片
	 * @param ad 是否广告
	 * @return Result
	 * @throws Exception
	 */
	public Result gainDate(UploadFile file, String userId, String type, String ad) throws Exception {
    	String path ="user" + File.separator + userId;
		String realFileName = "";
		if(!StringUtils.isBlank(ad)){
			path = "ad";
		}
		//真实地址
		String realPath=MainConfig.p.get("imageSaveDir")+path;
		//创建目录
		File localFile = new File(realPath);
		if (!localFile.exists()) {
			localFile.mkdirs();
		}
		Map<String,Object> map=new HashMap<>();
		//存在数据库中的地址
		ResultData uploadResult = (ResultData)uploadImg(file, userId, realPath);
		if ("success".equals(uploadResult.getVerify().get("sign"))){
			Object data = uploadResult.getData();
			map = JSONObject.parseObject(JSONObject.toJSONString(data), Map.class);
			//新文件名称
			realFileName = BaseUtil.objToStr(map.get("imageFileName"),"无");
		}else {
			return uploadResult;
		}
		//新图片地址
		String realFilePath=path + File.separator + realFileName;
		map.put("imageFileName",realFilePath);
		uploadResult.setData(map);
		//银行卡功能暂时无效
//		if ("1".equals(type)) {
//			// 银行卡
//			System.out.println("进入银行卡.............");
//			String bankCardData = bankCardDiscernService.getBankCardDiscern(files.getAbsolutePath());
//			//bankcardService.getBankCardData(files.getAbsolutePath());
//			System.out.println("bankCardData:" + bankCardData);
//			ResponseBank bankObj = (ResponseBank) JsonUtils.toObj(bankCardData, ResponseBank.class);
//			if (bankObj.getCardNumber() == null)  {
//
//				return ResultData.dataResult("ocr","识别失败",map);
//			}
//		}
		if("3".equals(type)){
			System.out.println("进入人脸图片........");
			String absolutePath=MainConfig.p.get("imageSaveDir")+realFilePath;
			//获取文件路径
			log.info("文件路径:{}",absolutePath);
			// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
			String pic64_1 = FilesUtils.ImageToBase64ByLocal(absolutePath);
			//创建人脸模型
			RetMsg retMsg = FaceModuleUtil.buildFaceModel(pic64_1,1);
			//调用人像识别，判断是否符合
			if( retMsg.getResult_code()>0&&retMsg.getResult_code()!=500){
				return uploadResult;
				//失败移除文件
			}else {
				log.info("识别失败人像："+absolutePath);
				return Result.unDataResult("fail","人脸识别失败，请重新提交人脸图片");
			}
		}if("4".equals(type)){
			log.info("进入普通图片........{}",realFilePath);
		}
		return uploadResult;
	}
}
