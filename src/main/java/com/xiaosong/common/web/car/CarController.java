package com.xiaosong.common.web.car;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.web.inOut.InOutController;
import com.xiaosong.constant.Constant;
import com.xiaosong.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CarController extends Controller {

    private Log log = Log.getLog(InOutController.class);
    public CarService srv = CarService.me;

    public void findList(){
        try {
            String userName = getPara("userName");
            String visitName = getPara("visitName");
            String plate = getPara("plate");
            String startTime = getPara("startTime");
            String endTime = getPara("endTime");
            String visitDept = getPara("visitDept");
            int pageNum = getInt("pageNum");
            int pageSize = getInt("pageSize");
            Page<Record> pagelist = srv.findList(userName,visitName,plate,startTime,endTime,visitDept,pageNum,pageSize);
            Record user_key = Db.findFirst("select * from v_user_key");
            String userId = getHeader("userId");
            if (StringUtils.isEmpty(userId)) {
                userId = get("userId");
            }
            boolean isAdmin= IdCardUtil.isAdmin(userId);
            for (Record record : pagelist.getList()) {
                //根据登入角色进行脱敏
                record.set("idNO", IdCardUtil.desensitizedDesIdNumber(DESUtil.decode(user_key.getStr("workKey"), record.getStr("idNO")),isAdmin));
            }
            renderJson(RetUtil.okData(pagelist));
        }catch (Exception e){
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    public void downReport(){
        OutputStream os = null;
        try {
            String userName = getPara("userName");
            String visitName = getPara("visitName");
            String plate = getPara("plate");
            String startTime = getPara("startTime");
            String endTime = getPara("endTime");
            String visitDept = getPara("visitDept");
            //获取列表
            List<Record> downReportList = srv.downReport(userName,visitName,plate,startTime,endTime,visitDept);
           String userId = getHeader("userId");
        if (StringUtils.isEmpty(userId)) {
            userId = get("userId");
        }
        boolean isAdmin= IdCardUtil.isAdmin(userId);
            if (downReportList != null && downReportList.size() > 0){
                Record user_key = Db.findFirst("select * from v_user_key");
                for (Record record : downReportList) {
                    // 根据登入角色进行脱敏
                    record.set("idNO", IdCardUtil.desensitizedDesIdNumber(DESUtil.decode(user_key.getStr("workKey"), record.getStr("idNO")),isAdmin));
                }
                String systemTimeFourteen = DateUtil.getSystemTimeFourteen();
                String[] fields = {"来访人姓名","受访人","受访人单位","车牌号","来访总人数","来访人员身份证","审核人员","审核时间","审核结果"};
                List<String> fieldsList = Arrays.asList(fields);

                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("车辆大数据报表");

                //设置单元格行高，列宽
                sheet.setDefaultRowHeightInPoints(18);
                sheet.setDefaultColumnWidth(20);

                //标题
                HSSFRow rowTitle = sheet.createRow(0);
                HSSFCell cell = rowTitle.createCell(0);
                cell.setCellValue("车辆大数据报表");
                sheet.addMergedRegion( new CellRangeAddress(0,0,0,fields.length -1));
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
                for (Record record : downReportList) {
                    row = sheet.createRow(index);
                    //来访人姓名
                    ExcelUtil.createCell(row,cellStyle,record.get("userName"),0);
                    //受访人
                    ExcelUtil.createCell(row,cellStyle,record.get("visitName"),1);
                    //受访人单位
                    ExcelUtil.createCell(row,cellStyle,record.get("visitDept"),2);
                    //车牌号
                    ExcelUtil.createCell(row,cellStyle,record.get("plate"),3);
                    //来访总人数
                    ExcelUtil.createCell(row,cellStyle,record.get("num").toString(),4);
                    //来访人员身份证
                    ExcelUtil.createCell(row,cellStyle,record.get("idNO"),5);
                    //审核人员
                    ExcelUtil.createCell(row,cellStyle,record.get("realName"),6);
                    //审核时间
                    ExcelUtil.createCell(row,cellStyle,record.get("replayDate"),7);
                    //审核结果
                    ExcelUtil.createCell(row,cellStyle,record.get("cStatus"),8);
                    index++;
                }
                String fileName = String.format("车辆大数据报表_%s.xls",systemTimeFourteen);
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

    public static void main(String[] args) throws IOException {
       new File("D:/java/hello");
    }
}
