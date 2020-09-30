package com.xiaosong.common.web.blackUser;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VBlackUser;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.RetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlackUserController extends Controller {

    private BlackUserService blackUserService = BlackUserService.me;

    public void addBlackUser(){
       String realName = getPara("realName");
       String idCard = getPara("idCard");
       String level = getPara("level");
       String userType = getPara("userType");
       VBlackUser blackUser = new VBlackUser();
       if(realName == null || idCard ==null ||level == null){
           renderJson(RetUtil.fail(500,"参数不完整"));
           return;
       }
       blackUser.setRealName(realName);
       blackUser.setLevel(level);
       if(userType != null){
           blackUser.setUserType(userType);
       }
       Record record = Db.findFirst("select * from v_user_key");
       String idNo = DESUtil.encode(record.getStr("workKey"), idCard);
       blackUser.setIdCard(idNo);
       blackUser.setCreateDate(getDate());
       blackUser.save();
       renderJson(RetUtil.ok());
    }
    public void findBlackUsers(){
        String realName = getPara("realName");
        String idCard = getPara("idCard");
        String level = getPara("level");
        int currentPage = getInt("currentPage");
        int pageSize = getInt("pageSize");
        Record keyRecord = Db.findFirst("select * from v_user_key");
        if(idCard != null){
            idCard = DESUtil.encode(keyRecord.getStr("workKey"), idCard);
        }
        Page<Record> list = blackUserService.findList(currentPage,pageSize,realName,idCard,level);
        for(Record record :list.getList()){
            if(record.get("idCard") != null){
                String idNO = DESUtil.decode(keyRecord.getStr("workKey"), record.get("idCard"));
                record.set("idCard",idNO);
            }
        }
        renderJson(RetUtil.ok(list));
    }

    public void editBlackUsers(){
        int id = getInt("id");
        String realName = getPara("realName");
        String idCard = getPara("idCard");
        String level = getPara("level");

        VBlackUser blackUser = VBlackUser.dao.findFirst("select * from v_black_user where id = ?",id);
       if(blackUser == null){
           renderJson(RetUtil.fail(500,"修改失败，没有该ID黑名单人员"));
           return;
       }
        blackUser.setLevel(level);
        blackUser.setRealName(realName);
        blackUser.setIdCard(idCard);
        blackUser.update();
        renderJson(RetUtil.ok());
    }
    public void delBlackUsers(){
        int id = getInt("id");
        VBlackUser blackUser = VBlackUser.dao.findFirst("select * from v_black_user where id = ?",id);
        if(blackUser == null){
            renderJson(RetUtil.fail(500,"修改失败，没有该ID黑名单人员"));
            return;
        }
        blackUser.delete();
        renderJson(RetUtil.ok());
    }

    //获取 当前时间的 年-月-日
    private String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(new Date()); // new Date()为获取当前系统时间
    }
}
