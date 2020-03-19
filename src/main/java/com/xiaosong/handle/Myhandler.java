package com.xiaosong.handle;

import com.jfinal.core.ActionHandler;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 手动实现@PathVariable功能
 */
public class Myhandler extends Handler {

    private static final Log log = Log.getLog(ActionHandler.class);
    //公众版api分页接口将参数写在uri上
    private static final String[] strs={
            "/visitor/appVersion/updateAndroid/",
            "/visitor/news/list/","/visitor/notice/list/","/visitor/notice/allList/","/visitor/visitorRecord/visitMyPeople/",
            "/visitor/visitorRecord/visitMyCompany/",
            };
    private static final String param="/visitor/param/";//参数接口
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (target.contains(param)){
            request.setAttribute("paramName",target.substring(param.length()));
            this.next.handle(param, request, response, isHandled);
            return;
        }
        if (target.contains("/visitor/code/sendCode/")){
            String substring = target.substring("/visitor/code/sendCode/".length());
            String[] split = substring.split("/");
            request.setAttribute("phone",(split[0]));
            Integer integer = Integer.valueOf(split[1]);
            request.setAttribute("type", (integer));
            this.next.handle("/visitor/code/sendCode/", request, response, isHandled);
            return;
        }
        for(int i=0;i<strs.length;i++) {
            if (target.contains(strs[i])) {//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
                String substring = target.substring(strs[i].length());
                String[] split = substring.split("/");
                if (split.length==2) {
                    if( Character.isDigit(split[0].charAt(0))&&Character.isDigit(split[1].charAt(0))){

                        request.setAttribute("pageNum", Integer.valueOf(split[0]));
                        request.setAttribute("pageSize", Integer.valueOf(split[1]));
                    }else {//版本控制
                        request.setAttribute("channel",split[0]);
                        request.setAttribute("versionNum",split[1]);
                    }
                }
                this.next.handle(strs[i], request, response, isHandled);
                return;
            }
        }
        this.next.handle(target, request, response, isHandled);
    }
}
