package com.xiaosong.bean;

/**
 * Created by CNL on 2020/9/4.
 */
public class MonitorResult {

    private int type;
    private Object data;
    private boolean succ;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
