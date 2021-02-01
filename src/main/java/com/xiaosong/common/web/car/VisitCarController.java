package com.xiaosong.common.web.car;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.bean.dto.VisitCarAddDto;
import com.xiaosong.interceptor.jsonbody.JsonBody;
import com.xiaosong.model.VCar;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: gx
 * @Date: 2021/1/27 14:29
 * @Description: 来访车辆管理
 */
public class VisitCarController extends Controller {

    private Log log = Log.getLog(VisitCarController.class);

    private VisitCarService visitCarService = VisitCarService.me;

    /**
     * 来访车辆管理列表
     */
    public void getVisitCarList() {
        try {
            int currentPage = getInt("currentPage");
            int pageSize = getInt("pageSize");
            Page<Record> visitCarList = visitCarService.getVisitCarList(currentPage, pageSize, getPara("plate"), getPara("cStatus"));
            //获取加密key
            Record user_key = Db.findFirst("select * from v_user_key");
            for (Record record : visitCarList.getList()) {
                record.set("idNO", DESUtil.decode(user_key.getStr("workKey"), record.getStr("idNO")));
            }

            renderJson(RetUtil.okData(visitCarList));
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    /**
     * 来访车辆记录审核
     */
    public void auditVisitCar() {
        try {
            if (getLong("id") == null || StringUtils.isEmpty(getPara("cStatus")) || getLong("userId") == null) {
                renderJson(RetUtil.fail("参数缺失！"));
            }
            if (visitCarService.auditVisitCar(getLong("userId"), getLong("id"), getPara("cStatus")) > 0) {
                renderJson(RetUtil.ok());
            } else {
                renderJson(RetUtil.fail());
            }
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    /**
     * 来访车辆记录放行
     */
    public void passVisitCar() {
        try {
            if (getLong("id") == null) {
                renderJson(RetUtil.fail("参数缺失！"));
            }
            renderJson(visitCarService.passVisitCar(getLong("id")));
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    /**
     * 来访车辆记录放行统计
     */
    public void passVisitCarReport() {
        try {
            int currentPage = getInt("currentPage");
            int pageSize = getInt("pageSize");
            Page page = visitCarService.passVisitCarReport(currentPage, pageSize, get("startDate"), get("endDate"), getLong("deptId"), get("gate"));
            renderJson(RetUtil.okData(page));
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    /**
     * 新增来访车辆记录
     */
    public void insertVisitCar(@JsonBody VisitCarAddDto vCar) {
        try {
            if (vCar == null ||
                    StringUtils.isEmpty(vCar.getPlate()) || StringUtils.isEmpty(vCar.getUserName()) || StringUtils.isEmpty(vCar.getIdNO()) ||
                    StringUtils.isEmpty(vCar.getStartDate()) || StringUtils.isEmpty(vCar.getGate()) || vCar.getEndDate()==null || vCar.getReplyUserId() == null || StringUtils.isEmpty(vCar.getPhone())) {
                renderJson(RetUtil.fail("参数缺失！"));
            } else {
                Record manage = Db.findFirst("select du.*,d.dept_name from v_sys_user su left join v_dept_user du on su.tel=du.phone left join v_dept d on du.deptId=d.id where du.userType='staff' and du.currentStatus='normal' and su.id=?", vCar.getReplyUserId());
                if (manage == null || manage.getColumns().isEmpty()) {
                    renderJson(RetUtil.fail("未找到登入人的员工信息！"));
                    return;
                }
                VCar car = new VCar();
                //查询访客用户
                VDeptUser visitor = VDeptUser.dao.findFirst("select * from v_dept_user where realName=?  and phone =? ", vCar.getUserName(), vCar.getPhone());
                if (visitor == null || visitor._getAttrNames().length < 1) {
                    visitor = new VDeptUser();
                    //获取加密key
                    Record user_key = Db.findFirst("select * from v_user_key");
                    String idNO = DESUtil.encode(user_key.getStr("workKey"), vCar.getIdNO());

                    visitor.setIdNO(idNO).setRealName(vCar.getUserName()).setPhone(vCar.getPhone()).setUserType("visitor").setCreateDate(DateUtil.now()).setStatus("applySuc")
                            .setCurrentStatus("normal");
                    boolean save = visitor.save();
                }

                car.setVisitDept(manage.getStr("dept_name"));
                DateTime parse = DateUtil.parse(vCar.getStartDate());
                DateTime dateTime = DateUtil.offsetHour(parse, vCar.getEndDate());
                String endDate = DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss");
                car.setPlate(vCar.getPlate())
                        .setUserName(vCar.getUserName())
                        .setInOutType(vCar.getInOutType())
                        .setVisitId(visitor.getId())
                        .setVisitName(manage.getStr("realName"))
                        .setVisitPhone(manage.getStr("phone"))
                        .setIdNO(visitor.getIdNO())
                        .setReplyUserId(manage.getLong("id"))
                        .setIntervieweeId(manage.getLong("id"))
                        .setStartDate(vCar.getStartDate())
                        .setGate(vCar.getGate())
                        .setEndDate(endDate);
                renderJson(visitCarService.insertVisitCar(car));
            }
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }
}
