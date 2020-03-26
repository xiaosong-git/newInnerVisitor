package com.xiaosong.common.api.foreign;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.base.MyBaseService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.MyRecordPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.Base64;
import com.xiaosong.util.FilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2020-01-11 15:46
 **/
public class ForeignService extends MyBaseService {
    Logger log = LoggerFactory.getLogger(ForeignService.class);
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
        pageNum=pageNum==null?1:pageNum;
        pageSize=pageSize==null?10:pageSize;

        String columnSql = "SELECT vr.id visitId,vr.userId,vr.visitDate,vr.visitTime,vr.orgCode,vr.dateType,vr.startDate," +
                "vr.endDate,u.realName userRealName,'01' userIdType,u.idNO userIdNO,u.idHandleImgUrl,d.floor companyFloor,du.realName vistorRealName,'01' vistorIdType,du.idNO visitorIdNO ";
        String fromsql = Db.getSql("visitRecord.findOrgCode");
        String count = "select count(*) " + fromsql;
        String orderBy = "order by vr.id";
        Page<Record> recordPage = Db.paginateByFullSql(pageNum, pageSize, count, columnSql + fromsql + orderBy,orgCode);
        //有数据 获取图片并插入
        List<Record> rows = recordPage.getList();
        MyRecordPage myPage = new MyRecordPage(apiList(rows), pageNum, pageSize, recordPage.getTotalPage(), recordPage.getTotalRow());
       return insertUserPhoto(rows);
    }

    public Result insertUserPhoto(List<Record> rows) {
        String photo = null;
        StringBuilder errorId = new StringBuilder();
        String idHandleImgUrl;
        String  imageServerUrl = ParamService.me.findValueByName("imageServerUrl");
        for (Record row : rows) {
            idHandleImgUrl = row.get("idHandleImgUrl");
            if (idHandleImgUrl != null && idHandleImgUrl.length() != 0) {
//             //生产图片地址
                try {
                    photo = Base64.encode(FilesUtils.getImageFromNetByUrl(imageServerUrl + idHandleImgUrl));
                } catch (Exception e) {
                    errorId.append(row.get("userId") + ",");
                }
                row.set("photo", photo);
            }else {
                row.set("photo", "");
            }
        }
        log.error("错误照片的用户id:{}", errorId);
        if ("".contentEquals(errorId)) {
            return !rows.isEmpty()
                    ? ResultData.dataResult("success", "获取大楼员工信息成功", apiList(rows))
                    : Result.unDataResult("success", "暂无数据");
        }
        return !rows.isEmpty()
                ? ResultData.dataResult("success", "获取大楼员工信息成功" + ",错误照片的用户id:" + errorId, apiList(rows))
                : Result.unDataResult("success", ",错误照片的用户id:" + errorId);
    }
}

