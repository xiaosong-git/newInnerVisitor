package com.xiaosong.interceptor.jsonbody;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import java.lang.reflect.Parameter;

 /* 该拦截器使得json格式的数据也能作为action的参数
 *
 * @author cwf
 *
 */
public class JsonInterceptor implements Interceptor {
    private static final String JSON_TYPE = "application/json";
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        String contentType = controller.getRequest().getContentType();
        Parameter[] parameters = inv.getMethod().getParameters();
        JsonBody jsonBody = null;
        // 判断contentType 是否包含 application/json
        if (contentType != null && contentType.contains(JSON_TYPE)) {
            for (int i = 0; i < parameters.length; i++) {
                jsonBody = parameters[i].getAnnotation(JsonBody.class);
                if (jsonBody != null) {
                    Class<?> T = parameters[i].getType();
                    Object result = null;
                    try {

                        result= JSON.parseObject(controller.getRawData(),T);
                    } catch (Exception e) {
                        throw new RuntimeException("Bad Request");

                    }
                    // 替换原先的参数
                    inv.setArg(i, result);
                }
            }
        }
        inv.invoke();
    }

}