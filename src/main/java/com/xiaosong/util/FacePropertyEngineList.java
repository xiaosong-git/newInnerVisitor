package com.xiaosong.util;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FacePropertyEngineList extends ConcurrentLinkedQueue<Long> {

    private static FacePropertyEngineList facePropertyEngineList;

    private FacePropertyEngineList() {

    }

    public static FacePropertyEngineList getInstance() {
        if (FacePropertyEngineList.facePropertyEngineList == null) {
            FacePropertyEngineList.facePropertyEngineList = new FacePropertyEngineList();
        }
        return FacePropertyEngineList.facePropertyEngineList;
    }

}
