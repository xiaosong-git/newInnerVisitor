package com.xiaosong.common.web.inOut;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.bean.InOutBean;
import com.xiaosong.bean.VisitorsBean;
import com.xiaosong.common.web.dept.DeptService;
import com.xiaosong.common.web.device.DeviceService;
import com.xiaosong.constant.Constant;
import com.xiaosong.constant.ErrorCodeDef;
import com.xiaosong.model.VDevice;
import com.xiaosong.util.DESUtil;
import com.xiaosong.util.DateUtil;
import com.xiaosong.util.ExcelUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InOutController extends Controller {

    private Log log = Log.getLog(InOutController.class);
    public InOutService srv = InOutService.me;

    public void findList() {
        try {
            String userName = getPara("userName");
            String userType = getPara("userType");
            String startDate = getPara("startDate");
            String endDate = getPara("endDate");
            String deptName = getPara("deptName");
            String inOrOut = getPara("inOrOut");
            int currentPage = getInt("currentPage");
            int pageSize = getInt("pageSize");
            Page<Record> pagelist = srv.findList(userName,userType,deptName,startDate,endDate,inOrOut,currentPage,pageSize);

            Record user_key = Db.findFirst("select * from v_user_key");

            for (Record record : pagelist.getList()) {
                record.set("idNO",DESUtil.decode(user_key.getStr("workKey"),record.getStr("idNO")));
            }

            renderJson(RetUtil.okData(pagelist));
        } catch (Exception e) {
            log.error("错误信息：", e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }



    public void downReport() {

        OutputStream os = null;
        try {
            String userName = getPara("userName");
            String userType = getPara("userType");
            String startDate = getPara("startDate");
            String endDate = getPara("endDate");
            String deptName = getPara("deptName");
            String inOrOut = getPara("inOrOut");
            //获取列表
            List<Record> downReportList = srv.downReport(userName,userType,deptName,startDate,endDate,inOrOut);

            if (downReportList != null && downReportList.size() > 0){
                Record user_key = Db.findFirst("select * from v_user_key");
                for (Record record : downReportList) {
                    record.set("idNO", DESUtil.decode(user_key.getStr("workKey"), record.getStr("idNO")));
                }
                String systemTimeFourteen = DateUtil.getSystemTimeFourteen();
                String[] fields = {"日期","时间","姓名","人员类型","身份证号","所在单位","进出类型","通行方式"};
                List<String> fieldsList = Arrays.asList(fields);

                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("人员通行大数据");

                //设置单元格行高，列宽
                sheet.setDefaultRowHeightInPoints(18);
                sheet.setDefaultColumnWidth(20);

                //标题
                HSSFRow rowTitle = sheet.createRow(0);
                HSSFCell cell = rowTitle.createCell(0);
                cell.setCellValue("人员通行大数据");
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
                    //日期
                    ExcelUtil.createCell(row,cellStyle,record.get("scanDate"),0);
                    //时间
                    ExcelUtil.createCell(row,cellStyle,record.get("scanTime"),1);
                    //姓名
                    ExcelUtil.createCell(row,cellStyle,record.get("userName"),2);
                    //人员类型
                    ExcelUtil.createCell(row,cellStyle,record.get("userType").toString(),3);
                    //身份证号
                    ExcelUtil.createCell(row,cellStyle,record.get("idNO"),4);
                    //所在单位
                    ExcelUtil.createCell(row,cellStyle,record.get("deptName"),5);
                    //进出类型
                    ExcelUtil.createCell(row,cellStyle,record.get("inOrOut"),6);
                    //通行方式
                    ExcelUtil.createCell(row,cellStyle,record.get("deviceType"),7);
                    index++;
                }
                String fileName = String.format("人员通行大数据报表_%s.xls",systemTimeFourteen);
                String fileNameUrl = Constant.BASE_DOWNLOAD_PATH;
//                String fileNameUrl = "E:/newInnerVisitor/download/temp";
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


    /**
     * 获取当前系统时间 年-月-日
     *
     * @return
     */
    private String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date()); // new Date()为获取当前系统时间
        return date;
    }
}
