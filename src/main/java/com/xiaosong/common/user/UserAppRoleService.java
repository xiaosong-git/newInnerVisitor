package com.xiaosong.common.user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.compose.Result;
import com.xiaosong.common.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VCompany;
import com.xiaosong.model.VOrg;
import com.xiaosong.util.BaseUtil;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @program: visitor
 * @description: 用户app权限控制实现
 * @author: cwf
 * @create: 2019-09-14 10:02
 **/
public class UserAppRoleService  {
    org.apache.log4j.Logger logger = Logger.getLogger(UserAppRoleService.class);
    /**
     * 1、获取登入人公司所在大楼的app角色权限 2、获取个人所在大楼的app角色权限 3、获取个人的app角色权限
     * 4、个人角色权限需要时1、2 两个步骤合集的子集
     * 企业版公司还需要吗？？
     * @param userId
     * @return
     * @throws Exception
     */
    public Result getRoleMenu(Long userId) {
        if (userId==null){
          return Result.unDataResult("fail","缺少参数");
        }
        VAppUser user = VAppUser.dao.findById(userId);
        if(user==null){
            return Result.unDataResult("fail","没有用户参数");
        }
        //查找自己的大楼id
        String orgId = BaseUtil.objToStr(user.getOrgId(),null);
        VCompany company = null;
        //查找公司的大楼id
         if(user.getCompanyId()!=null){
             company= VCompany.dao.findById(user.getCompanyId());
        }
        String columSql="select DISTINCT m.id,m.menu_code,m.menu_name,m.menu_url,m.sid,sstatus ";
//        //3、获取个人的app角色权限
//        //获取基础用户权限
        String fromSql="  from "+ TableList.APP_MENU+" m " +
                "left join \n" +
                TableList.APP_ROLE_MENU+" urm on m.id=urm.menu_id ";
        String suffix=" left join " +TableList.APP_ROLE +" ur on ur.id=urm.role_id and urm.isOpen='T'"+
        " where ur.role_name='访客'";
        String union="";
        //查找orgRole
        if (company!=null&&company.getOrgId()!=null) {
            VOrg org = VOrg.dao.findById(company.getOrgId());
            if (org.getApprole()!=null) {
                    union = " union " + columSql + fromSql + " where urm.role_id=" + org.getApprole()+" and urm.isOpen='T' ";
            }
        }
        String order=" order by id";
        logger.info("访客权限："+columSql+fromSql+suffix+union+order);
//        //大楼id sql
        List<Record> records = Db.find(columSql + fromSql + suffix + union);
        return (records.isEmpty())?Result.unDataResult("success","暂无数据"): ResultData.dataResult("success","获取app权限菜单成功",records);
    }
}
