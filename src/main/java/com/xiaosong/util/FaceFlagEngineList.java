package com.xiaosong.util;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FaceFlagEngineList extends ConcurrentLinkedQueue<Long> {

    private static class FaceFlagEngineHandle{
        private static FaceFlagEngineList faceFlagEngineList = new FaceFlagEngineList();
    }

    private FaceFlagEngineList() {

    }

    public static FaceFlagEngineList getInstance() {
        return FaceFlagEngineHandle.faceFlagEngineList;
    }

}
