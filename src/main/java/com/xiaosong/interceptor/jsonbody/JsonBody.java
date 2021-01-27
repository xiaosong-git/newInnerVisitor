package com.xiaosong.interceptor.jsonbody;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface JsonBody {
    /**
     * 是否对Json内容根据注解进行校验
     * @return
     */
     boolean validate() default true;
}