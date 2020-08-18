package com.xiaosong.common.web.inOut;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.constant.ErrorCodeDef;
import com.xiaosong.util.RetUtil;
import com.xiaosong.util.XLSFileKit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InOutController extends Controller {

    private Log log = Log.getLog(InOutController.class);
    public InOutService srv = InOutService.me;

    public void findList() {
        String userName = getPara("userName");
        String userType = getPara("userType");
        String inOrOut = getPara("inOrOut");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        int currentPage = getInt("currentPage");
        int pageSize = getInt("pageSize");
        Page<Record> pagelist = srv.findList(userName,userType,inOrOut,startDate,endDate,currentPage,pageSize);
        renderJson(pagelist);
    }

    public void downReport() {
        String userName = getPara("userName");
        String userType = getPara("userType");
        String inOrOut = getPara("inOrOut");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        List<Record> pagelist = srv.downReport(userName,userType,inOrOut,startDate,endDate);

        // 导出`Excel`名称
        String fileName = "通行人员报表_" + getDate() + ".xls";

        // excel`保存路径
        String filePath = getRequest().getRealPath("/") + "/file/export/";
        System.out.println(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        filePath += fileName;
        XLSFileKit xlsFileKit = new XLSFileKit(filePath);
        List<List<Object>> content = new ArrayList<List<Object>>();
        List<String> title = new ArrayList<String>();
        // 添加`title`,对应的从数据库检索出`datas`的`title`
        title.add("序号");
        title.add("通行日期");
        title.add("通行时间");
        title.add("姓名");
        title.add("人员类型");
        title.add("设备类型");
        title.add("设备IP");
        title.add("进出类型");
        int i = 0;

        OK:
        while (true) {
            if (pagelist.size() < (i + 1)) {
                break OK;
            }
            // 判断单元格是否为空，不为空添加数据
            int index = i + 1;
            List<Object> row = new ArrayList<Object>();
            row.add(index + "");
            //row.add(null == tbStatements.get(i).getId() ? "" : tbStatements.get(i).getId());
            row.add(null == pagelist.get(i).get("scanDate") ? "" : pagelist.get(i).get("scanDate"));
            row.add(null == pagelist.get(i).get("scanTime") ? "" : pagelist.get(i).get("scanTime"));
            row.add(null == pagelist.get(i).get("userName") ? "" : pagelist.get(i).get("userName"));
            row.add("staff".equals(pagelist.get(i).get("userType")) ? "员工" : "访客");
            row.add("FACE".equals(pagelist.get(i).get("deviceType")) ? "人脸设备" : "二维码设备");
            row.add(null == pagelist.get(i).get("deviceIp") ? "" : pagelist.get(i).get("deviceIp"));
            row.add("in".equals(pagelist.get(i).get("turnOver")) ? "进" : "出");
            content.add(row);
            i++;
        }

        xlsFileKit.addSheet(content, "通行人员报表", title);
        boolean save = xlsFileKit.save();
        if (save) {
            log.info("报表导出成功~");
            renderJson(RetUtil.ok(ErrorCodeDef.CODE_NORMAL, "报表导出成功~"));
            File file1 = new File(getRequest().getRealPath("/") + "/file/export/" + "通行人员报表_" + getDate() + ".xls");
            renderFile(file1);
        } else {
            log.error("报表导出失败~");
            renderJson(RetUtil.fail(ErrorCodeDef.CODE_ERROR, "报表导出失败~"));
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
