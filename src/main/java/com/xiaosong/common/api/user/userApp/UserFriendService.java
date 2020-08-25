package com.xiaosong.common.api.user.userApp;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDept;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VOrg;
import com.xiaosong.model.VUserFriend;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.util.phoneUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public Result getRoleMenu(Long userId) {
        if (userId == null) {
            return Result.unDataResult("fail", "缺少参数");
        }
        VDeptUser user = VDeptUser.dao.findById(userId);
        if (user == null) {
            return Result.unDataResult("fail", "没有用户参数");
        }

        //查找自己的大楼id
        String columSql = "select DISTINCT m.id,m.menu_code,m.menu_name,m.menu_url,m.sid,sstatus ";
//        //3、获取个人的app角色权限
//        //获取基础用户权限
        String fromSql = "  from " + TableList.APP_MENU + " m " +
                "left join \n" +
                TableList.APP_ROLE_MENU + " urm on m.id=urm.menu_id ";
        String suffix = " left join " + TableList.APP_ROLE + " ur on ur.id=urm.role_id and urm.isOpen='T'" +
                " where ur.role_name='访客'";
        StringBuilder union = new StringBuilder(" union " + columSql + fromSql).append("where urm.role_id=9");
        //查找orgRole
//        if (dept!=null&&dept.getOrgId()!=null) {
//            VOrg org = VOrg.dao.findById(dept.getOrgId());
//            if (org.getApprole()!=null) {
//                    union = " union " + columSql + fromSql + " where urm.role_id=" + org.getApprole()+" and urm.isOpen='T' ";
//            }
//        }
        String order = " order by id";
        log.info("访客权限：" + columSql + fromSql + suffix + union + order);
        //大楼id sql
        List<Record> records = Db.find(columSql + fromSql + suffix + union);
        //返回转换为公众版apiList的对象
        return records == null || records.isEmpty() ? Result.unDataResult("success", "暂无数据") :
                ResultData.dataResult("success", "获取app权限菜单成功", apiList(records));
    }

    //点击退出app 修改在线状态
    public Result appQuit(Long userId) {
        VDeptUser appUser = new VDeptUser();
        appUser.setId(userId).setIsOnlineApp("F");
        return appUser.update() ? Result.success() : Result.fail();
    }

    /**
     * 查询用户好友
     * 2019/12/31 11:25
     */
    public Result findUserFriend(Long userId) {
        //添加好友对登入人状态 2为删除
        log.info("查询好友:" + userId);
        List<Record> records = Db.find(Db.getSql("deptUser.findUserFriend"), userId, userId);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success", "获取通讯录记录成功", apiList(records))
                : Result.unDataResult("success", "暂无数据");
    }

    public Result addFriendByPhoneAndUser(String userId, String phone, String realName, String remark) throws Exception {
        String p = Db.queryStr(Db.getSql("deptUser.findId"), phone);//查询手机是否存在
        if (p == null) {
            return Result.unDataResult(ConsantCode.FAIL, "未找到手机号!");
        }
        String id = Db.queryStr(Db.getSql("deptUser.findIdName"), phone, realName);//查看手机与真实姓名是否匹配
        if (id == null) {
            return Result.unDataResult("fail", "用户姓名与手机不匹配!");
        }
        if (userId.equals(id)) {
            return Result.unDataResult("fail", "无法添加自己为好友!");
        }
        VUserFriend userFriend = new VUserFriend();
        userFriend.setUserId(Long.valueOf(userId)).setFriendId(Long.valueOf(id)).setRemark(remark);//添加好友参数
        return applyUserFriend(userFriend);
    }

    private Result applyUserFriend(VUserFriend userFriend) throws Exception {
//        //如果存在好友申请
        Long userId = userFriend.getUserId();
        Long friendId = userFriend.getFriendId();
        String remark = userFriend.getRemark();
        VUserFriend uf = VUserFriend.dao.findFirst(Db.getSql("deptUser.findFriend"), userId, friendId);
//        Map<String,Object> newUserMap=new HashMap<>();
        if (uf != null) {
            VUserFriend friend = VUserFriend.dao.findFirst(Db.getSql("deptUser.findFriend"), friendId, userId);
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
                                if (friend.setApplyType(1).update()) {
                                    log.info(userId + "重新添加好友成功!" + friendId);
                                    return Result.unDataResult("success", "添加好友成功");
                                }
                                break;
                            case 2://对方删除我，我重新申请对方
                                log.info("更新好友状态id：" + friendType);
                                if (uf.setApplyType(0).update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            default:
                                break;
                        }

                    }
                    break;
                case 2://我对好友状态为已删除，判断好友对我的状态
                    if (friend != null) {
                        Integer friendType = friend.getApplyType();
                        log.info(friendId + "对于" + userId + "的好友状态" + friendType);
                        //如果对方也在申请我
                        switch (friendType) {
                            case 0:
                                if (remark != null) {
                                    uf.setRemark(remark);
                                }
                                //事务
                                boolean tx = Db.tx(() -> {
                                    uf.setApplyType(1).update();
                                    friend.setApplyType(1).update();
                                    return true;
                                });
                                if (tx) {
                                    log.info(userId + "重新申请好友成功!" + friendId);
                                    return Result.unDataResult("success", "重新申请好友成功");
                                }
                                break;
                            case 1:
                                if (uf.setApplyType(1).update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            case 2:
                                log.info("更新好友状态id：" + friendType);
                                if (uf.setApplyType(0).update()) {
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                break;
                            default:
                                return Result.unDataResult("fail", "好友数据丢失，请联系管理员!");
                        }
                    }
                    break;
                default://申请中
                    log.info(userId + "申请中的好友!" + friendId);
                    return Result.unDataResult("fail", "申请中的好友!");
            }
        }
        //applyType=0 申请中
        if (userFriend.setApplyType(0).save()) {
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
            return Result.unDataResult("success", "提交好友申请成功");
        }
        return Result.unDataResult("fail", "提交好友申请失败");
    }

    /**
     * 添加好友
     *
     * @param userFriend
     * @return result 成功 失败
     */
    public Result agreeFriend(VUserFriend userFriend) throws Exception {
        Long userId = userFriend.getUserId();
        Long friendId = userFriend.getFriendId();
        String remark = userFriend.getRemark();
        //我存在好友
        VUserFriend uf = VUserFriend.dao.findFirst(Db.getSql("deptUser.findFriend"), userId, friendId);
        //只有通过同意列表显示的按钮才能添加好友，所以好友必定存在我
        VUserFriend fu = VUserFriend.dao.findFirst(Db.getSql("deptUser.findFriend"), friendId, userId);
        //对方没有添加我
        if (fu == null || fu.getApplyType() == 2) {
            return Result.unDataResult("fail", "数据错误！请联系管理员");
        }
        fu.setApplyType(1);
        //我存在好友
        if (uf != null) {
            //已通过验证
            Integer applyType = uf.getApplyType();
            if (applyType == 1) {
                return Result.unDataResult("fail", "你们已经是好友啦!");
            } else { //未通过验证 已删除的好友
                if (remark != null) {
                    uf.setRemark(remark);
                }
                boolean tx = Db.tx(() -> {
                    uf.setApplyType(1).update();
                    fu.update();
                    return true;
                });
                return tx ? Result.unDataResult("success", "通过好友申请成功") : Result.unDataResult("fail", "操作失败");
            }
            //如果不存在好友记录
        } else {
            boolean tx = Db.tx(() -> {
                userFriend.setApplyType(1).save();
                fu.update();
                return true;
            });
            return tx ? Result.unDataResult("success", "通过好友申请成功") : Result.unDataResult("fail", "操作失败");
        }
    }

    public Result newFriend(Long userId, String phoneStr) {
        String[] phones = phoneStr.split(",");
        log.info(userId + "传入手机号为：" + phoneStr);
        StringBuffer newPhones = new StringBuffer();
        for (String phone : phones) {
            if (phoneUtil.isPhoneLegal(phone)) {
                newPhones.append(phone).append(",");
            }
        }
        if (newPhones.length() == 0) {
            return Result.unDataResult("success", "暂无数据");
        }
        newPhones.deleteCharAt(newPhones.length() - 1);
        log.info(userId + "最终查询的手机号为：" + newPhones);

        String columsql = "select * from ";
        String sql = "(select u.id,u.realName,u.phone,u.addr,u.idHandleImgUrl,u.deptId companyId,u.headImgUrl,'同意' applyType, null\n" +
                " remark  from  " + TableList.USER_FRIEND + " uf  left join " + TableList.DEPT_USER + " u on uf.userId=u.id where uf.friendId = '" + userId + "' and uf.applyType=0 \n" +
                " union " +
                "select u.id,u.realName,u.phone,u.addr,u.idHandleImgUrl,u.deptId companyId,u.headImgUrl," +
                " case (select  applyType from " + TableList.USER_FRIEND + " uf where uf.friendId=u.id and uf.userId=" + userId + " )  when 0 then '申请中' when 1 then '已添加' else '添加' end \n" +
                "\t applyType," +
                "(select  remark from " + TableList.USER_FRIEND + " uf where uf.friendId=u.id and uf.userId=" + userId + " ) remark" +
                " from " + TableList.DEPT_USER + "  u where phone in (" + newPhones + ") and isAuth='T' " +
                "ORDER BY FIELD(applyType, '同意', '添加', '申请中','已添加'),convert(realName using gbk))x where id >0 and id <>" + userId;
        List<Record> records = Db.find(columsql + sql);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success", "查询用户成功", apiList(records))
                : Result.unDataResult("success", "暂无数据");
    }

    /**
     * 删除好友
     */
    public Result deleteUserFriend(Long userId, Long friendId) throws Exception {
        int update = Db.update(Db.getSql("deptUser.deleteUserFriend"), userId, friendId);
        if (update > 0) {
            log.info(userId + "删除好友" + friendId + "成功");
            return Result.unDataResult("success", "删除成功");
        } else {
            return Result.unDataResult("fail", "删除失败");
        }
    }


    public Result findFriendApplyMe(Long userId) {

//            String columnSql = "select * from (select uf.userId,uf.friendId,uf.applyType,u.realName,u.phone,u.orgId,u.province,u.city" +
//                    ",u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl";
//            String fromSql   = " from " + TableList.USER_FRIEND + " uf " +
//                    " left join " + TableList.DEPT_USER + " u on uf.friendId=u.id" +
//                    " where uf.userId = '"+userId+"'";
//            String union=" union all \n" +
//                    "select uf.userId,uf.friendId,uf.applyType,u.realName,u.phone,u.orgId,u.province,u.city," +
//                    "u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl\n" +
//                    "from " + TableList.USER_FRIEND + "   uf \n" +
//                    "left join " + TableList.DEPT_USER + " u on uf.userid=u.id \n" +
//                    "where uf.friendId = "+userId+")x group by realName,phone,companyId ";

        List<Record> records = Db.find(Db.getSql("deptUser.findFriendApplyMe"), userId, userId);
        return records != null && !records.isEmpty()
                ? ResultData.dataResult("success", "获取列表成功", apiList(records))
                : Result.unDataResult("success", "暂无数据");
    }

    public Result findIsUserByPhone(Map<String, Object> paramMap) throws Exception {
        String phoneStr = BaseUtil.objToStr(paramMap.get("phoneStr"), ",");
        String userId = BaseUtil.objToStr(paramMap.get("userId"), "0");
        String[] phones = phoneStr.split(",");
        log.info("传入手机号为：{}", phoneStr);
        StringBuffer newPhones = new StringBuffer();
        for (String phone : phones) {
            if (phoneUtil.isPhoneLegal(phone)) {
                newPhones.append(phone).append(",");
            }
        }
        if (newPhones.length() == 0) {
            return Result.unDataResult("success", "暂无数据");
        }
        newPhones.deleteCharAt(newPhones.length() - 1);
        log.info("最终查询的手机号为：{}", newPhones);
        // update by cwf  2019/11/8 15:44 Reason:查询是否存在用户，并显示是否为好友
        String columsql = "select *,(select  applyType from " + TableList.USER_FRIEND + " uf where uf.friendId=u.id and uf.userId=" + userId + " ) applyType," +
                "(select  remark from " + TableList.USER_FRIEND + " uf where uf.friendId=u.id and uf.userId=" + userId + " ) remark";
        String sql = " from " + TableList.DEPT_USER + "  u where phone in (" + newPhones + ") and isAuth='T'";
        log.info(columsql + sql);
        List<Record> list = Db.find(columsql + " " + sql);
        return list != null && !list.isEmpty()
                ? ResultData.dataResult("success", "查询用户成功",apiList(list))
                : Result.unDataResult("success", "暂无数据");
    }


    public Result findPhone(String phone) throws Exception {
        //通过手机号查询用户信息
        String coloumSql = " select u.id,u.realName,u.phone,u.deptId,u.addr,u.idHandleImgUrl,headImgUrl";
        String fromSql = " from " + TableList.DEPT_USER + " u" +
                " where u.phone = ? ";
        List<Record> user = Db.find(coloumSql + fromSql, phone);
        if (user.size() < 1) {
            return Result.unDataResult("fail", "没有找到此手机用户!");
        }
        return ResultData.dataResult("success", "查找此用户成功!", apiList(user));
    }


    /**
     * 添加好友
     *
     * @return
     */



   public Result addUserFriend( Integer userId,Integer friendId, String remark, String authentication,String remarkMsg) {
        //添加通讯录功能需要改变
        try {
                if (userId==null||friendId==null){
                    return  Result.unDataResult(ConsantCode.FAIL,"缺少参数!");
                }
                if (userId.equals(friendId)){
                    return Result.unDataResult(ConsantCode.FAIL,"无法添加自己为好友!");
                }
                //如果存在好友申请
                Map<String,Object> ifUserFriend = findFriend(userId,friendId);
                Map<String,Object> newUserMap=new HashMap<>();
                if (ifUserFriend!=null) {
                    String applyType = BaseUtil.objToStr(ifUserFriend.get("applyType"),null);
                    //对方对我状态
                    Map<String,Object> friendUser = findFriend(friendId,userId);
                    if (applyType ==null||"0".equals(applyType)) {
                        log.info("{}申请中的好友!{}",userId,friendId);
                        return Result.unDataResult("fail", "申请中的好友!");
                    }else if ("1".equals(applyType)){
                        log.info("{}你们已经是好友啦!{}",userId,friendId);
                        if (friendUser!=null) {
                            String friendType = BaseUtil.objToStr(friendUser.get("applyType"), null);
                            log.info("{}对于{}的好友状态{}", friendId, userId, friendType);
                            //ifUserFriend的id
                            long id = BaseUtil.objToLong(ifUserFriend.get("ufId"), null);
                            //如果对方在申请我，直接添加好友
                            if ("0".equals(friendType)) {
                                Integer updateFriendType = updateFriendType(friendId, userId, null, 1);
                                if (updateFriendType > 0 ) {
                                    log.info("{}重新申请好友成功!{}", userId, friendId);
                                    return Result.unDataResult("success", "添加好友成功");
                                }
                            }else if ("2".equals(friendType)){

                                log.info("更新好友状态id：{}",friendType);
                                newUserMap.put("id",id);
                                newUserMap.put("applyType","0");
                                int update = Db.update(TableList.USER_FRIEND, newUserMap);
                                if (update>0){
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                            }
                        }
                        return Result.unDataResult("fail", "你们已经是好友啦!");
                        //重新添加好友
                    }else if ("2".equals(applyType)){

                        //查看对方是否也删除了我
                        //如果无数据，返回错误
                        if (friendUser!=null){
                            String friendType = BaseUtil.objToStr(friendUser.get("applyType"),null);
                            log.info("{}对于{}的好友状态{}",friendId,userId,friendType);
                            //ifUserFriend的id
                            long id = BaseUtil.objToLong(ifUserFriend.get("ufId"), null);
                            //如果对方也在申请我
                            if("0".equals(friendType)){
                                Integer updatemyType = updateFriendType(userId, friendId, remark, 1);
                                Integer updateFriendType = updateFriendType(friendId, userId, null, 1);
                                if (updateFriendType>0&&updatemyType>0){
                                    log.info("{}重新申请好友成功!{}",userId,friendId);
                                    return Result.unDataResult("success","重新申请好友成功");
                                }
                                //对方没删除我，直接修改回状态为1
                            } else if ("1".equals(friendType)){

                                newUserMap.put("id",id);
                                newUserMap.put("applyType","1");
                                int update = Db.update(TableList.USER_FRIEND, newUserMap);
                                if (update>0){
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                                //对方也删除了我，重新发起申请
                            }else if("2".equals(friendType)){
                                log.info("更新好友状态id：{}",friendType);
                                newUserMap.put("id",id);
                                newUserMap.put("applyType","0");
                                int update = Db.update(TableList.USER_FRIEND, newUserMap);
                                if (update>0){
                                    return Result.unDataResult("success", "重新申请好友成功!");
                                }
                            }
                            return Result.unDataResult("fail", "好友数据丢失，请联系管理员!");
                        }
                    }
                }
                //添加至数据库 用户id 好友id 备注 applytype为0 对方同意后改为1
                boolean save = addFriend(userId,friendId,remark,"0",authentication,remarkMsg);
                if (save){
                 /*   //发送websocket给好友
                   for (Map.Entry<Object, WebSocketSession> entry: Constant.SESSIONS.entrySet()){
                        log.info("当前在线：user: "+entry.getKey()+" value: "+entry.getValue());
                    }
                    if (Constant.SESSIONS.containsKey((long)friendId)){
                        JSONObject obj = new JSONObject();
                        obj.put("fromUserId", userId);
                        obj.put("toUserId", friendId);
                        obj.put("message", "申请好友");
                        obj.put("type", 4);
                        String sql="select count(*) c from "+TableList.USER_FRIEND+" where friendId="+friendId+" and applyType=0";
                        Map<String, Object> count =apiMap(Db.findFirst(sql));
                        if (count!=null){
                            obj.put("count",count.get("c"));
                        }
                        webSocketService.sendMessageToUser(Constant.SESSIONS.get((long)friendId), (long)userId, (long)friendId, "申请好友",(long) 4, new TextMessage(obj.toJSONString()));
                    }else {
                        webSocketService.saveMessage((long)userId,(long)friendId,"申请好友",(long)4);
                    }
                    return Result.unDataResult("success","提交好友申请成功");*/
                }
                return Result.unDataResult("fail","提交好友申请失败");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.unDataResult("fail", "系统异常");
        }
    }


    public Map<String, Object> findFriend(Integer userId, Integer friendId) throws Exception {
        String sql = " select u.id,u.realName,u.phone,u.orgId,u.province,u.city,u.area,u.addr,u.idHandleImgUrl,u.companyId,uf.applyType,u.niceName,u.headImgUrl,uf.id ufId"+
                " from " + TableList.USER_FRIEND + " uf " +
                " left join " + TableList.DEPT_USER + " u on uf.friendId=u.id" +
                " where uf.userId = '"+userId+"' and uf.friendId = '"+friendId+"'";
        System.out.println(sql);
        Record record =  Db.findFirst(sql);
        return apiMap(record);
    }


    public Integer updateFriendType(Integer userId,Integer friendId,String remark,Integer applyType) throws Exception {
        String remarkSql="";
        if(remark!=null){
            remarkSql= ", remark ='"+remark+"'";
        }
        String sql = "update " + TableList.USER_FRIEND +" set applyType = '"+applyType+"'"+remarkSql+" where userId = "+userId +
                " and friendId ="+friendId ;
        return  Db.update(sql);
    }


    public boolean addFriend(Integer userId, Integer friendId, String remark, String applyType, String authentication, String remarkMsg) throws Exception {
        Record userFriend = new Record();
        userFriend.set("userId",userId);
        userFriend.set("friendId",friendId);
        userFriend.set("remark",remark);
        userFriend.set("applyType",applyType);
        userFriend.set("authentication",authentication);
        userFriend.set("remarkMsg",remarkMsg);
        return Db.save(TableList.USER_FRIEND,userFriend);
    }


}

