package com.xiaosong.interceptor.apiInterceptor;

import java.lang.annotation.*;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2020-01-13 15:36
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthCheckAnnotation {
    boolean checkLogin()  default false;    //是否验证登录
    boolean checkVerify() default false;    //是否验证实名
    boolean checkRequestLegal() default false; //检查请求合法性
}
