package com.xiaosong.common.imgServer.img;

import com.jfinal.upload.UploadFile;
import com.xiaosong.compose.Result;

/**
 * 图片
 **/
public class ImageService {
    public static final ImageService me = new ImageService();

    public Result uploadMore(UploadFile myfiles, String userId) {
//        for (MultipartFile)
//		try {
//			for (MultipartFile myfile : myfiles) {
//				if (myfile.isEmpty()) {
//					System.out.println("文件未上传");
//				} else {
//					System.out.println("文件长度: " + myfile.getSize());
//					System.out.println("文件类型: " + myfile.getContentType());
//					System.out.println("文件名称: " + myfile.getName());
//					System.out.println("文件原名: " + myfile.getOriginalFilename());
//					String originalFilename = myfile.getOriginalFilename();
//					if (!originalFilename
//							.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$")) {
//						isSucc = false;
//						break;
//					} else {
//						String  realPath = ImageConfig.imageSaveDir + File.separator + userId;//paramsService.findByParamName("imageSaveDir").getParamText() + File.separator + userId;
//
//						File file = new File(realPath);
//						if (!file.exists()) {
//							file.mkdirs();
//						}
//						// 这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉，我是看它的源码才知道的
//
//						String newFileName = myfile.getOriginalFilename();
//						int index = originalFilename.lastIndexOf(".");
//						if (index < 0) {
//
//						}
//						String fileNameType = originalFilename.substring(index,
//								originalFilename.length());
//						FileUtils.copyInputStreamToFile(
//								myfile.getInputStream(), new File(realPath,
//										newFileName + fileNameType));
//
//						System.out.println("上传成功");
//					}
//
//				}
//
//			}
//			if (isSucc) {
//				base.setSign("success");
//				base.setDesc("提交成功");
////				ResponseData data = new ResponseData();
////				data.setImageFileName(realFileName);
////				obj.setData(data);
//			} else {
//				base.setSign("fail");
//				base.setDesc("提交失败");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			base.setSign("fail");
//			base.setDesc("提交失败");
//		} finally {
//			obj.setVerify(base);
//			json = JsonUtils.toJson(obj);
//			System.out.println(json);
//			ResponseUtil.responseJson(response, json);
//		}
        return Result.unDataResult("success","提交成功");
    }
}
