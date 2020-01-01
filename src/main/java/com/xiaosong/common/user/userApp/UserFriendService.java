package com.xiaosong.common.user.userApp;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.template.stat.ast.Case;
import com.xiaosong.common.compose.Result;
import com.xiaosong.common.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VCompany;
import com.xiaosong.model.VOrg;
import com.xiaosong.model.VUserFriend;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: visitor
 * @description: 用户app权限控制实现
 * @author: cwf
 * @create: 2019-09-14 10:02
 **/
public class UserFriendService {
    org.apache.log4j.Logger logger = Logger.getLogger(UserFriendService.class);
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
        logger.info("访客权限："+columSql+fromSql+suffix+union+order);
        //大楼id sql
        List<Record> records = Db.find(columSql + fromSql + suffix + union);
        return (records.isEmpty())?Result.unDataResult("success","暂无数据"): ResultData.dataResult("success","获取app权限菜单成功",records);
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
        String columnSql = "select uf.id ufId,u.id,u.realName,u.phone,u.orgId,u.province,u.city,u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl,uf.remark,c.companyName," +
                "(select fuf.applyType from " + TableList.USER_FRIEND + " fuf where fuf.userId=uf.friendId and fuf.friendId="+userId+") applyType";
        String fromSql = " from " + TableList.USER_FRIEND + " uf " +
                " left join " + TableList.APP_USER + " u on uf.friendId=u.id" +
                " left join " + TableList.COMPANY + " c on c.id=u.companyId"+
                " where uf.userId = '"+userId+"' and uf.applyType=1  ";
        logger.info("查询好友:"+userId);
        List<Record> records = Db.find(columnSql + fromSql);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success","获取通讯录记录成功",records)
                : Result.unDataResult("success","暂无数据");
    }
    public Result addFriendByPhoneAndUser(String userId,String phone,String realName,String remark) throws Exception {
        String p = Db.queryStr(Db.getSql("vAppUser.findId"),phone);//查询手机是否存在
        if (p==null){
            return Result.unDataResult(ConsantCode.FAIL, "未找到手机号!");
        }
        String id=Db.queryStr(Db.getSql("vAppUser.findIdName"),phone,realName);//查看手机与真实姓名是否匹配
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
        VUserFriend uf = VUserFriend.dao.findFirst(Db.getSql("vAppUser.findFriend"), userId, friendId);
//        Map<String,Object> newUserMap=new HashMap<>();
        if (uf!=null) {
            VUserFriend friend = VUserFriend.dao.findFirst(Db.getSql("vAppUser.findFriend"), friendId, userId);
            Integer applyType = uf.getApplyType();
//            //对方对我状态
            switch (applyType) {
                case 1://我对好友状态已经是好友，判断好友对我的状态
                    logger.info(userId + "已经是好友!" + friendId);
                    if (friend != null) {
                        Integer friendType = friend.getApplyType();
                        logger.info(friendId + "对于" + userId + "的好友状态" + friendType);
//                    //如果对方在申请我，直接添加好友
                        switch (friendType) {
                            case 0:
                                if (updateFriendType(friendId, userId, null, 1) > 0) {
                                    logger.info(userId + "重新添加好友成功!" + friendId);
                                    return Result.unDataResult("success", "添加好友成功");
                                }
                                break;
                            case 2://对方删除我，我重新申请对方
                                logger.info("更新好友状态id：" + friendType);
                                uf.setApplyType(0);
                                if (uf.update()) {
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
                        logger.info(friendId+"对于"+userId+"的好友状态"+friendType);
                        //如果对方也在申请我
                        switch (friendType){
                            case 0:
                                Integer updatemyType = updateFriendType(userId, friendId, remark, 1);
                                Integer updateFriendType = updateFriendType(friendId, userId, null, 1);
                                if (updateFriendType>0&&updatemyType>0){
                                    logger.info(userId+"重新申请好友成功!"+friendId);
                                    return Result.unDataResult("success","重新申请好友成功");
                                }
                                break;
                            case 1:
                                uf.setApplyType(1);
                                if (uf.update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            case 2:
                                logger.info("更新好友状态id："+friendType);
                                uf.setApplyType(0);
                                if (uf.update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            default:
                                return Result.unDataResult("fail", "好友数据丢失，请联系管理员!");
                        }
                    }
                    break;
                default://申请中
                  logger.info(userId+"申请中的好友!"+friendId);
                  return Result.unDataResult("fail", "申请中的好友!");
            }
        }
//        //添加至数据库 用户id 好友id 备注 applytype为0 对方同意后改为1
//        Integer save = addFriend(userId,friendId,remark,"0");
//        if (save > 0){
//            //发送websocket给好友
//            for (Map.Entry<Object, WebSocketSession> entry: Constant.SESSIONS.entrySet()){
//                logger.info("当前在线：user: "+entry.getKey()+" value: "+entry.getValue());
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
//            return Result.unDataResult("success","提交好友申请成功");

        return Result.unDataResult("fail","提交好友申请失败");
    }
    public Integer updateFriendType(Long userId,Long friendId,String remark,Integer applyType) throws Exception {
        String remarkSql="";
        if(remark!=null){
            remarkSql= ", remark ='"+remark+"'";
        }
        String sql = "update " + TableList.USER_FRIEND +" set applyType = '"+applyType+"'"+remarkSql+" where userId = "+userId +
                " and friendId ="+friendId ;
        return Db.update(sql);
    }
}
