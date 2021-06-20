package com.xiaosong.common.web.blackUser;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.stat.ast.If;
import com.xiaosong.model.VBlackUser;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.IdCardUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlackUserController extends Controller {

    private BlackUserService blackUserService = BlackUserService.me;

    /**
     * 添加黑名单
    */
    public void addBlackUser(){
        Long id = getLong("id");
        String realName = getPara("realName");
        String idCard = getPara("idCard");
        Record record = Db.findFirst("select * from v_user_key");
        if (id!=null){
            VDeptUser first = VDeptUser.dao.findFirst("select idNO,realName from v_dept_user where id=?",id);
            VBlackUser blackUser = new VBlackUser();
            blackUser.setRealName(first.getRealName());
            if (StringUtils.isBlank(first.getIdNO())){
                if ( idCard ==null){
                    renderJson(RetUtil.fail("该用户没有身份证！"));
                    return ;
                }
                String idNo = DESUtil.encode(record.getStr("workKey"), idCard);
                first.setIdNO(idNo);
            }
            blackUser.setIdCard(first.getIdNO());
            blackUser.setCreateDate(getDate());
            blackUser.save();
            renderJson(RetUtil.ok());
            return ;
        }

//       String level = getPara("level");
       String userType = getPara("userType");
       VBlackUser blackUser = new VBlackUser();
       if(realName == null || idCard ==null){

           renderJson(RetUtil.fail("参数不完整"));
           return;
       }
       blackUser.setRealName(realName);
//       blackUser.setLevel(level);
       if(userType != null){
           blackUser.setUserType(userType);
       }
       String idNo = DESUtil.encode(record.getStr("workKey"), idCard);
        Long aLong = Db.queryLong(("select id from v_black_user where realName=? and idCard=?"), realName, idNo);
        if(aLong!=null){
            renderJson(RetUtil.fail("用户已存在于黑名单"));

            return;
        }
        blackUser.setIdCard(idNo);
       blackUser.setCreateDate(getDate());
       blackUser.save();
       renderJson(RetUtil.ok());
    }

    /**
     * 黑名单列表
     */
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
        boolean isAdmin= IdCardUtil.isAdmin(getHeader("userId"));
        for(Record record :list.getList()){
            if(record.get("idCard") != null){
                // 根据登入角色进行脱敏
                record.set("idCard", IdCardUtil.desensitizedDesIdNumber(DESUtil.decode(keyRecord.getStr("workKey"), record.get("idCard")),isAdmin));
            }
        }
        renderJson(RetUtil.okData(list));
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

    /**
     * 删除黑名单
     */
    public void delBlackUsers(){
        if (getLong("id") == null) {
            renderJson(RetUtil.fail("参数缺失！"));
            return;
        }
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
