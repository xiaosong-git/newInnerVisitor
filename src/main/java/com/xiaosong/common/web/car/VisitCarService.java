package com.xiaosong.common.web.car;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VCar;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: gx
 * @Date: 2021/1/27 14:31
 * @Description: 来访车辆管理实现类
 */
public class VisitCarService {

    public static final VisitCarService me = new VisitCarService();

    public Page<VCar> getVisitCarList(int currentPage, int pageSize, String phone, String cStatus) {
        StringBuilder sql = new StringBuilder("  from v_car");
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        if (StringUtils.isNotBlank(phone)) {
            whereSql.append(" and phone like CONCAT('%','").append(phone).append("','%')");
        }
        if (StringUtils.isNotBlank(cStatus)) {
            whereSql.append(" and cStatus ='").append(cStatus).append("'");
        }
        whereSql.append(" order by id ");
        return VCar.dao.paginate(currentPage, pageSize, "select *", sql.append(whereSql).toString());
    }

    public int auditVisitCar(Long id, String cStatus) {
        return Db.update("update v_car set cStatus=? where id=? and cStatus = 'applyConfirm' ", cStatus, id);
    }

    public RetUtil insertVisitCar(VCar vCar) {
        vCar.setCStatus("applySuccess");
        vCar.setVisitDate(DateUtil.getCurDate());
        vCar.setVisitTime(DateUtil.getCurTime());
        vCar.setReplyDate(DateUtil.getCurDate());
        vCar.setReplyTime(DateUtil.getCurTime());
        vCar.setRecordType(Constant.INVITE);
        boolean save = vCar.save();
        return save ? RetUtil.ok("新增成功") : RetUtil.fail("新增失败");
    }
}
