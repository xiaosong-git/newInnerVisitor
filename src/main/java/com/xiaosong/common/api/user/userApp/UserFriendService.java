package com.xiaosong.common.api.user.userApp;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VCompany;
import com.xiaosong.model.VOrg;
import com.xiaosong.model.VUserFriend;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.util.phoneUtil;

import java.util.List;
import java.util.Map;

/**
 * @program: visitor
 * @description: 用户app权限控制实现
 * @author: cwf
 * @create: 2019-09-14 10:02
 **/
public class UserFriendService  extends MyBaseService {
    private Log log = Log.getLog(UserFriendService.class);
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
        VCompany company = new VCompany();
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
        log.info("访客权限："+columSql+fromSql+suffix+union+order);
        //大楼id sql
        List<Record> records = Db.find(columSql + fromSql + suffix + union);
        //返回转换为公众版apiList的对象
        return records == null ||records.isEmpty()?Result.unDataResult("success","暂无数据"):
                ResultData.dataResult("success","获取app权限菜单成功",apiList(records));
    }
    //点击退出app 修改在线状态
    public Result appQuit(Long userId)  {
        VAppUser appUser=new VAppUser();
        appUser.setId(userId).setIsOnlineApp("F");
        return  appUser.update()?Result.success():Result.fail();
    }
    /** 
     * 查询用户好友
     * 2019/12/31 11:25
     */
    public Result findUserFriend(Long userId)  {
        //添加好友对登入人状态 2为删除
        log.info("查询好友:"+userId);
        List<Record> records = Db.find(Db.getSql("appUser.findUserFriend"),userId);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success", "获取通讯录记录成功", apiList(records))
                : Result.unDataResult("success", "暂无数据");
    }
    public Result addFriendByPhoneAndUser(String userId,String phone,String realName,String remark) throws Exception {
        String p = Db.queryStr(Db.getSql("appUser.findId"),phone);//查询手机是否存在
        if (p==null){
            return Result.unDataResult(ConsantCode.FAIL, "未找到手机号!");
        }
        String id=Db.queryStr(Db.getSql("appUser.findIdName"),phone,realName);//查看手机与真实姓名是否匹配
        if(id==null){
            return Result.unDataResult("fail","用户姓名与手机不匹配!");
        }
        if (userId.equals(id)){
            return Result.unDataResult("fail","无法添加自己为好友!");
        }
        VUserFriend userFriend=new VUserFriend();
        userFriend.setUserId(Long.valueOf(userId)).setFriendId(Long.valueOf(id)).setRemark(remark);//添加好友参数
        return  applyUserFriend(userFriend);
    }

    private Result applyUserFriend(VUserFriend userFriend) throws Exception {
//        //如果存在好友申请
        Long userId = userFriend.getUserId();
        Long friendId = userFriend.getFriendId();
        String remark=userFriend.getRemark();
        VUserFriend uf = VUserFriend.dao.findFirst(Db.getSql("appUser.findFriend"), userId, friendId);
//        Map<String,Object> newUserMap=new HashMap<>();
        if (uf!=null) {
            VUserFriend friend = VUserFriend.dao.findFirst(Db.getSql("appUser.findFriend"), friendId, userId);
            Integer applyType = uf.getApplyType();
//            //对方对我状态
            switch (applyType) {
                case 1://我对好友状态已经是好友，判断好友对我的状态
                    log.info(userId + "已经是好友!" + friendId);
                    if (friend != null) {
                        Integer friendType = friend.getApplyType();
                        log.info(friendId + "对于" + userId + "的好友状态" + friendType);
//                    //如果对方在申请我，直接添加好友
                        switch (friendType) {
                            case 0:
                                if ( friend.setApplyType(1).update()) {
                                    log.info(userId + "重新添加好友成功!" + friendId);
                                    return Result.unDataResult("success", "添加好友成功");
                                }
                                break;
                            case 2://对方删除我，我重新申请对方
                                log.info("更新好友状态id：" + friendType);
                                if ( uf.setApplyType(0).update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            default:
                                break;
                        }

                    }
                    break;
                case 2://我对好友状态为已删除，判断好友对我的状态
                    if (friend!=null){
                        Integer friendType = friend.getApplyType();
                        log.info(friendId+"对于"+userId+"的好友状态"+friendType);
                        //如果对方也在申请我
                        switch (friendType){
                            case 0:
                                if (remark!=null){
                                    uf.setRemark(remark);
                                }
                                //事务
                                boolean tx = Db.tx(() -> {
                                    uf.setApplyType(1).update();
                                    friend.setApplyType(1).update();
                                    return true;
                                });
                                if (tx){
                                    log.info(userId+"重新申请好友成功!"+friendId);
                                    return Result.unDataResult("success","重新申请好友成功");
                                }
                                break;
                            case 1:
                                if (  uf.setApplyType(1).update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            case 2:
                                log.info("更新好友状态id："+friendType);
                                if ( uf.setApplyType(0).update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            default:
                                return Result.unDataResult("fail", "好友数据丢失，请联系管理员!");
                        }
                    }
                    break;
                default://申请中
                  log.info(userId+"申请中的好友!"+friendId);
                  return Result.unDataResult("fail", "申请中的好友!");
            }
        }
        //applyType=0 申请中
        if(userFriend.setApplyType(0).save()){
            //发送websocket给好友
//            for (Map.Entry<Object, WebSocketSession> entry: Constant.SESSIONS.entrySet()){
//                log.info("当前在线：user: "+entry.getKey()+" value: "+entry.getValue());
//            }
//
//            if (Constant.SESSIONS.containsKey((long)friendId)){
//                JSONObject obj = new JSONObject();
//                obj.put("fromUserId", userId);
//                obj.put("toUserId", friendId);
//                obj.put("message", "申请好友");
//                obj.put("type", 4);
//                String sql="select count(*) c from "+TableList.USER_FRIEND+" where friendId="+friendId+" and applyType=0";
//                Map<String, Object> count = findFirstBySql(sql);
//                if (count!=null){
//                    obj.put("count",count.get("c"));
//                }
//                webSocketService.sendMessageToUser(Constant.SESSIONS.get((long)friendId), (long)userId, (long)friendId, "申请好友",(long) 4, new TextMessage(obj.toJSONString()));
//            }else {
//                webSocketService.saveMessage((long)userId,(long)friendId,"申请好友",(long)4);
//            }
            return Result.unDataResult("success","提交好友申请成功");
        }
        return Result.unDataResult("fail","提交好友申请失败");
    }

    /**
     * 添加好友
     * @param userFriend
     * @return result 成功 失败
     */
    public Result agreeFriend(VUserFriend userFriend) throws Exception {
        Long userId = userFriend.getUserId();
        Long friendId = userFriend.getFriendId();
        String remark = userFriend.getRemark();
        //我存在好友
        VUserFriend uf = VUserFriend.dao.findFirst(Db.getSql("appUser.findFriend"), userId, friendId);
        //只有通过同意列表显示的按钮才能添加好友，所以好友必定存在我
        VUserFriend fu = VUserFriend.dao.findFirst(Db.getSql("appUser.findFriend"), friendId, userId);
        //对方没有添加我
        if (fu==null||fu.getApplyType()==2){
            return Result.unDataResult("fail","数据错误！请联系管理员");
        }
        fu.setApplyType(1);
        //我存在好友
        if ( uf!=null) {
            //已通过验证
            Integer applyType = uf.getApplyType();
            if (applyType==1) {
                return Result.unDataResult("fail", "你们已经是好友啦!");
            } else { //未通过验证 已删除的好友
                if(remark!=null){
                    uf.setRemark(remark);
                }
                boolean tx = Db.tx(() -> {
                    uf.setApplyType(1).update();
                    fu.update();
                    return true;
                });
                return tx?Result.unDataResult("success","通过好友申请成功"):Result.unDataResult("fail","操作失败");
            }
            //如果不存在好友记录
        }else{
            boolean tx = Db.tx(() -> {
                userFriend.setApplyType(1).save();
                fu.update();
                return true;
            });
            return tx?Result.unDataResult("success","通过好友申请成功"):Result.unDataResult("fail","操作失败");
        }
    }

    public Result newFriend(Long userId,String phoneStr){
        String[] phones = phoneStr.split(",");
        log.info(userId+"传入手机号为："+phoneStr);
        StringBuffer newPhones=new StringBuffer();
        for (String phone:phones){
            if( phoneUtil.isPhoneLegal(phone)){
                newPhones.append(phone).append(",");
            }
        }
        if (newPhones.length()==0){
            return Result.unDataResult("success","暂无数据");
        }
        newPhones.deleteCharAt(newPhones.length() - 1);
        log.info(userId+"最终查询的手机号为："+newPhones);

        String columsql="select * from ";
        String sql = "(select u.id,u.realName,u.phone,u.orgId,u.province,u.city,u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl,'同意' applyType, null\n" +
                " remark  from  "+ TableList.USER_FRIEND +" uf  left join "+ TableList.APP_USER +" u on uf.userId=u.id where uf.friendId = '"+userId+"' and uf.applyType=0 \n" +
                " union " +
                "select u.id,u.realName,u.phone,u.orgId,u.province,u.city,u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl," +
                " case (select  applyType from "+ TableList.USER_FRIEND +" uf where uf.friendId=u.id and uf.userId="+userId+" )  when 0 then '申请中' when 1 then '已添加' else '添加' end \n" +
                "\t applyType," +
                "(select  remark from "+ TableList.USER_FRIEND +" uf where uf.friendId=u.id and uf.userId="+userId+" ) remark"+
                " from "+ TableList.APP_USER +"  u where phone in ("+newPhones+") and isAuth='T' " +
                "ORDER BY FIELD(applyType, '同意', '添加', '申请中','已添加'),convert(realName using gbk))x where id >0 and id <>"+userId;
        List<Record> records = Db.find(columsql + sql);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success", "查询用户成功", apiList(records))
                : Result.unDataResult("success", "暂无数据");
    }
    /**
        删除好友
     */
    public Result deleteUserFriend(Long userId,Long friendId) throws Exception {
        int update = Db.update(Db.getSql("appUser.deleteUserFriend"), userId, friendId);
        if(update > 0){
            log.info(userId+"删除好友"+friendId+"成功");
            return  Result.unDataResult("success","删除成功");
        }else{
            return Result.unDataResult("fail","删除失败");
        }
    }


    public Result findFriendApplyMe(Long userId) {

            String columnSql = "select * from (select uf.userId,uf.friendId,uf.applyType,u.realName,u.phone,u.orgId,u.province,u.city" +
                    ",u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl";
            String fromSql   = " from " + TableList.USER_FRIEND + " uf " +
                    " left join " + TableList.APP_USER + " u on uf.friendId=u.id" +
                    " where uf.userId = '"+userId+"'";
            String union=" union all \n" +
                    "select uf.userId,uf.friendId,uf.applyType,u.realName,u.phone,u.orgId,u.province,u.city," +
                    "u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl\n" +
                    "from " + TableList.USER_FRIEND + "   uf \n" +
                    "left join " + TableList.APP_USER + " u on uf.userid=u.id \n" +
                    "where uf.friendId = "+userId+")x group by realName,phone,companyId ";
        List<VAppUser> vAppUsers = VAppUser.dao.find(columnSql + fromSql + union);
        return vAppUsers != null && !vAppUsers.isEmpty()
                    ? ResultData.dataResult("success","获取列表成功",vAppUsers)
                    : Result.unDataResult("success","暂无数据");
        }

}

