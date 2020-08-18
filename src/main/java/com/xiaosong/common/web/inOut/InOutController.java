package com.xiaosong.common.web.inOut;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class InOutController extends Controller {
    public InOutService srv = InOutService.me;

    public void findList() {
        String userName = getPara("userName");
        String userType = getPara("userType");
        String inOrOut = getPara("inOrOut");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        int currentPage = getInt("currentPage");
        int pageSize = getInt("pageSize");
        Page<Record> pagelist = srv.findList(userName,userType,inOrOut,startDate,endDate,currentPage,pageSize);
        renderJson(pagelist);
    }
}
