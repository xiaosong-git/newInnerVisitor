package com.xiaosong.common.api.deptUser;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.common.api.foreign.ForeignService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.MyRecordPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.constant.UserPostConstant;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VUserPost;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 原公司员工，现部门员工
 * @author: cwf
 * @create: 2020-01-10 17:39
 **/
public class DeptUserService extends MyBaseService {


    public static final DeptUserService me = new DeptUserService();

    Log log=Log.getLog(DeptUserService.class);
    public Result findApplySuc(String userId) {
        String columnSql = "select du.realName userName,currentStatus,status,roleType,du.createDate," +
                "du.deptId companyId,concat(IFNULL(floor,'1'),'层',dept_name) companyName, case when IFNULL(du.addr,'')='' then '省行政服务中心' else du.addr end as addr,d.dept_name sectionName ";
        String fromSql = " from " + TableList.DEPT_USER + " du " +
                " left join " + TableList.DEPT + " d on du.deptId=d.id" +
                 " left join"  + TableList.ORG +" og on d.org_id=og.id"+
                " where du.id = '"+userId+"' and du.status = 'applySuc' and du.currentStatus='normal'";
        List<Record> records = Db.find((columnSql + fromSql));
        log.info("查询公司信息{}",columnSql+fromSql);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success","获取公司成功",apiList(records))
                : Result.unDataResult("success","暂无数据");
    }


    /**
     * 获取上级领导
     * @return
     */
    public List<VDeptUser> getSuperior(Long userId)
    {
        List<VDeptUser> list = new ArrayList<>();

        VDeptUser user = VDeptUser.dao.findById(userId);

        if(user!=null) {
            //如果是部门领导那么获去到经办岗信息
            if(1==user.getDeptLeader())
            {
                list = VDeptUser.dao.find("select * from v_dept_user where id in (select userId from v_user_post where postId =?) ", UserPostConstant.MANAGE_CAR_POST);

            }else {
                list = VDeptUser.dao.find("select * from v_dept_user where deptId = ? and deptLeader =1", user.getDeptId());
            }
        }
        return list;
    }


    /**
     *  获取人员类型，金卡 红卡 返回 1，蓝卡员工返回0，领导返回1, 经办岗返回2
     * @param user
     * @param type 获取类型，0:访客审核，1：车辆审核 需要返回经办岗 否则不需要
     * @return
     */
    public int getUserType(VDeptUser user,int type)
    {
        int userType = 0;

        if(user!=null) {
            //先查询是否经办岗 如果是 直接返回
            if(type ==1) {
                VUserPost vUserPost = VUserPost.dao.findFirst("select * from  v_user_post where userId = ? and postId=?", user.getId(), UserPostConstant.MANAGE_CAR_POST);
                if (vUserPost != null) {
                    return  2;
                }
            }
            userType = 1 == user.getDeptLeader() ? 1 : 0;

        }
        return userType;
    }


//
//    List<VUserPost> list = VUserPost.dao.find("select * from v_user_post where userId = ?",user.getId());
//         if(list!=null && list.size()>0) {
//        Long[] userPosts = new Long[list.size()];
//        for(int i = 0; i< list.size();i++)
//        {
//            userPosts[i] = list.get(i).getPostId();
//        }
//        users.put("userPost", userPosts);
//    }


}
