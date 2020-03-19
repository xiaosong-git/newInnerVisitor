package com.xiaosong.common.api.work;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.MainConfig;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.*;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.DateUtil;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: visitor
 * @description:
 * @author: cwf
 * @create: 2019-11-04 10:25
 **/

public class CheckInWorkService extends MyBaseService {
    Log log = Log.getLog(CheckInWorkService.class);
    public static final CheckInWorkService me = new CheckInWorkService();
    Prop p = MainConfig.p;

//    /**
//     * 1、插入tbl_wk_group规则字段，得到规则id。字段：id，company_id,group_type，group_name，sync_holidays，need_photo，note_can_use_local_pic，allow_checkin_offworkday，allow_apply_offworkday
//     * 2、根据规则id插入规则相关打卡日期表tbl_wk_checkindate，打卡时间表tbl_wk_checkintime，打卡日期时间关系表：tbl_wk_date_time_rlat地址表tbl_wk_loc_infos。特殊日期表tbl_wk_spe_days
//     * 3、修改旧规则时，需要新增一个新规则，保存旧规则，保存修改时间，以便进行判断
//     * 4、同一个用户，只对最新规则进行判断
//     * 新增打卡时间限制，并且中午下班打卡时间限制不得与中午上班打卡时间限制有交叉？·
//     *
//     * @param jsonObject
//     * @return
//     */
//
//    public Result saveGroup(JSONObject jsonObject) {
//        Record groupRecord = new Record();//规则
//        Integer companyId = BaseUtil.objToInteger(jsonObject.get("companyId"), 0);
//        Integer groupType = BaseUtil.objToInteger(jsonObject.get("groupType"), 0);
//        String groupName = BaseUtil.objToStr(jsonObject.get("groupName"), "");
//        String syncHolidays = BaseUtil.objToStr(jsonObject.get("sync_holidays"), "F");
//        String needPhoto = BaseUtil.objToStr(jsonObject.get("need_photo"), "F");
//        String noteCanUseLocalPic = BaseUtil.objToStr(jsonObject.get("note_can_use_local_pic"), "F");
//        String allowCheckinOffworkday = BaseUtil.objToStr(jsonObject.get("allow_checkin_offworkday"), "F");
//        String allowApplyOffworkday = BaseUtil.objToStr(jsonObject.get("allow_apply_offworkday"), "F");
//        groupRecord.set("company_id", companyId);
//        groupRecord.set("group_type", groupType);
//        groupRecord.set("group_name", groupName);
//        groupRecord.set("sync_holidays", syncHolidays);
//        groupRecord.set("need_photo", needPhoto);
//        groupRecord.set("note_can_use_local_pic", noteCanUseLocalPic);
//        groupRecord.set("allow_checkin_offworkday", allowCheckinOffworkday);
//        groupRecord.set("allow_apply_offworkday", allowApplyOffworkday);
//        groupRecord.set("effective_time", DateUtil.getCurDate());
//        Record dateRecord = new Record();
//        Record timeRecord = new Record();
//        //------------------打卡时间不能为空-----------------
//        JSONArray datesArray = jsonObject.getJSONArray("checkInDate");
//        AtomicReference<JSONArray> timeInterval = new AtomicReference<>();//打卡时间区间
//        if (datesArray == null || datesArray.size() == 0) {
//            return Result.unDataResult("fail", "未传入打卡日期！");
//        } else {
//            //上下班时间区间不能为空
//            if (datesArray.getJSONObject(0).getJSONArray("timeInterval") == null) {
//                return Result.unDataResult("fail", "未传入上下班时间！");
//            }
//        }
//        //------------------打卡地点不能为空-----------------
//        JSONArray locsArray = jsonObject.getJSONArray("locInfos");//获取地址数组
//        AtomicReference<JSONObject> locObject = new AtomicReference<>();
//        log.info("{}", locsArray.size());
//        if (locsArray.size() == 0) {
//            return Result.unDataResult("fail", "未传入打卡地址！");
//        }
//        //-----------打卡用户不能为空
//        JSONArray userList = jsonObject.getJSONArray("userList");
//        if (userList == null || userList.size() == 0) {
//            return Result.unDataResult("fail", "未传入打卡用户！");
//        }
//        //事务
//        boolean tx = Db.tx(() -> {
//            Db.update("update " + TableList.WK_GROUP + " set failure_time =NOW()  where company_id=" + companyId + " ORDER BY id desc  limit 1");
//            boolean save = Db.save(TableList.WK_GROUP, groupRecord);
//            //插入规则相关数据
//            int remind = BaseUtil.objToInteger(jsonObject.get("remind"), 0);
//            //暂时设置为1测试
//            int id = BaseUtil.objToInteger(groupRecord.get("id"),0);
//            log.info("生成规则id为：{}",id);
////            ------------------工作日期----------------------------
//            int dateId = 0;
//            int timeId = 0;
//            JSONObject dateObject;//日期
//            //添加规则中打卡日期与时间
//            List<Record> dtrRecords = new LinkedList<>();//批量记录
//            Record dtrRecord;//日期时间关系model
//            for (int i = 0; i < datesArray.size(); i++) {
//
//                dateObject = datesArray.getJSONObject(i);
//                timeInterval.set(dateObject.getJSONArray("timeInterval"));
//                dateRecord.set("group_id", id);
//                dateRecord.set("workDays", dateObject.getString("workDays"));
////                checkInDateMap.set("flex_time",dateObject.getString("flex_time"));
//                dateRecord.set("noneed_offwork", dateObject.getString("noneedOffwork"));
//                dateRecord.set("limit_aheadtime", dateObject.getString("limitAheadtime"));
//                dateRecord.remove("id");
//                Db.save(TableList.WK_CHECKINDATE, dateRecord);
//                dateId = dateRecord.getInt("id");
//
//                //用dateId插入checkInTime
//                if (timeInterval.get() != null) {
//                    for (int j = 0; j < timeInterval.get().size(); j++) {
//                        dtrRecord = new Record();//日期时间关系model
//                        dtrRecord.set("date_id", dateId);
//                        if (j % 2 == 0) {
//                            log.info("上班时间：" + timeInterval.get().getInteger(j));
//                            log.info("上班提醒时间：" + (timeInterval.get().getInteger(j) - jsonObject.getInteger("remind")));
//                            timeRecord.set("work_sec", timeInterval.get().getInteger(j));
//                            timeRecord.set("remind_work_sec", (timeInterval.get().getInteger(j) - jsonObject.getInteger("remind")));
//
//                        } else {
//                            log.info("下班时间：" + timeInterval.get().getInteger(j));
//                            log.info("下班提醒时间：" + (timeInterval.get().getInteger(j) - jsonObject.getInteger("remind")));
//                            timeRecord.set("off_work_sec", timeInterval.get().getInteger(j));
//                            timeRecord.set("remind_off_work_sec", (timeInterval.get().getInteger(j) - jsonObject.getInteger("remind")));
//                            timeRecord.remove("id");
//                            Db.save(TableList.WK_CHECKINTIME, timeRecord);
//                            timeId = timeRecord.getInt("id");
//                            //拼接批量插入语句
//                            dtrRecord.set("time_id", timeId);
//                            dtrRecords.add(dtrRecord);
//                        }
//                    }
//                }
//            }
//
////            int[] ints = baseDao.batchUpdate(dateTimeRlatPrefixSql + dateTimeRlatsuffixSql.substring(0, dateTimeRlatsuffixSql.length() - 1));
////            "loc_title":"福州软件园G区1#楼","loc_detail":"福建省福州市闽侯县","lat":"30547030","lng":"104062890","distance":"300"
//            //------------------地址----------------------------
//
//
//            List<Record> localRecords = new LinkedList<>();
//            Record localRecord;//地址record
//            for (int k = 0; k < locsArray.size(); k++) {//获取多个打卡地址
//                localRecord = new Record();//地址record
//                locObject.set(locsArray.getJSONObject(k));
//                localRecord.set("group_id", id).set("lat", BaseUtil.objToStr(locObject.get().get("lat"), ""))
//                        .set("lng", BaseUtil.objToStr(locObject.get().get("lng"), ""))
//                        .set("loc_title", locObject.get().get("locTitle"))
//                        .set("loc_detail", locObject.get().get("locDetail"))
//                        .set("distance", BaseUtil.objToStr(locObject.get().get("distance"), "300"));//默认300米
//                localRecords.add(localRecord);
//            }
//
//            // ------------------打卡用户表----------------------------
//            List<Record> userRecords = new LinkedList<>();
//            Record userRecord;//用户打卡关系
//            for (Object userId : userList) {
//                userRecord = new Record();
//                userRecord.set("group_id", id).set("user_id", userId);
//                userRecords.add(userRecord);
//            }
//            //批量插入日期时间关系表
//            String columns = "date_id,time_id";
//            String sql = "insert into " + TableList.WK_DATE_TIME_RLAT + "(" + columns + ") values(?,?)";
//            int[] batch = Db.batch(sql, columns, dtrRecords, timeInterval.get().size());
//            //批量插入打卡地点表
//            columns = "group_id,lat,lng,loc_title,loc_detail,distance";
//            sql = "insert into " + TableList.WK_LOC_INFOS + "(" + columns + ") values(?,?,?,?,?,?)";
//            batch = Db.batch(sql, columns, localRecords, locsArray.size());
//            //批量插入用户规则关系
//            columns = "group_id,user_id";
//            sql = "insert into " + TableList.WK_USER_GROUP_RLAT + "(" + columns + ") values (?,?)";
//            batch = Db.batch(sql, columns, userRecords, userList.size());
//            //插入白名单-----------可以为空
//            JSONArray whiteList = jsonObject.getJSONArray("whiteList");
//            if (whiteList != null && whiteList.size() != 0) {
//                List<Record> whiteRecords = new LinkedList<>();
//                Record whiteRecord;//用户白名单打卡关系
//                for (Object userId : whiteList) {
//                    whiteRecord = new Record();
//                    whiteRecord.set("group_id", id).set("user_id", userId);
//                    whiteRecords.add(whiteRecord);
//                }
//                columns = "group_id,user_id";
//                sql = "insert into " + TableList.WK_WHITE_LIST + "(" + columns + ") values (?,?)";
//                batch = Db.batch(sql, columns, whiteRecords, userList.size());
//            }
//            //------------------特殊日期----------------------------
////            JSONArray speWorkdays = jsonObject.getJSONArray("speWorkdays");
////            if(speWorkdays!=null&&speWorkdays.size()!=0) {
////                int speDateId = 0;
////                int spetimeId = 0;
////                JSONObject speDateObject = null;
////                JSONArray speTimeInterval = null;
////                Record speInDateRecord;
////                Record speInTimeRecord;
////                StringBuffer speTimeRlatPrefixSql = new StringBuffer("insert into " + TableList.WK_SPE_DAYS_TIME_RLAT + "(spe_id,time_id) values");
////                StringBuffer speTimeRlatsuffixSql = new StringBuffer();
//////            //添加特殊日期规则中打卡日期与时间
////                for (int l = 0; l < speWorkdays.size(); l++) {
////                    speDateObject = speWorkdays.getJSONObject(l);
////                    speTimeInterval = speDateObject.getJSONArray("time_interval");
////                    speInDateRecord=new Record();
////                    speInDateRecord.set("group_id", id);
////                    speInDateRecord.set("spe_date", speDateObject.getString("spe_date"));
////                    speInDateRecord.set("type", speDateObject.getString("type"));
////                    speInDateRecord.set("notes", speDateObject.getString("notes"));
////                    speDateId = save(TableList.WK_SPE_DAYS, speInDateMap);
////                    //用dateId插入checkInTime
////                    if (speTimeInterval != null) {
////                        for (int j = 0; j < speTimeInterval.size(); j++) {
////                            if (j % 2 == 0) {
////                                log.info("上班时间：" + speTimeInterval.getInteger(j));
////                                log.info("上班提醒时间：" + (speTimeInterval.getInteger(j) - Integer.valueOf(jsonObject.get("remind"))));
////                                checkInTimeMap.put("work_sec", speTimeInterval.getInteger(j));
////                                checkInTimeMap.put("remind_work_sec", (speTimeInterval.getInteger(j) - Integer.valueOf(jsonObject.get("remind"))));
////
////                            } else {
////                                log.info("下班时间：" + speTimeInterval.getInteger(j));
////                                log.info("下班提醒时间：" + (speTimeInterval.getInteger(j) - Integer.valueOf(jsonObject.get("remind"))));
////                                checkInTimeMap.put("off_work_sec", speTimeInterval.getInteger(j));
////                                checkInTimeMap.put("remind_off_work_sec", (speTimeInterval.getInteger(j) - Integer.valueOf(jsonObject.get("remind"))));
////                                spetimeId = save(TableList.WK_CHECKINTIME, checkInTimeMap);
////                                log.info("tbl_wk_checkintime 并获取spetimeId：{}",spetimeId);
////                                //拼接批量插入语句
////                                speTimeRlatsuffixSql.append("(" + speDateId + "," + spetimeId + "),");
////                            }
////                        }
////                    }
////                    log.info(speDateObject.toJSONString());
////                }
////                //插入date与time关系
////                int[] spes = baseDao.batchUpdate(speTimeRlatPrefixSql + speTimeRlatsuffixSql.substring(0, speTimeRlatsuffixSql.length() - 1));
////                log.info("批量插入特殊日期关系表语句：\n{}",speTimeRlatPrefixSql + speTimeRlatsuffixSql.substring(0, speTimeRlatsuffixSql.length() - 1));
////            }
//            return true;
//        });
//        return Result.unDataResult("success", "保存打卡规则成功！");
//    }
//
//    /**
//     * 判断是否为白名单内，白名单无需打卡
//     * 判断是否特殊日期，如果是，则按照特殊日期打卡
//     *
//     * @param userId    用户id
//     * @param companyId 公司id
//     * @param date      时间 YY-MM-DD
//     * @return Result
//     * @throws Exception
//     * @author cwf
//     * @date 2019/11/13 17:20
//     */
//    public Result gainWork(Long userId, Long companyId, String date) throws ParseException {
//
//        if (userId == null || companyId == null || date == null) {
//            return Result.unDataResult("fail", "缺少参数！");
//        }
//        //获取打卡规则
//        String groupSql = "select user_id,wg.* from " + TableList.WK_GROUP + " wg \n" +
//                "left join " + TableList.WK_USER_GROUP_RLAT +
//                " wugr on wugr.group_id=wg.id where user_id=" + userId + " and company_id=" + companyId + " ORDER BY wg.id desc";
//        Record groupRecord = Db.findFirst(groupSql);
//
//        log.info("用户{}的规则sql:\n{}", userId, groupSql);
//        Long groupId = BaseUtil.objToLong(groupRecord.get("id"), null);
//        Map<String, Object> map = new HashMap<>();
//        map.put("group", groupRecord.getColumns());
//        if (groupId == null) {
//            return Result.unDataResult("fail", "未找到用户规则");
//        } else {
//            //获取白名单,如果用户是白名单内，不需要打卡，返回
//            Record white = Db.findFirst("select * from " + TableList.WK_WHITE_LIST + " where user_id=" + userId + " and group_id=" + groupId);
//            if (white != null) {
//                //返回信息 code=201=白名单
//                return Result.ResultCode("success", "用户在白名单内", "201");
//            }
//
//            //获取地址信息
//            String coloumSql = "select lat,lng,loc_title,loc_detail,distance ";
//            String fromSql = " from " + TableList.WK_LOC_INFOS + " where group_id=" + groupId;
//            List<Record> locInfosList = Db.find(coloumSql + fromSql);
//
//            String startDate = DateUtil.getDate(date);
//            String nextDate = DateUtil.NextDate(date);
//            //当天打卡记录 有效记录未筛选
//            coloumSql = "select * ";
//            fromSql = " from " + TableList.WK_RECORD + " where group_id=" + groupId + " and user_id=" + userId +
//                    " and checkin_time between '" + startDate + "' and '" + nextDate + "'";
//            log.info("用户{}的打卡记录sql:\n{}", userId, coloumSql + fromSql);
//            List<Record> dayWork = Db.find(coloumSql + fromSql);
//
//            map.put("dayWork", apiList(dayWork));
//            //插入地址信息
//            map.put("loc_infos", apiList(locInfosList));
//            /**
//             * 判断今天是星期几，如果星期在workDays之间，则当日为工作日
//             * @date 2019/11/20 15:25
//             */
//            //获取打卡时间
//            coloumSql = "select id,workdays,noneed_offwork,limit_aheadtime";
//            fromSql = " from " + TableList.WK_CHECKINDATE + " where group_id=" + groupId;
//
//            String week = String.valueOf(DateUtil.getWeek(date));
//            List<Record> checkDateList = Db.find(coloumSql + fromSql);
//            Record checkDateMap;
//            Object checdDateId;
//
//            for (int i = 0; i < checkDateList.size(); i++) {
//                //打卡日期数据
//                checkDateMap = checkDateList.get(i);
//                checdDateId = checkDateMap.get("id");
//                String workdays = BaseUtil.objToStr(checkDateMap.get("workdays"), ",");
//                String[] weekDay = workdays.split(",");
//                for (String s : weekDay) {
//                    //星期存在日期则查找时间区间
//                    if (week.equals(s)) {
//                        coloumSql = "select work_sec,off_work_sec,remind_work_sec,remind_off_work_sec ";
//                        fromSql = " from " + TableList.WK_CHECKINTIME + " " +
//                                "wt left join " + TableList.WK_DATE_TIME_RLAT + " " +
//                                "dtr on wt.id=dtr.time_id where dtr.date_id=" + checdDateId;
//                        log.info("用户{}的需要打卡的时间sql:\n{}", userId, coloumSql + fromSql);
//                        List<Record> records = Db.find(coloumSql + fromSql);
//                        map.put("interval", apiList(records));
//                        return ResultData.dataResult("success", "获取打卡规则成功", map);
//                    }
//                }
//            }
//            return ResultData.dataResult("success", "获取打卡规则成功", map);
//        }
//    }



//    /**
//     * 日历 个人日打卡记录
//     * 统计这个月的每一天是否有异常，前端显示红点--有异常，白点--正常
//     * @param paramMap
//     * @return com.goldccm.model.compose.Result
//     * @throws Exception
//     * @author cwf
//     * @date 2019/11/15 15:28
//     */
//    @Override
//    public Result gainCalendarStatistics(Map<String, Object> paramMap) {
//        /**
//         *         统计某个月每一天的异常情况
//         *         1、查询规则
//         *         查看规则是否变化，根据规则表的变动时间进行查询
//         *         2、比对规则
//         *
//         */
//
//        return null;
//    }

