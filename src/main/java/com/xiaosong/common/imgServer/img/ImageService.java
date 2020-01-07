package com.xiaosong.common.imgServer.img;

import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.xiaosong.MainConfig;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
		if (myfile.getFile()==null) {
			log.info("{}文件未上传",userId);
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
				try {
					FileUtils.moveFile(file,new File(MainConfig.p.get("imageSaveDir") + File.separator + userId + File.separator + originalFilename));
				} catch (IOException e) {
					log.error("移动文件{}失败",myfile.getFileName(),e);
					return Result.unDataResult("fail","提交失败");
				}

			}
		}
    	return Result.unDataResult("success","提交成功");
	}

	public Result gainBankIcon(UploadFile myfile, String userId) {
		return null;
	}
}
