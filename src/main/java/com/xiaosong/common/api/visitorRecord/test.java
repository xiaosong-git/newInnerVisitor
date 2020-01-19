package com.xiaosong.common.api.visitorRecord;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.HttpKit;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2020-01-13 22:50
 **/
public class test {
    public static void main(String[] args) {
        String url = "http://unionsug.baidu.com/su?wd=";
        String ret = HttpKit.post(url, "jfinal");
        System.out.println(ret);
    }

}
