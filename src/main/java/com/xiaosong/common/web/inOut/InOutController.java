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
import com.xiaosong.util.ExcelUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
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
        List list = pagelist.getList();
        List<Record> depts = DeptService.me.findDeptList();
        List<VDevice> devices =DeviceService.me.findAll();
        Record user_key = Db.findFirst("select * from v_user_key");

        for(Object obj : list)
        {
            Record record = (Record) obj;
            String deptId  = record.getStr("deptId");
            String deviceIp = record.getStr("deviceIp");
            String idCard = record.getStr("idCard");

            if(StringUtils.isNotBlank(deptId))
            {
                for(Record dept : depts) {
                    if (deptId.equals(dept.getStr("dept_id")))
                    {
                        record.set("deptName",dept.getStr("dept_name"));
                        break;
                    }
                }
            }

            if(StringUtils.isNotBlank(deviceIp))
            {
                for(VDevice vDevice : devices) {
                    if (deviceIp.equals(vDevice.getExtra2()))
                    {
                        record.set("addr",vDevice.getExtra2());
                        break;
                    }
                }
            }

            String idNo = DESUtil.decode(user_key.getStr("workKey"), idCard);
            record.set("idCard",idNo);
        }
        renderJson(pagelist);
    }



    public void downReport() {

        String userName = getPara("userName");
        String userType = getPara("userType");
        String inOrOut = getPara("inOrOut");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        List<Record> recordList = srv.downReport(userName,userType,inOrOut,startDate,endDate);

        List outputList = new ArrayList<>();
        if (recordList != null && recordList.size() > 0) {
            // 生成文件并返回
            for (int i = 0; i < recordList.size(); i++) {
                Record record = recordList.get(i);
                InOutBean sd = new InOutBean();
                sd.setId((long)i+1);
                sd.setDeviceIp(record.getStr("deviceIp"));
                sd.setScanDate(record.getStr("scanDate"));
                sd.setScanTime(record.getStr("scanTime"));
                sd.setUserName(record.getStr("userName"));
                sd.setUserType("staff".equals(record.getStr("userType")) ? "员工" : "访客");
                sd.setDeviceType("FACE".equals(record.getStr("deviceType"))? "人脸设备" : "二维码设备");
                sd.setInOrOut("in".equals(record.getStr("inOrOut")) ? "进" : "出");
                outputList.add(sd);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        String exportName = date + "_通行人员报表.xls";
        String exportPath = Constant.BASE_DOWNLOAD_PATH;
        File exportFile = new File(exportPath + "/" + exportName);
        if(exportFile.exists()){
            exportFile.delete();
            try {
                exportFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        String[] title = {  "序号", "通行日期", "通行时间","姓名","人员类型","设备类型","设备IP","进出类型"};
        byte[] data = ExcelUtil.export("通行人员报表", title, outputList);
        try {
            FileUtils.writeByteArrayToFile(exportFile, data, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        renderFile(exportFile);
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
