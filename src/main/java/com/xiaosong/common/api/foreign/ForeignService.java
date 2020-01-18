package com.xiaosong.common.api.foreign;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
        String posp = Db.queryStr(Db.getSql("foreign.findOrgCode"), pospCode, orgCode);
        if (posp == null) {
            return Result.unDataResult("fail", "无此上位机编码" + pospCode + "或者无此大楼编码" + orgCode);
        }
        String columnSql = "select vr.id visitId,vr.userId,vr.visitDate,vr.visitTime,vr.orgCode,vr.dateType,vr.startDate,vr.endDate,u.realName userRealName,u.idType userIdType,u.idNO userIdNO,u.soleCode soleCode,u.idHandleImgUrl idHandleImgUrl,c.companyFloor companyFloor,v.realName vistorRealName,v.idType vistorIdType,v.idNO visitorIdNO,o.province province,o.city city";
        String fromSql = " from " + TableList.VISITOR_RECORD + " vr " + " left join " + TableList.DEPT_USER
                + " v on vr.visitorId=v.id" + " left join " + TableList.DEPT_USER + " u on vr.userId=u.id" + " left join " + TableList.COMPANY + " c on vr.companyId=c.id"
                + " left join " + TableList.ORG + " o on v.orgId=o.id"
                + " where vr.cstatus='applySuccess' and vr.orgCode = '" + orgCode + "'"
                + " and vr.startDate<=date_add(now(),interval +30 minute) and vr.endDate>= date_add(now(),interval -30 minute) and isFlag='F' ";
        String count = "select count(*) " + fromSql;
        String orderBy = "order by vr.id";
        Page<Record> recordPage = Db.paginateByFullSql(pageNum, pageSize, count, columnSql + fromSql + orderBy);
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

