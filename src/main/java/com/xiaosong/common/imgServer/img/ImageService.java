package com.xiaosong.common.imgServer.img;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import org.apache.commons.io.FileUtils;

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
		return upload(myfile, userId, newPath);

	}

	public Result gainBankIcon(UploadFile myfile, String userId) {
		String newPath=MainConfig.p.get("imageSaveDir") + File.separator + userId + File.separator;
		return upload(myfile, userId, newPath);
	}
	public Result upload(UploadFile myfile, String userId,String newPath){
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
					.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$")) {
				myfile.getFile().delete();
			}else {
				File file = myfile.getFile();
				String newFileName = System.currentTimeMillis() + "";
				String fileNameType = originalFilename.substring(originalFilename.lastIndexOf("."));
				realFileName = newFileName + fileNameType;
				try {
					FileUtils.moveFile(file,new File(newPath+realFileName));
				} catch (IOException e) {
					log.error("移动文件{}失败",myfile.getFileName(),e);
					return Result.unDataResult("fail","提交失败");
				}

			}
			map.put("imageFileName",realFileName);
			return ResultData.dataResult("success", "提交成功",map );
		}

	}

	public Result gainDate(UploadFile file, String userId, String type, String ad) {
		String path = "user" + File.separator + userId;
		File files = null;
		String realFileName = "";
		ResultData upload = (ResultData)upload(file, userId, path);
		if ("success".equals(upload.getVerify().get("sign"))){
			Object data = upload.getData();

		}
		return Result.unDataResult("success","成功");
	}
}
