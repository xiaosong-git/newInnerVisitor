package com.xiaosong.common.api.deptUser;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.constant.UserPostConstant;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VUserPost;
import com.xiaosong.util.DateUtil;

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
            //如果是部门领导那么获去到当前部门的经办岗信息
            if(user.getDeptLeader()!=null && 1==user.getDeptLeader())
            {
                list = VDeptUser.dao.find("select * from v_dept_user where id in (select userId from v_user_post where postId =?) and deptId=?", UserPostConstant.MANAGE_CAR_POST,user.getDeptId());

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
            //车辆审批类型，先查询是否经办岗 如果是 直接返回2
            if(type ==1) {
                int deptLeader = user.getDeptLeader()==null?0:user.getDeptLeader();

                VUserPost vUserPost = VUserPost.dao.findFirst("select * from  v_user_post where userId = ? and postId=?", user.getId(), UserPostConstant.MANAGE_CAR_POST);
                if (vUserPost != null) {
                    return  2;
                }
                userType = deptLeader;
            }else{
                 //如果是金卡或者红卡，默认返回领导标识
                if(user.getCardType()!=null && (1==user.getCardType() || 2==user.getCardType()))
                {
                    userType =1;
                }
                //蓝卡如果是员工，那么去他的上级领导审批
                else if(user.getCardType()!=null && 3==user.getCardType() )
                {
                    int deptLeader = user.getDeptLeader() ==null?0:user.getDeptLeader();
                    userType = 1 == deptLeader ? 1 : 0;
                }
                //米色卡和绿卡没有审批权限
                else {
                    userType = 0;
                }
            }
        }
        return userType;
    }


    public VDeptUser createVisitor(String phone,String realName) throws Exception
    {
        String sql = "select * from " + TableList.DEPT_USER + " " +
                "where currentStatus='normal' and phone='" + phone + "'";
//        //被邀者==访问者
        VDeptUser invitor = VDeptUser.dao.findFirst(sql);

        if (invitor == null) {
            // todo 如果用户不存在,创建一个新用户
            //return Result.unDataResult("fail", "用户不存在");
            invitor = new VDeptUser();
            invitor.setCurrentStatus("normal");
            invitor.setIsAuth("F");
            invitor.setPhone(phone);
            invitor.setRealName(realName);
            invitor.setCreateDate(DateUtil.getSystemTime());
            invitor.setStatus("applySuc");
            invitor.setUserType("visitor");
            invitor.save();
        }
        else
        {
            if(!invitor.getRealName().equals(realName))
            {
                throw new Exception("手机号或者姓名错误");
            }
        }
        return invitor;

    }



}
