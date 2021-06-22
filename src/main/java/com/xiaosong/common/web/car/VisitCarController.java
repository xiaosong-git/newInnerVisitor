package com.xiaosong.common.web.car;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.bean.dto.VisitCarAddDto;
import com.xiaosong.constant.Constant;
import com.xiaosong.interceptor.jsonbody.JsonBody;
import com.xiaosong.model.VCar;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.ExcelUtil;
import com.xiaosong.util.IdCardUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

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
            String startDate = get("startDate");
            String endDate = get("endDate");
            String visitDept = get("visitDept");

            Page<Record> visitCarList = visitCarService.getVisitCarList(currentPage, pageSize, getPara("plate"),getPara("cStatus"),startDate,endDate,visitDept);
            //获取加密key
            Record user_key = Db.findFirst("select * from v_user_key");
           String userId = getHeader("userId");
        if (StringUtils.isEmpty(userId)) {
            userId = get("userId");
        }
        boolean isAdmin= IdCardUtil.isAdmin(userId);
            for (Record record : visitCarList.getList()) {
                // 根据登入角色进行脱敏
//                System.out.println(record);
                String decode = DESUtil.decode(user_key.getStr("workKey"), record.getStr("idNo"));
//                log.info(decode);
                record.set("idNo", IdCardUtil.desensitizedDesIdNumber(decode,isAdmin));
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
                return;
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
            Page page = visitCarService.passVisitCarReport(currentPage, pageSize, get("startDate"), get("endDate"), get("visitDept"), get("gate"));
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

    public void downReport(){
        OutputStream os = null;
        try {
            String startDate = get("startDate");
            String endDate = get("endDate");
            String visitDept = get("visitDept");

            List<Record> visitCarList = visitCarService.downReport(getPara("plate"),getPara("cStatus"),startDate,endDate,visitDept);
            //获取加密key
            String userId = getHeader("userId");
            if (StringUtils.isEmpty(userId)) {
                userId = get("userId");
            }
            boolean isAdmin= IdCardUtil.isAdmin(userId);
            if (visitCarList != null && visitCarList.size() > 0){

                Record user_key = Db.findFirst("select * from v_user_key");
                for (Record record : visitCarList) {
                    //根据登入角色进行脱敏
                    record.set("idNo", IdCardUtil.desensitizedDesIdNumber(DESUtil.decode(user_key.getStr("workKey"), record.getStr("idNo")),isAdmin));
                }
                String systemTimeFourteen = com.xiaosong.util.DateUtil.getSystemTimeFourteen();
                String[] fields = {"姓名","身份证号","被访人/邀约人姓名","被访人/邀约人单位","访问时间","车牌号","通行方式","出入口","经办人","审核时间"};
                List<String> fieldsList = Arrays.asList(fields);

                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("来访车辆放行报表");

                //设置单元格行高，列宽
                sheet.setDefaultRowHeightInPoints(18);
                sheet.setDefaultColumnWidth(20);

                //标题
                HSSFRow rowTitle = sheet.createRow(0);
                HSSFCell cell = rowTitle.createCell(0);
                cell.setCellValue("来访车辆放行报表");
                sheet.addMergedRegion( new CellRangeAddress(0,0,0,fields.length-1));
                //设置表标题样式
                HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.ALIGN_CENTER, HSSFColor.SKY_BLUE.index, "新宋体", (short) 12, true);
                cell.setCellStyle(cellStyle);
                //创建字段栏目
                cellStyle = ExcelUtil.createCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.ALIGN_CENTER, HSSFColor.YELLOW.index, "新宋体", (short) 12, true);
                HSSFRow rowFiled = sheet.createRow(1);
                for (int i = 0; i < fieldsList.size(); i++){
                    ExcelUtil.createCell(rowFiled,cellStyle,fieldsList.get(i),i);
                }

                HSSFRow row;
                int index = 2;
                cellStyle = ExcelUtil.createCellStyle(workbook, HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.ALIGN_CENTER, HSSFColor.WHITE.index, "新宋体", (short) 12, false);
                for (Record record : visitCarList) {
                    row = sheet.createRow(index);
                    //姓名
                    ExcelUtil.createCell(row,cellStyle,record.get("userName"),0);
                    //身份证号
                    ExcelUtil.createCell(row,cellStyle,record.get("idNo"),1);
                    //被访人/邀约人姓名
                    ExcelUtil.createCell(row,cellStyle,record.get("visitName"),2);
                    //被访人/邀约人单位
                    ExcelUtil.createCell(row,cellStyle,record.get("deptName"),3);
                    //访问时间
                    ExcelUtil.createCell(row,cellStyle,record.get("visitTime"),4);
                    //车牌号
                    ExcelUtil.createCell(row,cellStyle,record.get("plate"),5);
                    //通行方式
                    ExcelUtil.createCell(row,cellStyle,record.get("inOutType"),6);
                    //出入口
                    ExcelUtil.createCell(row,cellStyle,record.get("gate").toString(),7);
                    //经办人
                    ExcelUtil.createCell(row,cellStyle,record.get("replyUserName"),8);
                    //审核时间
                    ExcelUtil.createCell(row,cellStyle,record.get("replyTime"),9);
                    index++;
                }
                String fileName = String.format("来访车辆放行报表_%s.xls",systemTimeFourteen);
                String fileNameUrl = Constant.BASE_DOWNLOAD_PATH;
                File exportFile = new File(fileNameUrl);
                File file = new File(exportFile,fileName);
                if(!exportFile.exists()){
                    exportFile.mkdirs();
                    if (!file.exists()){
                        file.createNewFile();
                    }
                }else {
                    if (!file.exists()){
                        file.createNewFile();
                    }
                }
                os = new FileOutputStream(file);
                workbook.write(os);
                os.flush();
                os.close();
                renderFile(file);
            }
        }catch (Exception e){
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }finally {
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
