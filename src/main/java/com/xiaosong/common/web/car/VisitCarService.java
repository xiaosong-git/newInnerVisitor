package com.xiaosong.common.web.car;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VCar;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @Author: gx
 * @Date: 2021/1/27 14:31
 * @Description: 来访车辆管理实现类
 */
public class VisitCarService {

    public static final VisitCarService me = new VisitCarService();

    public Page<Record> getVisitCarList(int currentPage, int pageSize, String plate, String startDate, String endDate, String visitDept) {
        StringBuilder sql = new StringBuilder("  from v_car c left join v_dept_user du on c.replyUserId=du.id left join v_dept d on du.deptId = d.id  ");
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        if (StringUtils.isNotBlank(plate)) {
            whereSql.append(" and plate ='").append(plate).append("'");
        }
        if (StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)) {
            whereSql.append(" and DATE_FORMAT(CONCAT(visitDate,\" \",visitTime),'%Y-%m-%d %H:%i:%s')  between '").append(startDate).append("' and '").append(endDate).append("'");
        }else if (StringUtils.isNotBlank(startDate)&&StringUtils.isBlank(endDate)){
            whereSql.append(" and DATE_FORMAT(CONCAT(visitDate,\" \",visitTime),'%Y-%m-%d %H:%i:%s') >'").append(startDate).append("' ");
        }else if(StringUtils.isBlank(startDate)&&StringUtils.isNotBlank(endDate)){
            whereSql.append(" and DATE_FORMAT(CONCAT(visitDate,\" \",visitTime),'%Y-%m-%d %H:%i:%s') <'").append(endDate).append("' ");
        }
        if (StringUtils.isNotBlank(visitDept)){
            whereSql.append(" and visitDept like concat('%','").append(visitDept).append("','%')");
        }

        whereSql.append(" order by visitDate desc,visitTime desc ");
        return Db.paginate(currentPage, pageSize, "select userName,c.idNo,visitName,dept_name deptName,concat(visitDate,'',visitTime) visitTime,plate,(case inOutType when 0 then '按次' when 1 then '按时' end) inOutType,gate,du.realName replyUserName,concat(replyDate,' ',replyTime) replyTime,v.cStatus", sql.append(whereSql).toString());
    }

    public int auditVisitCar(Long userId, Long id, String cStatus) {
        String replyDate = DateUtil.getCurDate();
        String replyTime = DateUtil.getCurTime();
        return Db.update("update v_car set cStatus=?, replyUserId=?, replyDate=?, replyTime=? where id=?  ",
                cStatus, userId, replyDate, replyTime, id);
    }

    public RetUtil passVisitCar(Long id) {
        VCar vCar = VCar.dao.findFirst("select * from v_car c where c.id=?", id);
        if (vCar!=null&&vCar._getAttrNames().length>0) {
            if ("applyPass".equals(vCar.getStr("cStatus"))) {
                return RetUtil.fail("已放行，请勿重复放行！");
            }else if (!"applySuccess".equals(vCar.getStr("cStatus"))){
                return RetUtil.fail("未审核，请审核后放行！");
            }
            Long intervieweeId = vCar.getLong("intervieweeId");
            VDeptUser deptUser = VDeptUser.dao.findFirst("select * from v_dept_user where id=?", intervieweeId);
            if (deptUser!=null&&deptUser._getAttrNames().length>0){
                CodeService.me.pushMsg(deptUser, 5, null, null, vCar.getStartDate(), vCar.getUserName());
            }
        }
        int update = Db.update("update v_car set cStatus='applyPass' where id=? and cStatus = 'applySuccess' ", id);
        if (update>0){
           return RetUtil.ok();
        }
        return RetUtil.fail();
    }


    public RetUtil insertVisitCar(VCar vCar) {
        //直接放行
        vCar.setCStatus("applyPass");
        vCar.setVisitDate(DateUtil.getCurDate());
        vCar.setVisitTime(DateUtil.getCurTime());
        vCar.setReplyDate(DateUtil.getCurDate());
        vCar.setReplyTime(DateUtil.getCurTime());
        vCar.setRecordType(Constant.INVITE);


        boolean save = vCar.save();
        return save ? RetUtil.ok("新增成功") : RetUtil.fail("新增失败");
    }

    public Page passVisitCarReport(int currentPage, int pageSize, String startDate, String endDate, String visitDept, String gate) {
        StringBuilder sql = new StringBuilder("  from v_car c left join v_dept_user  u on c.intervieweeId=u.id  ");
        StringBuilder whereSql = new StringBuilder(" where c.cStatus='applyPass' and visitDept is not null ");
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            whereSql.append(" and c.visitDate between'").append(startDate).append("' and '").append(endDate).append("' ");
        } else if (StringUtils.isNotBlank(startDate)&&StringUtils.isBlank(endDate)){
            whereSql.append(" and c.visitDate >'").append(startDate).append("' ");
        }else if(StringUtils.isBlank(startDate)&&StringUtils.isNotBlank(endDate)){
            whereSql.append(" and c.visitDate <'").append(endDate).append("' ");
        }
        if (StringUtils.isNotBlank(gate)) {
            whereSql.append(" and gate like CONCAT('%','").append(gate).append("','%')");
        }
        if (StringUtils.isNotBlank(visitDept)){
            whereSql.append(" and visitDept like concat('%','").append(visitDept).append("','%')");
        }
        whereSql.append(" group by visitDept,gate ");
        return Db.paginate(currentPage, pageSize, "select c.visitDate,visitDept,gate,count(*) carNum", sql.append(whereSql).toString());

    }

    public List<Record> downReport(String plate, String startDate, String endDate, String visitDept){
        StringBuilder sql = new StringBuilder("select userName,c.idNo,visitName,dept_name deptName,concat(visitDate,'',visitTime) visitTime,plate,(case inOutType when 0 then '按次' when 1 then '按时' end) inOutType,gate,du.realName replyUserName,concat(replyDate,' ',replyTime) replyTime from v_car c left join v_dept_user du on c.replyUserId=du.id left join v_dept d on du.deptId = d.id  ");
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        if (StringUtils.isNotBlank(plate)) {
            whereSql.append(" and plate ='").append(plate).append("'");
        }
        if (StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)) {
            whereSql.append(" and DATE_FORMAT(CONCAT(visitDate,\" \",visitTime),'%Y-%m-%d %H:%i:%s')  between '").append(startDate).append("' and '").append(endDate).append("'");
        }else if (StringUtils.isNotBlank(startDate)&&StringUtils.isBlank(endDate)){
            whereSql.append(" and DATE_FORMAT(CONCAT(visitDate,\" \",visitTime),'%Y-%m-%d %H:%i:%s') >'").append(startDate).append("' ");
        }else if(StringUtils.isBlank(startDate)&&StringUtils.isNotBlank(endDate)){
            whereSql.append(" and DATE_FORMAT(CONCAT(visitDate,\" \",visitTime),'%Y-%m-%d %H:%i:%s') <'").append(endDate).append("' ");
        }
        if (StringUtils.isNotBlank(visitDept)){
            whereSql.append(" and visitDept like concat('%','").append(visitDept).append("','%')");
        }

        whereSql.append(" order by visitDate desc,visitTime desc ");
        return Db.find(sql.append(whereSql).toString());
    }
}
