package com.xiaosong.common.api.device;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.code.CodeService;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.Params;
import com.xiaosong.model.TbFailreceive;
import com.xiaosong.model.VDevice;
import com.xiaosong.util.ConsantCode;
import com.xiaosong.util.YunPainSmsUtil;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CNL on 2020/9/7.
 */
public class DeviceController extends Controller {


    private static Long lastSendMsgTime = System.currentTimeMillis();
    private static String lastErrorDevices = null;

    public void save() {
        String jsonStr = HttpKit.readData(getRequest());
        System.out.println("接收的JSON参数：" + jsonStr);
        try {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            if (jsonArray == null) {
                throw new Exception("获取不到设备信息");
            }

            StringBuilder errorDevices = new StringBuilder();

            String gateName = "";
            for (int i = 0; i < jsonArray.size(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String ip = jsonObject.getString("ip");
                String deviceName = jsonObject.getString("deviceName");
                String type = jsonObject.getString("type");
                String gate = jsonObject.getString("gate");
                String status = jsonObject.getString("status");
                String avg = jsonObject.getString("avg");
                String extra1 = jsonObject.getString("extra1");
                String addr = jsonObject.getString("addr");
                Integer ping = (int) Float.parseFloat(avg);
                if (StringUtils.isBlank(ip)) {
                    throw new Exception("IP地址不能为空");
                }
                if (StringUtils.isBlank(deviceName)) {
                    throw new Exception("设备名称不能为空");
                }
                if (StringUtils.isBlank(type)) {
                    throw new Exception("设备类型不能为空");
                }
                if (StringUtils.isBlank(status)) {
                    throw new Exception("设备状态不能为空");
                }
                //删除上位机的所有数据
                if(i==0)
                {
                    Db.delete("delete from v_device where gate = ?",gate );
                    Record record = Db.findFirst("select * from v_org where org_code = ? ",gate);
                    if(record!=null)
                    {
                        gateName = record.getStr("org_name");
                    }
                }

                VDevice vDevice =new VDevice();
                vDevice.setIp(ip);
                vDevice.setDeviceName(deviceName);
                vDevice.setType(type);
                vDevice.setGate(gate);
                vDevice.setStatus(status);
                vDevice.setPing(ping);
                vDevice.setExtra1(extra1);
                vDevice.setExtra2(addr);
                boolean succ = vDevice.save();
                if (succ) {
                    if("error".equals(status))
                    {
                        errorDevices.append(addr);
                        if("FACE".equals(type))
                        {
                            errorDevices.append("人脸设备");
                        }
                        else if("QRCODE".equals(type))
                        {
                            errorDevices.append("二维码设备");
                        }
                        errorDevices.append(addr);
                        errorDevices.append(ip);
                        errorDevices.append("，");
                    }
                    WebSocketMonitor.me.getDeviceStatus();
                    renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.SUCCESS, "同步设备信息成功")));
                }
            }
            String strErrorDevices = errorDevices.toString();
            if(StringUtils.isNotBlank(strErrorDevices) && !strErrorDevices.equals(lastErrorDevices) && System.currentTimeMillis()-lastSendMsgTime>=1*60*60*1000)
            {
                lastErrorDevices = strErrorDevices;
                String mobile =  Params.getMaintenancePhone();
                String [] mobiles = mobile.split(",");
                for(String phone : mobiles) {
                    YunPainSmsUtil.sendSmsErrorDevices(phone, gateName+strErrorDevices);
                }
                lastSendMsgTime = System.currentTimeMillis();
            }

        } catch (Exception ex) {
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常," + ex.getMessage())));
        }
    }





    public void saveFailreceive() {
        String jsonStr = HttpKit.readData(getRequest());
        System.out.println("接收的JSON参数：" + jsonStr);
        try {
            TbFailreceive tbFailreceive = JSON.parseObject(jsonStr,TbFailreceive.class);
            if (tbFailreceive == null) {
                throw new Exception("获取不到失败记录");
            }
            tbFailreceive.save();
        } catch (Exception ex) {
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常," + ex.getMessage())));
        }
    }


}
