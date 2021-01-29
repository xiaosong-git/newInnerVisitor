package com.xiaosong.common.web.car;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.interceptor.jsonbody.JsonBody;
import com.xiaosong.model.VCar;
import com.xiaosong.util.RetUtil;

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
            Page<VCar> visitCarList = visitCarService.getVisitCarList(currentPage, pageSize, getPara("phone"), getPara("cStatus"));
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
            if (visitCarService.auditVisitCar(getLong("id"), getPara("cStatus")) > 0) {
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
     * 新增来访车辆记录
     */
    public void insertVisitCar(@JsonBody VCar vCar) {
        try {
            if (vCar == null || vCar._getAttrNames().length == 0) {
                renderJson(RetUtil.fail("参数缺失！"));
            } else {
                renderJson(visitCarService.insertVisitCar(vCar));
            }
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }
}
