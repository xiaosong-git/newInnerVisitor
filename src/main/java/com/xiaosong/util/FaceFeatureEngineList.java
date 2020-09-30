package com.xiaosong.util;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 人脸特征值计算引擎列表
 */
public class FaceFeatureEngineList extends ConcurrentLinkedQueue<Long> {
    private static FaceFeatureEngineList faceDetectEnginList;

    private FaceFeatureEngineList() {

    }

    public static FaceFeatureEngineList getInstance() {
        if (FaceFeatureEngineList.faceDetectEnginList == null) {
            FaceFeatureEngineList.faceDetectEnginList = new FaceFeatureEngineList();
        }
        return FaceFeatureEngineList.faceDetectEnginList;
    }
}
