package com.xiaosong.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hj.biz.bean.BizFaceBean;
import com.hj.biz.bean.RetMsg;
import com.hj.jni.bean.*;
import com.hj.jni.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FaceModuleUtil {
    private static HJFaceDrive hjFaceDrive = new HJFaceDrive();
    private static Logger logger= Logger.getLogger(FaceModuleUtil.class);
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
     * @param FacePropertyEng   ineList 人脸部件引擎队列
     */
    private static FaceFlagEngineList faceFlagEngineList = FaceFlagEngineList.getInstance();

    private static ConcurrentLinkedQueue<Long> faceLmEngineList = new ConcurrentLinkedQueue<>();

//    private static RailwayFaceBiz railwayFaceBiz = new RailwayFaceBiz();

    private static FaceBiz railwayFaceBiz = new FaceBiz();

    private static HJFaceProperty hjFaceProperty = new HJFaceProperty();

    private static HJFaceFlag hjFaceFlag = new HJFaceFlag();

    private static AtomicInteger detectEngineSize = new AtomicInteger(0);

    private static AtomicInteger featureEngineSize = new AtomicInteger(0);

    private static AtomicInteger propertyEngineSize = new AtomicInteger(0);

    private static AtomicInteger flagEngineSize = new AtomicInteger(0);



    /**
     * 获取检测引擎
     */
    public static long getEngineId(ConcurrentLinkedQueue<Long> concurrentLinkedQueue) {
        Long engineId = concurrentLinkedQueue.poll();
//        int count = 0;
//        while ((engineId == null || engineId == 0) && count < 20) {
//            engineId = concurrentLinkedQueue.poll();
//            try {
//                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
//                count++;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        while (engineId == null || engineId == 0) {
            engineId = concurrentLinkedQueue.poll();
        }
        return engineId != null ? engineId : 0;
    }

    public static long getDetectEngineId(int countNum) {
        Long engineId = faceDetectEngineList.poll();
        int count = 0;
        while ((engineId == null || engineId == 0) && count < countNum) {
            engineId = faceDetectEngineList.poll();
            try {
                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (engineId == null || engineId == 0) {
            engineId = faceDetectEngineList.poll();
        }
        return engineId != null ? engineId : 0;
    }


    public static long getFeatureEngineId(int countNum) {
        Long engineId = faceFeatureEngineList.poll();
        int count = 0;
        while ((engineId == null || engineId == 0) && count < countNum) {
            engineId = faceFeatureEngineList.poll();
            try {
                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 无空闲引擎时，直接返回
        while (engineId == null || engineId == 0) {
            engineId = faceFeatureEngineList.poll();
        }
        return engineId != null ? engineId : 0;
    }

    public static long getPropEngineId(int limitSize){
        Long engineId = facePropertyEngineList.poll();
//        int count = 0;
//        while ((engineId == null || engineId == 0) && count < 20) {
////            if (propertyEngineSize.get() < limitSize) {
////                initDetectFaceProperty();
////                engineId = facePropertyEngineList.poll();
////                break;
////            }
//            engineId = facePropertyEngineList.poll();
//            try {
//                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
//                count++;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        // 无空闲引擎时，直接返回
        while (engineId == null || engineId == 0) {
            engineId = facePropertyEngineList.poll();
        }
        return engineId != null ? engineId : 0;
    }

    public static long getFlagEngineId(int limitSize){
        Long engineId = faceFlagEngineList.poll();
//        int count = 0;
//        while ((engineId == null || engineId == 0) && count < 20) {
////            if (flagEngineSize.get() < limitSize) {
////                initDetectFaceFlag();
////                engineId = faceFlagEngineList.poll();
////                break;
////            }
//            engineId = faceFlagEngineList.poll();
//            try {
//                Thread.sleep(Constant.REQUEST_THREAD_WAIT_TIME);
//                count++;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        // 无空闲引擎时，直接返回
        while (engineId == null || engineId == 0) {
            engineId = faceFlagEngineList.poll();
        }
        return engineId != null ? engineId : 0;
    }

    /**
     * 检测
     *
     * @param data
     * @return
     */
    public static RetMsg genHjFaceModule(String data) {
        RetMsg retMsg = new RetMsg();
        //使用人脸图片数据进行人脸识别和建模
        long getEngineIdTime = System.currentTimeMillis();
        long engineId = FaceModuleUtil.getDetectEngineId(16);
        if (engineId != 0) {
            try {
                retMsg = FaceModuleUtil.detectFacePic(engineId, data, 1);
                System.out.println("Result_code:"+retMsg.getResult_code());
            } catch (Exception e) {
                System.out.println("人脸检测报错！");
                e.printStackTrace();
                retMsg.setResult_code(Constant.ErrorCode.NO_FACE_DETECT);
                retMsg.setResult_desc("人脸检测失败!");
            } finally {
                //归还人脸检测引擎
                faceDetectEngineList.add(engineId);
                System.out.println("face detect cost " + (System.currentTimeMillis() - getEngineIdTime) + "ms");
            }
        } else {
            retMsg.setResult_code(Constant.ErrorCode.SERVER_NO_REACT);
            retMsg.setResult_desc("当前服务器没有响应，请稍后重试");
        }
        return retMsg;
    }
    /**
     * 初始化人脸检测引擎和特征值引擎
     */
    public static void initDetectEngine() {
        System.out.println("initDetectEngine before");
        long detectEngineId = hjFaceDrive.HJDetectEngineIntial(Constant.TEMPLATE_EYE_CROSS, Constant.TEMPLATE_ROLL_ANGL, Constant.TEMPLATE_CONFIDENCE);
        System.out.println("detect engine init:" + detectEngineId);
        faceDetectEngineList.add(detectEngineId);
    }

//    public data void initDetectEngineEx(int index) throws Exception {
//        System.out.println("initDetectEngineEx before");
//        long detectEngineId = hjFaceDrive.HJDetectEngineIntialEx(Constant.TEMPLATE_EYE_CROSS, Constant.TEMPLATE_ROLL_ANGL, Constant.TEMPLATE_CONFIDENCE,index);
//        System.out.println("detect engine ex init:" + detectEngineId);
//        faceDetectEngineList.add(detectEngineId);
//    }

    public static void initDetectFaceProperty() {
        System.out.println("initDetectFaceProperty before");
        long propertyId = hjFaceProperty.HJFacePropertyInit();
        System.out.println("detect face property init:" + propertyId);
        facePropertyEngineList.add(propertyId);
        propertyEngineSize.getAndIncrement();
    }

    public static void initEngine(int initNum) {
        // driver num: 4; init num:4  init(0~3)
        // driver num: 4; init num:6  init(0~3,0~2)
        // driver num: 6; init num:4  init(0~3)
        initNum = initNum <= 0 ? 1 : initNum;
        System.out.println("init num:" + initNum);
        for (int i = 0;i < initNum;i++) {
            initDetectEngine();
            initFeatureEngine();
            initDetectFaceProperty();
            initDetectFaceFlag();
            initDetectFaceLm();
        }
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

    public static void initDetectFaceFlag() {
        long flagId = hjFaceFlag.HJFaceFlagInit();
        System.out.println("detect face flag init:" + flagId);
        faceFlagEngineList.add(flagId);
        flagEngineSize.getAndIncrement();
    }

    public static void initDetectFaceLm() {
        long lmId = faceLm.HJFaceLmInit();
        System.out.println("detect face lm init:" + lmId);
        faceLmEngineList.add(lmId);
    }

    /**
     * 初始化人脸特征值引擎
     */

    public static void initFeatureEngine() {
        System.out.println("initFeatureEngine before");
        long featureEngineId = hjFaceDrive.HJRecognizeEngineIntial();
        System.out.println("face feature engine init:" + featureEngineId);
        faceFeatureEngineList.add(featureEngineId);
        featureEngineSize.getAndIncrement();
    }

//    public data void initFeatureEngineEx(int index) {
//        System.out.println("initFeatureEngine before");
//        long featureEngineId = hjFaceDrive.HJRecognizeEngineIntialEx(index);
//        System.out.println("face feature engine init:" + featureEngineId);
//        faceFeatureEngineList.add(featureEngineId);
//    }

    /**
     * 检测人脸，并生成模型
     *
     * @param engineId  人脸检测引擎ID
     * @param base64Str base64格式图片
     * @param faceNum   人脸数量
     */
    public static RetMsg detectFacePic(long engineId, String base64Str, int faceNum) {
        RetMsg retMsg = new RetMsg();
        BufferedImage sourceImg = GetBufferedImage(base64Str);
        byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
        ArrayList<HJFaceModel> hjFaceModels = new ArrayList<HJFaceModel>();
        for (int i = 0; i < faceNum; i++) {
            hjFaceModels.add(new HJFaceModel());
        }
        try {
            int result = hjFaceDrive.HJDetectFace(engineId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(), faceNum, hjFaceModels);

            if (result > 0) {

                retMsg.setResult_code(0);
                retMsg.setContent(hjFaceModels);
            } /*else if (result == 0) {
                retMsg.setResult_code(Constant.ErrorCode.NO_FACE_DETECT);
                retMsg.setResult_desc("人脸检测失败!,请重新上传!");
            }*/ else {
                retMsg.setResult_code(Constant.ErrorCode.NO_FACE_DETECT);
                retMsg.setResult_desc("人脸检测失败!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            retMsg.setResult_code(500);
            retMsg.setContent(e.getMessage());
        }

        return retMsg;
    }

    /**
     * 检测人脸，并生成模型,携带84坐标信息
     *
     * @param engineId  人脸检测引擎ID
     * @param base64Str base64格式图片
     * @param faceNum   人脸数量
     */
    public static RetMsg detectFacePicWithPoint(long engineId, String base64Str, int faceNum) {
        RetMsg retMsg = new RetMsg();
        BufferedImage sourceImg = GetBufferedImage(base64Str);
        byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
        ArrayList<HJFaceDetail> hjFaceDetails = new ArrayList<HJFaceDetail>();
        for (int i = 0; i < faceNum; i++) {
            hjFaceDetails.add(new HJFaceDetail());
        }
        ArrayList<HJFaceModel> hjFaceModels = new ArrayList<HJFaceModel>();

        try {
            int result = hjFaceDrive.HJDetectFacesDetail(engineId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(), faceNum, hjFaceDetails);

            ArrayList pointList = new ArrayList();
            Long startDetectPoints;
            for (int i = 0; i < result; i++) {
                startDetectPoints = System.currentTimeMillis();
                long lmId = getEngineId(faceLmEngineList);
                if (lmId == 0) {
                    retMsg.setResult_code(Constant.ErrorCode.SERVER_NO_REACT);
                    retMsg.setResult_desc("当前服务器繁忙，请稍后重试");
                    return retMsg;
                }
                HJFaceDetail hjFaceDetail = hjFaceDetails.get(i);
                HJFaceModel hjFaceModel = transToFaceModel(hjFaceDetail);
                if (hjFaceModel == null) {
                    continue;
                }
                hjFaceModels.add(hjFaceModel);
                try {
                    String coordinate = faceLm.HJDetectFaceLm(lmId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(), hjFaceModel);
                    JSONObject res = JSON.parseObject(coordinate);
                    JSONObject content = JSON.parseObject(res.getString("content"));
                    pointList.add(content.get("points"));
                    System.out.println(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("人脸84点检测发生错误 -> " + e.getMessage());
                } finally {
                    faceLmEngineList.add(lmId);
                    System.out.println("detect face 84 point cost:" + (System.currentTimeMillis() - startDetectPoints));
                }

            }


            if (result > 0) {
                retMsg.setResult_code(0);
                JSONObject content = new JSONObject();
                content.put("features",hjFaceModels);
                content.put("coordinates",pointList);
                retMsg.setContent(content);
//                faceCache(base64Str);
            } /*else if (result == 0) {
                retMsg.setResult_code(Constant.ErrorCode.NO_FACE_DETECT);
                retMsg.setResult_desc("人脸检测失败!,请重新上传!");
            }*/ else {
                retMsg.setResult_code(Constant.ErrorCode.NO_FACE_DETECT);
                retMsg.setResult_desc("人脸检测失败!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            retMsg.setResult_code(500);
            retMsg.setContent(e.getMessage());
        }
        return retMsg;
    }


    private static HJFaceModel transToFaceModel(HJFaceDetail hjFaceDetail) {
        if (hjFaceDetail == null) {
            return null;
        }
        HJFaceModel hjFaceModel = new HJFaceModel();
        hjFaceModel.setnQuality(hjFaceDetail.getnQuality());
        hjFaceModel.setNoseY(hjFaceDetail.getNoseY());
        hjFaceModel.setNoseX(hjFaceDetail.getNoseX());
        hjFaceModel.setPitch(hjFaceDetail.getPitch());
        hjFaceModel.setResultCode(hjFaceDetail.getResultCode());
        hjFaceModel.setRoll(hjFaceDetail.getRoll());
        hjFaceModel.setTop(hjFaceDetail.getTop());
        hjFaceModel.setYaw(hjFaceDetail.getYaw());
        hjFaceModel.setnEyeOpen(hjFaceDetail.getnEyeStatus());
        hjFaceModel.setRightEyeY(hjFaceDetail.getRightEyeY());
        hjFaceModel.setRightEyeX(hjFaceDetail.getRightEyeX());
        hjFaceModel.setnMouthOpen(hjFaceDetail.getnMouthStatus());
        hjFaceModel.setMouthY(hjFaceDetail.getMouthY());
        hjFaceModel.setMouthX(hjFaceDetail.getMouthX());
        hjFaceModel.setLeftEyeY(hjFaceDetail.getLeftEyeY());
        hjFaceModel.setLeftEyeX(hjFaceDetail.getLeftEyeX());
        hjFaceModel.setfConfidence(hjFaceDetail.getfConfidence());
        hjFaceModel.setBottom(hjFaceDetail.getBottom());
        hjFaceModel.setDwReserved(hjFaceDetail.getDwReserved());
        hjFaceModel.setLeft(hjFaceDetail.getLeft());
        hjFaceModel.setRight(hjFaceDetail.getRight());
        return hjFaceModel;
    }


//    public data RetMsg detectFacePicDetail(String base64Str, int faceNum) {
//        RetMsg retMsg = new RetMsg();
//        BufferedImage sourceImg = GetBufferedImage(base64Str);
//        byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
//        ArrayList<HJFaceDetail> hjFaceDetails = new ArrayList<HJFaceDetail>();
//        for (int i = 0; i < faceNum; i++) {
//            hjFaceDetails.add(new HJFaceDetail());
//        }
//        long engineId = getEngineId(faceDetectEngineList);
//        if (engineId != 0) {
//            try {
//                int result = hjFaceDrive.HJDetectFacesDetail(engineId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(), faceNum, hjFaceDetails);
//                if (result > 0) {
//                    retMsg.setResult_code(0);
//                    retMsg.setContent(hjFaceDetails);
//                } else if (result == 0) {
//                    retMsg.setResult_code(404);
//                    retMsg.setResult_desc("图片中找不到人脸,请重新上传!");
//                } else {
//                    retMsg.setResult_code(result);
//                    retMsg.setResult_desc("人脸检测失败!");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                retMsg.setResult_code(505);
//                retMsg.setContent(e.getMessage());
//            } finally {
//                //归还人脸检测引擎
//                faceDetectEngineList.add(engineId);
//            }
//        } else {
//            retMsg.setResult_code(Constant.ErrorCode.SERVER_NO_REACT);
//            retMsg.setResult_desc("当前服务器繁忙，请稍后重试");
//        }
//
//        return retMsg;
//    }

    public static PropertyResult detectFaceProperty(HJFaceModel hjFaceModel, String base64Str, int limitSize) throws Exception {
        long properId = getPropEngineId(limitSize);
        if (properId == 0) {
            return null;
        }
        JSONObject jsonObject;
        try {
            BufferedImage sourceImg = GetBufferedImage(base64Str);
            byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
            String result = hjFaceProperty.HJDetectFaceProperty(properId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(),hjFaceModel);
            jsonObject = JSON.parseObject(result);
            int resp_code = Integer.parseInt((String)jsonObject.get("resp_code"));
            if (resp_code != 0) {return null;}
        }finally {
            facePropertyEngineList.add(properId);
        }
        return JSON.parseObject(jsonObject.getString("content"), PropertyResult.class);
    }

    public static HJDetectFlagResult detectFaceFlag(HJFaceModel hjFaceModel, String base64Str) throws Exception {
        long flagId = getEngineId(faceFlagEngineList);
        if (flagId == 0) {
            return null;
        }
        HJDetectFlagResult flagResult = null;
        try {
            BufferedImage sourceImg = GetBufferedImage(base64Str);
            byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
            String result = hjFaceFlag.HJDetectFaceFlag(flagId, img, 24, sourceImg.getWidth(), sourceImg.getHeight(),hjFaceModel);
            JSONObject jsonObject = JSON.parseObject(result);
            int resp_code = Integer.parseInt((String)jsonObject.get("resp_code"));
            if (resp_code != 0) {
                return null;
            }
            flagResult = JSON.parseObject(jsonObject.getString("content"), HJDetectFlagResult.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            faceFlagEngineList.add(flagId);
        }
        return flagResult;
    }

    /**
     * 检测人脸，并生成模型
     *
     * @param engineId     特征值生成引擎ID
     * @param base64Str    base64格式图片
     * @param hjFaceModels 人脸模型列表
     */
    public static RetMsg buildFaceFeature(long engineId, String base64Str, ArrayList<HJFaceModel> hjFaceModels) throws Exception {
        RetMsg retMsg = new RetMsg();
        BufferedImage sourceImg = GetBufferedImage(base64Str);
        byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
        retMsg.setResult_code(0);
        ArrayList<HJFaceFeature> hjFaceFeatures = new ArrayList<>();
        for (HJFaceModel hjFaceModel : hjFaceModels) {
            HJFaceFeature hjFaceFeature = new HJFaceFeature();
            int result = hjFaceDrive.HJFaceFeaturePick(engineId, img, sourceImg.getWidth(), sourceImg.getHeight(), hjFaceModel.getDwReserved(), hjFaceFeature);
            hjFaceFeature.setResultCode(result);
            hjFaceFeatures.add(hjFaceFeature);
        }
        retMsg.setContent(hjFaceFeatures);
        return retMsg;
    }

    /**
     * 检测人脸，并生成模型
     */
    public static byte[] buildFaceModel(long engineId, String base64Str, HJFaceModel hjFaceModel) {
        BufferedImage sourceImg = GetBufferedImage(base64Str);
        byte[] img = getMatrixBGR(sourceImg);//图片转化BGR格式
        HJFaceFeature hjFaceFeature = new HJFaceFeature();
        hjFaceDrive.HJFaceFeaturePick(engineId, img, sourceImg.getWidth(), sourceImg.getHeight(), hjFaceModel.getDwReserved(), hjFaceFeature);
        return hjFaceFeature.getpFeature();
    }

    /**
     * 添加人脸信息
     * @param group 组名
     * @param key 人脸 id
     * @param feature 人脸模型
     */
    public static void addFaceModel(String group,String key,byte[] feature) {
        BizFaceBean tString = new BizFaceBean();
//        RailwayFaceBean tString = new RailwayFaceBean();
        tString.setKeyId(key);
        tString.setModelData(feature);
        railwayFaceBiz.HJInsertFaceToSet(group, tString);
    }

    /**
     * 1：N人脸比对
     * @param feature1 人脸模型
     * @param group 组名
     * @param rtnNum 返回数量
     * @param threshold 比对阈值
     * @return RetMsg
     */
    public static RetMsg compareFaceInStore(byte[] feature1,String group,int rtnNum,int threshold) throws Exception {
        BizFaceBean tString = new BizFaceBean();
//        RailwayFaceBean tString = new RailwayFaceBean();
        tString.setModelData(feature1);
        String res = railwayFaceBiz.HJQryFaceFromSet(rtnNum, threshold, group, tString);
        if (res == null) {
            throw new NullPointerException("compare result is null!!!");
        }
        return JSON.parseObject(res, RetMsg.class);
    }

    public static float compareTwoFeature(byte[] feature1,byte[] feature2) {
        long compareId = hjFaceDrive.HJCompareEngineIntial();
        int result = hjFaceDrive.HJFaceFeatureCompare(compareId, feature1, feature2);
        hjFaceDrive.HJReleaseCompare(compareId);
        float score =  (float) result / 100;
        return score;
    }

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
        try {
            InputStream stream = BaseToInputStream(base64string);
            image = ImageIO.read(stream);
            System.out.println(">>>" + image.getWidth() + "," + image.getHeight() + "<<<");
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * 裁剪图片中的人脸,并转为base64编码
     */
    private static String cutFacePic2Base64(HJFaceModel hjFaceModel, String originPic) {
        String formatName = "JPEG";
        String[] imgData = originPic.split(",");
        String base64Img = imgData[imgData.length - 1];
        if (imgData.length > 1) {
            formatName = imgData[0].split("/")[1].split(";")[0];
        }
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(formatName);
        ImageReader reader = it.next();
        InputStream is = BaseToInputStream(base64Img);
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(hjFaceModel.getLeft()-50, hjFaceModel.getTop()-40<=0?hjFaceModel.getTop():hjFaceModel.getTop()-40, hjFaceModel.getRight() - hjFaceModel.getLeft()+100, hjFaceModel.getBottom() - hjFaceModel.getTop()+100);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            // bufferImage->base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bi, formatName, outputStream);
            base64Img = Base64.encodeBase64String(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();

        }
        return base64Img;
    }

}
