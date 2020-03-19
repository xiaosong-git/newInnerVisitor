package com.xiaosong.common.api.foreign;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.Base64;
import com.xiaosong.util.FilesUtils;

import java.util.List;
import java.util.Map;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2020-01-11 15:46
 **/
public class ForeignService extends MyBaseService {
    public Result newFindOrgCodeConfirm(String pospCode, String orgCode, String idStr) {
        String posp = Db.queryStr(Db.getSql("foreign.findOrgCode"), pospCode, orgCode);
        if (posp == null) {
            return Result.unDataResult("fail", "无此上位机编码" + pospCode + "或者无此大楼编码" + orgCode);
        }
        int update = Db.update("update " + TableList.VISITOR_RECORD + " set isFlag='T' where id in (" + idStr + ")");
        if (update > 0) {
            return Result.success();
        }
        return Result.fail();
    }

    public Result findOrgCode(String pospCode, String orgCode, Integer pageNum, Integer pageSize) {

        String columnSql = "SELECT vr.id visitId,vr.userId,vr.visitDate,vr.visitTime,vr.orgCode,vr.dateType,vr.startDate," +
                "vr.endDate,u.realName userRealName,'01' userIdType,u.idNO userIdNO,u.idHandleImgUrl,d.floor companyFloor,du.realName vistorRealName,'01' vistorIdType,du.idNO visitorIdNO ";
        String fromsql = Db.getSql("visitRecord.findOrgCode");
        String count = "select count(*) " + fromsql;
        String orderBy = "order by vr.id";
        Page<Record> recordPage = Db.paginateByFullSql(pageNum, pageSize, count, columnSql + fromsql + orderBy,orgCode);
        //有数据 获取图片并插入
        List<Record> recordPageList = recordPage.getList();
        if (!recordPageList.isEmpty()) {
            for (int i = 0; i < recordPageList.size(); i++) {
                Map<String, Object> map = recordPageList.get(i).getColumns();
                String idHandleImgUrl = (String) map.get("idHandleImgUrl");
                if (idHandleImgUrl != null && idHandleImgUrl.length() != 0) {
                    String imageServerUrl = ParamService.me.findValueByName("imageServerUrl");
                    String photo = Base64.encode(FilesUtils.getImageFromNetByUrl(imageServerUrl + idHandleImgUrl));
                    recordPageList.get(i).set("photo", photo);
                }
            }
        }
        return !recordPageList.isEmpty()
                ? ResultData.dataResult("success", "获取授权访问信息成功", apiList(recordPageList))
                : ResultData.dataResult("success", "暂无数据", recordPageList);
    }
}

