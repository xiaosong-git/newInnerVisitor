package com.xiaosong.util;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 人脸建模引擎列表
 */
public class FaceDetectEngineList extends ConcurrentLinkedQueue<Long> {
    private static FaceDetectEngineList faceDetectEnginList;
    //保存全量engineID作为备份
    private ArrayList<Long> engineIdList = new ArrayList<Long>();

    private FaceDetectEngineList() {

    }

    public static FaceDetectEngineList getInstance() {
        if (FaceDetectEngineList.faceDetectEnginList == null) {
            FaceDetectEngineList.faceDetectEnginList = new FaceDetectEngineList();
        }
        return FaceDetectEngineList.faceDetectEnginList;
    }

    public void resetQueue() {
        faceDetectEnginList = new FaceDetectEngineList();
        for (long engine : engineIdList) {
            faceDetectEnginList.add(engine);
        }

    }

    public ArrayList<Long> getEngineIdList() {
        return engineIdList;
    }

    public void setEngineIdList(ArrayList<Long> engineIdList) {
        this.engineIdList = engineIdList;
    }
}
