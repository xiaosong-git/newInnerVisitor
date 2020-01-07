package com.xiaosong.handle;

import com.jfinal.core.ActionHandler;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Myhandler extends Handler {

    private static final Log log = Log.getLog(ActionHandler.class);
    private static final String[] strs={"/visitor/visitorRecord/inviteMine/",
            "/visitor/visitorRecord/inviteRecord/","/visitor/visitorRecord/myVisit/",
            "/visitor/visitorRecord/visitRecord/"};
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {


        for(int i=0;i<strs.length;i++) {
            if (target.contains(strs[i])) {//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
                String substring = target.substring(strs[i].length());
                String[] split = substring.split("/");
                if (split.length==2) {
                    if( Character.isDigit(split[0].charAt(0))&&Character.isDigit(split[1].charAt(0))){

                        request.setAttribute("pageNum", Integer.valueOf(split[0]));
                        request.setAttribute("pageSize", Integer.valueOf(split[1]));
                    }
                }
                this.next.handle(strs[i], request, response, isHandled);
                return;
            }
        }
        this.next.handle(target, request, response, isHandled);
    }
}
