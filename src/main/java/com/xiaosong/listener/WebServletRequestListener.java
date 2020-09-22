package com.xiaosong.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
 
public class WebServletRequestListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
    }
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request=(HttpServletRequest) sre.getServletRequest();
        HttpSession session=request.getSession();
        //把HttpServletRequest中的IP地址放入HttpSession中，关键字可任取，此处为ClientIP
        session.setAttribute("client-ip", sre.getServletRequest().getRemoteAddr());
    }
}