    /**
     * 管理员 查看的月统计，月报
     * 日统计，一、上下班统计：1、迟到分钟数 2、早退分钟数 3、旷工分钟数 4、缺卡分钟数 5、地点异常数 6设备异常数
     *        二、假勤统计：1、打卡补卡 2、外勤 3、外出 4、出差 、5、年假 6、事假 7、病假 8、调休假 9、婚假 10、产假 11、陪产假 12、其他
     * 备注：当天没有打卡记录为旷工，当天有部分打卡为缺卡。
     * @param date 查看的月份
     * @return com.goldccm.model.compose.Result
     * @throws Exception
     * @author cwf
     * @date 2019/11/19 10:04
     */
//    public Result gainMonthStatistics(String date,Long groupId) throws ParseException {
//        String monthFirstDay = DateUtil.getMonthFirstDay(date);//月初
//        String monthLastDay = DateUtil.getMonthLastDay(date);//月末
//        String sql="select count(late>0 or NULL) late ,count(early>0 or NULL) early,count(absent>0 or NULL) absent,count(location>0 or NULL) location,count(equipment>0 or NULL) equipment from (\n" +
//                "select user_id,sum(late) late,sum(early) early,sum(absent) absent,sum(location) location,sum(equipment) equipment from wk_day_statistics \n" +
//                "where group_id=? and need_checkin_date BETWEEN ? and ? " +
//                "group by user_id)x";
//        Record first = Db.findFirst(sql, groupId, monthFirstDay, monthLastDay);
//
//        if (first!=null) {
//            return ResultData.dataResult("success", "成功", first.getColumns());
//        }else{
//            return Result.unDataResult("success", "暂无数据");
//        }
//    }

//    public Result gaindayStatistics(String dateTime,Long groupId) throws ParseException {
//        String sql="select count(late>0 or NULL) late ,count(early>0 or NULL) early,count(absent>0 or NULL) absent,count(location>0 or NULL) location,count(equipment>0 or NULL) equipment from (\n" +
//                "select user_id,sum(late) late,sum(early) early,sum(absent) absent,sum(location) location,sum(equipment) equipment from wk_day_statistics \n" +
//                "where group_id=? and need_checkin_date =? " +
//                "group by user_id)x";
//        Record first = Db.findFirst(sql, groupId,dateTime);
//        if (first!=null) {
//            return ResultData.dataResult("success", "成功", first.getColumns());
//        }else{
//            return Result.unDataResult("success", "暂无数据");
//        }
//    }
    /**
     * 打卡
     *
     * @param wkRecord
     * @return com.goldccm.model.compose.Result
     * @throws Exception
     * @author cwf
     * @date 2019/11/15 13:38
     */
//    public Result saveWork(WkRecord wkRecord) {
//
//
//        boolean save = wkRecord.save();
//        if (save) {
//            return Result.unDataResult("success", "打卡成功");
//        }
//        return Result.unDataResult("fail", "打卡失败");
//    }
//    public Result gainDay(Long userId, String date,Long companyId) {
//
//        String fromsql= "select * from "+TableList.WK_RECORD+" where user_id="+userId+" and checkin_date='"+date+"'"+
//                " and company_id="+companyId;
//        List<Record> records = Db.find(fromsql);
//
//        return ResultData.dataResult("success","成功",apiList(records));
//    }
}
