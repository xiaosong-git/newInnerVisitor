package com.xiaosong.util;

import com.hj.biz.bean.BizFaceBean;
import com.hj.biz.bean.RetMsg;
import com.hj.jni.bean.HJFaceFeature;
import com.hj.jni.bean.HJFaceModel;
import com.hj.jni.itf.*;
import com.hj.jni.utils.*;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FaceModuleUtil {
    private static HJFaceDrive hjFaceDrive = new HJFaceDrive();

    private static HJFaceLm faceLm = new HJFaceLm();
    /**
     * @param faceDetectEnginList 人脸检测引擎队列
     */
    private static FaceDetectEngineList faceDetectEngineList = FaceDetectEngineList.getInstance();
    /**
     * @param faceFeatureEnginList 人脸特征值引擎队列
     */
    private static FaceFeatureEngineList faceFeatureEngineList = FaceFeatureEngineList.getInstance();
    /**
     * @param FacePropertyEngineList 人脸属性值引擎队列
     */
    private static FacePropertyEngineList facePropertyEngineList = FacePropertyEngineList.getInstance();
    /**
     * @param FacePropertyEngineList 人脸部件引擎队列
     */
    private static FaceFlagEngineList faceFlagEngineList = FaceFlagEngineList.getInstance();

    private static ConcurrentLinkedQueue<Long> faceLmEngineList = new ConcurrentLinkedQueue<>();

    private static FaceBiz faceBiz = new FaceBiz();

    private static HJFaceProperty hjFaceProperty = new HJFaceProperty();

    private static HJFaceFlag hjFaceFlag = new HJFaceFlag();

    private static AtomicInteger detectEngineSize = new AtomicInteger(0);

    private static AtomicInteger featureEngineSize = new AtomicInteger(0);

    private static AtomicInteger propertyEngineSize = new AtomicInteger(0);

    private static AtomicInteger flagEngineSize = new AtomicInteger(0);


    /**
     * 获取检测引擎ID
     *
     * @param retryTimes 重试次数
     */
    private static long getDetectEngineId(int retryTimes) {
        Long engineId = faceDetectEngineList.poll();
        int count = 0;
        while ((engineId == null || engineId == 0) && count < retryTimes) {
            engineId = faceDetectEngineList.poll();
            try {
                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return engineId != null ? engineId : 0;
    }

    /**
     * 获取建模引擎ID
     *
     * @param retryTimes 重试次数
     */
    private static long getFeatureEngineId(int retryTimes) {
        Long engineId = faceFeatureEngineList.poll();
        int count = 0;
        while ((engineId == null || engineId == 0) && count < retryTimes) {
            engineId = faceFeatureEngineList.poll();
            try {
                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return engineId != null ? engineId : 0;
    }

    public static long getPropEngineId(int limitSize) {
        Long engineId = facePropertyEngineList.poll();
        int count = 0;
        while ((engineId == null || engineId == 0) && count < limitSize) {
            engineId = facePropertyEngineList.poll();
            try {
                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return engineId != null ? engineId : 0;
    }

    public static long getFlagEngineId(int limitSize) {
        Long engineId = faceFlagEngineList.poll();
        int count = 0;
        while ((engineId == null || engineId == 0) && count < limitSize) {
            engineId = faceFlagEngineList.poll();
            try {
                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return engineId != null ? engineId : 0;
    }

//    ,int eyeCross,int rollAngl,int confidence

    /**
     * 初始化人脸检测引擎
     * @param num 初始化检测引擎数量
     * @param eyeCross 眼间距
     * @param rollAngl 偏转角度
     * @param confidence 置信度
     */
    public static void initDetectEngine(int num,int eyeCross,int rollAngl,int confidence) {
        System.out.println("initDetectEngine before");
        for (int i = 0; i < num; i++) {
            long detectEngineId = hjFaceDrive.HJDetectEngineIntial(eyeCross, rollAngl, confidence);
            if(detectEngineId>0){
                faceDetectEngineList.add(detectEngineId);
                detectEngineSize.getAndIncrement();
            }else{
                System.out.println("引擎初始化异常故障码"+detectEngineId);
                System.out.println(Constant.resultCodeMap.get(detectEngineId));
            }
        }
    }

    /**
     * 初始化人脸特征值引擎
     */
    public static void initFeatureEngine(int num) {
        System.out.println("initFeatureEngine before");
        for (int i = 0; i < num; i++) {
            long featureEngineId = hjFaceDrive.HJRecognizeEngineIntial();
            if (featureEngineId>0){
                faceFeatureEngineList.add(featureEngineId);
                featureEngineSize.getAndIncrement();
            }else{
                System.out.println("引擎初始化异常故障码"+featureEngineId);
                System.out.println(Constant.resultCodeMap.get(featureEngineId));
            }
        }
    }


    /**
     * 初始化人脸检测引擎
     */
    public static void initDetectFaceProperty(int num) {
        System.out.println("initDetectFaceProperty before");
        for (int i = 0; i < num; i++) {
            long propertyId = hjFaceProperty.HJFacePropertyInit();
            System.out.println("detect face property init:" + propertyId);
            facePropertyEngineList.add(propertyId);
            propertyEngineSize.getAndIncrement();
        }
    }

    /**
     * 初始化人脸检测关键点引擎
     */
    public static void initDetectFaceFlag(int num) {
        for (int i = 0; i < num; i++) {
            long flagId = hjFaceFlag.HJFaceFlagInit();
            System.out.println("detect face flag init:" + flagId);
            faceFlagEngineList.add(flagId);
            flagEngineSize.getAndIncrement();
        }
    }

    /**
     * 初始化人脸检测引擎
     */
    public static void initDetectFaceLm(int num) {
        for (int i = 0; i < num; i++) {
            long lmId = faceLm.HJFaceLmInit();
            System.out.println("detect face lm init:" + lmId);
            faceLmEngineList.add(lmId);
        }
    }

    public static void initEngine(int initNum,int eyeCross,int rollAngl,int confidence) {
        // driver num: 4; init num:4  init(0~3)
        // driver num: 4; init num:6  init(0~3,0~2)
        // driver num: 6; init num:4  init(0~3)
        initNum = initNum <= 0 ? 1 : initNum;
        System.out.println("init num:" + initNum);
        initDetectEngine(initNum,eyeCross,rollAngl,confidence);
        initFeatureEngine(initNum);
        initDetectFaceProperty(initNum);
        initDetectFaceFlag(initNum);
        initDetectFaceLm(initNum);
    }

//    public data void initFixedEngine(int initNum,int driverIndex) throws Exception {
//        initNum = initNum <= 0 ? 1 : initNum;
//        driverIndex = driverIndex < 0 ? 0 : driverIndex;
//        System.out.println("init num:" + initNum);
//        System.out.println("driver index :" + driverIndex);
//        for (int i = 0;i < initNum;i++) {
//            FaceModuleUtil.initDetectEngineEx(driverIndex);
//            FaceModuleUtil.initFeatureEngineEx(driverIndex);
//            FaceModuleUtil.initDetectFaceProperty();
//        }
//    }


//    public data void initFeatureEngineEx(int index) {
//        System.out.println("initFeatureEngine before");
//        long featureEngineId = hjFaceDrive.HJRecognizeEngineIntialEx(index);
//        System.out.println("face feature engine init:" + featureEngineId);
//        faceFeatureEngineList.add(featureEngineId);
//    }

    /**
     * 检测人脸，并生成模型
     *
     * @param base64Str base64格式图片
     */
    public static RetMsg buildFaceModel(String base64Str) {
        RetMsg retMsg = new RetMsg();
        //检测引擎ID
        Long detectId = getDetectEngineId(0);
        //建模引擎ID
        Long featureId = getFeatureEngineId(0);
        try {
            if (detectId != null && detectId != 0l && featureId != null && featureId != 0) {
//                BufferedImage sourceImg = GetBufferedImage(base64Str);
//                byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
                HJFaceFeature hjFaceFeature = new HJFaceFeature();
                int result = hjFaceDrive.HJFaceModel(detectId,featureId,base64Str,hjFaceFeature);
                retMsg.setResult_code(result);
                retMsg.setResult_desc(Constant.resultCodeMap.get(result));
                if(result >= 0){
                    retMsg.setResult_code(0);
                    retMsg.setContent(hjFaceFeature);
                }
            } else {
                retMsg.setResult_code(-101);
                retMsg.setResult_desc(Constant.resultCodeMap.get(-101));
            }
        } catch (Exception e) {
            e.printStackTrace();
            retMsg.setResult_code(500);
            retMsg.setContent(e.getMessage());
        }finally {
            if(!faceDetectEngineList.contains(detectId)){
                faceDetectEngineList.add(detectId);
            }
            if(!faceFeatureEngineList.contains(featureId)){
                faceFeatureEngineList.add(featureId);
            }
        }
        return retMsg;
    }


    /**
     * 检测人脸，并生成模型
     *
     * @param base64Str base64格式图片
     * @param faceNum   期望人脸数量
     */
    public static RetMsg buildFaceModel(String base64Str, int faceNum) throws Exception {
        RetMsg retMsg = new RetMsg();
        Long engineId = getDetectEngineId(0);
        try {
            if (engineId != null && engineId != 0l) {
                BufferedImage sourceImg = GetBufferedImage(base64Str);

                byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
                ArrayList<HJFaceModel> hjFaceModels = new ArrayList<HJFaceModel>();
                for (int i = 0; i < faceNum; i++) {
                    hjFaceModels.add(new HJFaceModel());
                }
                int result = hjFaceDrive.HJDetectFace(engineId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(), faceNum, hjFaceModels);
                faceDetectEngineList.add(engineId);
                retMsg.setResult_code(result);
                retMsg.setResult_desc(Constant.resultCodeMap.get(result));
            } else {
                retMsg.setResult_code(-101);
                retMsg.setResult_desc(Constant.resultCodeMap.get(-101));
            }
        } catch (Exception e) {
            e.printStackTrace();
            retMsg.setResult_code(500);
            retMsg.setContent(e.getMessage());
            if(!faceDetectEngineList.contains(engineId)){
                faceDetectEngineList.add(engineId);
            }
        }
        return retMsg;
    }

    /**
     * 检测人脸，并生成模型
     * @param sourceImg    二进制图片
     * @param img    二进制图片
     * @param hjFaceModels 人脸模型列表
     */
    private static RetMsg buildFaceFeature(BufferedImage sourceImg, byte[] img, ArrayList<HJFaceModel> hjFaceModels) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg.setResult_code(0);
        ArrayList<HJFaceFeature> hjFaceFeatures = new ArrayList<>();
        Long engineId = getFeatureEngineId(0);
        if (engineId != null && engineId != 0L) {
            try {
                for (HJFaceModel hjFaceModel : hjFaceModels) {
                    HJFaceFeature hjFaceFeature = new HJFaceFeature();
                    int result = hjFaceDrive.HJFaceFeaturePick(engineId, img, sourceImg.getWidth(), sourceImg.getHeight(), hjFaceModel.getDwReserved(), hjFaceFeature);
                    hjFaceFeature.setResultCode(result);
                    hjFaceFeatures.add(hjFaceFeature);
                }
                retMsg.setContent(hjFaceFeatures);
            } catch (Exception e) {
                e.printStackTrace();
                retMsg.setResult_code(-100);
                retMsg.setResult_desc(Constant.resultCodeMap.get(-100));
            } finally {
                faceFeatureEngineList.add(engineId);
            }
        } else {
            retMsg.setResult_code(-100);
            retMsg.setResult_desc(Constant.resultCodeMap.get(-100));

        }         return retMsg;

    }



    /**
     * 添加人脸信息
     *
     * @param group   组名
     * @param key     人脸 id
     * @param feature 人脸模型
     */
    public static void addFaceModel(String group, String key, byte[] feature) {
        BizFaceBean tString = new BizFaceBean();
        tString.setKeyId(key);
        tString.setModelData(feature);
        faceBiz.HJInsertFaceToSet(group, tString);
    }

    /**
     * 1：N人脸比对
     *
     * @param feature1  人脸模型
     * @param group     组名
     * @param rtnNum    返回数量
     * @param threshold 比对阈值
     * @return RetMsg
     */
    public static String compareFaceInStore(byte[] feature1, String group, int rtnNum, int threshold) throws Exception {
        BizFaceBean bizFaceBean = new BizFaceBean();
        bizFaceBean.setModelData(feature1);
        String res = faceBiz.HJQryFaceFromSet(rtnNum, threshold, group, bizFaceBean);
        if (res == null) {
            throw new NullPointerException("compare result is null!!!");
        }
        return res;
    }

    public static float compareTwoFeature(byte[] feature1, byte[] feature2) {
        long compareId = hjFaceDrive.HJCompareEngineIntial();
        int result = hjFaceDrive.HJFaceFeatureCompare(compareId, feature1, feature2);
        hjFaceDrive.HJReleaseCompare(compareId);
        float score = (float) result / 100;
        return score;
    }

    //================================引擎释放==================================
    public static void releaseIdleDetectEngine(int minIdle) {
        while (faceDetectEngineList.size() > minIdle) {
            long engine = faceDetectEngineList.poll();
            detectEngineSize.getAndDecrement();
            releaseFaceDetectEngine(engine);
        }
    }

    public static void releaseIdleFeatureEngine(int minIdle) {
        while (faceFeatureEngineList.size() > minIdle) {
            long engine = faceFeatureEngineList.poll();
            featureEngineSize.getAndDecrement();
            releaseFaceFeatureEngine(engine);
        }
    }

    public static void releaseIdlePropEngine(int minIdle) {
        while (facePropertyEngineList.size() > minIdle) {
            long engine = facePropertyEngineList.poll();
            propertyEngineSize.getAndDecrement();
            releaseFacePropertyEngine(engine);
        }
    }

    public static void releaseIdleFlagEngine(int minIdle) {
        while (faceFlagEngineList.size() > minIdle) {
            long engine = faceFlagEngineList.poll();
            flagEngineSize.getAndDecrement();
            releaseFaceFlagEngine(engine);
        }
    }

    /**
     * 释放人脸检测引擎
     */
    public static void releaseFaceDetectEngine(long enginId) {
        hjFaceDrive.HJReleaseDetector(enginId);
    }

    /**
     * 释放特征值引擎
     */
    public static void releaseFaceFeatureEngine(long enginId) {
        hjFaceDrive.HJReleaseExtractor(enginId);
    }

    public static void releaseFacePropertyEngine(long enginId) {
        hjFaceProperty.HJFacePropertyRelease(enginId);

    }

    public static void releaseFaceFlagEngine(long enginId) {
        hjFaceFlag.HJFaceFlagRelease(enginId);
    }

    public static void releaseFaceLmEngine(long engineId) {
        faceLm.HJFaceLmRelease(engineId);
    }

    /**
     * BGR数组转图片
     */
    public static void writeImageFromArray(String imageFile, int width, int height, String type, byte[] gbrArray) {

        // 检测参数合法性
        if (null == gbrArray || gbrArray.length != width * height * 3)
            throw new IllegalArgumentException("invalid image description");
        // 将byte[]转为DataBufferByte用于后续创建BufferedImage对象
        DataBufferByte dataBuffer = new DataBufferByte(gbrArray, gbrArray.length);
        // sRGB色彩空间对象
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] nBits = {8, 8, 8};
        int[] bOffs = {2, 1, 0};
        ComponentColorModel colorModel = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width * 3, 3, bOffs, null);
        BufferedImage newImg = new BufferedImage(colorModel, raster, false, null);
        try {
            File file = new File(imageFile);
            ImageIO.write(newImg, type, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像类型转换：GIF->JPG、GIF->PNG、PNG->JPG、PNG->GIF(X)、BMP->PNG
     *
     * @throws IOException
     */
    public static byte[] getMatrixRBG(BufferedImage image) throws IOException {
        if (null == image)
            throw new NullPointerException();
        byte[] matrixRGB;
        if (isRGB3Byte(image)) {
            matrixRGB = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            // 转RGB格式
            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.TYPE_RGB), null).filter(image, rgbImage);
            matrixRGB = (byte[]) rgbImage.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        }
        return matrixRGB;
    }

    /**
     * 对图像解码返回BGR格式矩阵数据
     *
     * @param image
     * @return
     */
    public static byte[] getMatrixBGR(BufferedImage image) {
        byte[] matrixBGR;

        if (isBGR3Byte(image)) {
            matrixBGR = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            // ARGB格式图像数据
            int intrgb[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            matrixBGR = new byte[image.getWidth() * image.getHeight() * 3];
            // ARGB转BGR格式
            for (int i = 0, j = 0; i < intrgb.length; ++i, j += 3) {
                matrixBGR[j] = (byte) (intrgb[i] & 0xff);
                matrixBGR[j + 1] = (byte) ((intrgb[i] >> 8) & 0xff);
                matrixBGR[j + 2] = (byte) ((intrgb[i] >> 16) & 0xff);
            }
        }
        return matrixBGR;
    }

    /**
     * @param image
     * @param bandOffset 用于判断通道顺序
     * @return
     */
    private static boolean equalBandOffsetWith3Byte(BufferedImage image, int[] bandOffset) {
        if (null == image) {
            System.out.println("image is null........");
            return false;
        }
        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            if (image.getData().getSampleModel() instanceof ComponentSampleModel) {
                ComponentSampleModel sampleModel = (ComponentSampleModel) image.getData().getSampleModel();
                if (Arrays.equals(sampleModel.getBandOffsets(), bandOffset)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断图像是否为BGR格式
     *
     * @return 结果
     */
    public static boolean isBGR3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{0, 1, 2});
    }

    /**
     * 判断图像是否为RGB格式
     *
     * @return 结果
     */
    public static boolean isRGB3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{2, 1, 0});
    }

    /**
     * base64位图片转BufferedImage
     *
     * @param base64string 图片base64数据
     */
    public static BufferedImage GetBufferedImage(String base64string) {
        BufferedImage image = null;
        InputStream stream=null;
        try {
             stream = BaseToInputStream(base64string);
            image = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (stream!=null){
                    stream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return image;
    }

    /**
     * base64位图片转IO流
     *
     * @param base64string 图片base64数据
     */
    public static InputStream BaseToInputStream(String base64string) {
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;

    }


}
