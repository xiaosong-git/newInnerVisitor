package com.xiaosong.common.visitDevice;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;


public class CommonResult<T> {

    public List<T> data;

    public Integer code;

    public String message;

    public Integer total;

    public CommonResult(){}

    public CommonResult( Integer code, String message,List<T> data) {
        this.code = code;
        this.total = data.size();
        this.message = message;
        this.data = data;
    }

    public CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
