package com.xiaosong.common.web.car;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VCar;
import com.xiaosong.util.DESUtil;
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

    public Page<VCar> getVisitCarList(int currentPage, int pageSize, String plate, String cStatus) {
        StringBuilder sql = new StringBuilder("  from v_car");
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        if (StringUtils.isNotBlank(plate)) {
            whereSql.append(" and plate ='").append(plate).append("'");
        }
        if (StringUtils.isNotBlank(cStatus)) {
            whereSql.append(" and cStatus ='").append(cStatus).append("'");
        }
        whereSql.append(" order by id ");
        return VCar.dao.paginate(currentPage, pageSize, "select *", sql.append(whereSql).toString());
    }

    public int auditVisitCar(Long userId, Long id, String cStatus) {
        String replyDate = DateUtil.getCurDate();
        String replyTime = DateUtil.getCurTime();
        return Db.update("update v_car set cStatus=?, replyUserId=?, replyDate=?, replyTime=? where id=? and cStatus = 'applyConfirm' ",
                cStatus, userId, replyDate, replyTime, id);
    }

    public int passVisitCar(Long id) {
        return Db.update("update v_car set cStatus='applyPass' where id=? and cStatus = 'applySuccess' ", id);
    }

    public RetUtil insertVisitCar(VCar vCar) {
        //直接放行
        vCar.setCStatus("applyPass");
        vCar.setVisitDate(DateUtil.getCurDate());
        vCar.setVisitTime(DateUtil.getCurTime());
        vCar.setReplyDate(DateUtil.getCurDate());
        vCar.setReplyTime(DateUtil.getCurTime());
        vCar.setRecordType(Constant.INVITE);
        //受访人为创建记录的人员
        vCar.setIntervieweeId(vCar.getLong("replyUserId"));
        //获取加密key
        Record user_key = Db.findFirst("select * from v_user_key");
        vCar.setIdNO(DESUtil.encode(user_key.getStr("workKey"), vCar.getStr("idNO")));
        boolean save = vCar.save();
        return save ? RetUtil.ok("新增成功") : RetUtil.fail("新增失败");
    }
}
