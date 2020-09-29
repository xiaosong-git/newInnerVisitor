package com.xiaosong.common.visitDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  随机数缓存 - 单例模式
 *
 */
public class NonceData {

    public List<String> nonceList = new ArrayList<>();

    private static NonceData nonceData = null;

    private NonceData(){}


    public static NonceData getInstance(){
        if(nonceData == null){
            synchronized (NonceData.class){
                if (nonceData == null){
                    nonceData = new NonceData();
                }
            }
        }
        return nonceData;
    }
}
