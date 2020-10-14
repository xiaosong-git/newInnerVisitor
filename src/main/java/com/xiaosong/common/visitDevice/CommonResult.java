package com.xiaosong.common.visitDevice;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;


public class CommonResult<T> {

    public Object data;

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

    public CommonResult( Integer code, String message,Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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

    @Override
    public String toString() {
        return super.toString();
    }
}
