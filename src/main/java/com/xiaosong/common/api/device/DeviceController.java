package com.xiaosong.common.api.device;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.model.VDevice;
import com.xiaosong.util.ConsantCode;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by CNL on 2020/9/7.
 */
public class DeviceController extends Controller {

    public void save() {
        String ip = get("ip");
        String deviceName = get("deviceName");
        String type = get("type");
        String gate = get("gate");
        String status = get("status");
        String avg = get("avg");
        Integer ping =(int)Float.parseFloat(avg);
        try {
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

            VDevice vDevice = VDevice.dao.findFirst("select * from v_device where ip =?", ip);
            boolean isUpdate = true;
            if (vDevice == null) {
                isUpdate = false;
                vDevice = new VDevice();
            }
            vDevice.setIp(ip);
            vDevice.setDeviceName(deviceName);
            vDevice.setType(type);
            vDevice.setGate(gate);
            vDevice.setStatus(status);
            vDevice.setPing(ping);
            boolean succ = isUpdate ? vDevice.update() : vDevice.save();
            if (succ) {
                WebSocketMonitor.me.getDeviceStatus();
                renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.SUCCESS, "同步设备信息成功")));
            }
        } catch (Exception ex) {
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常," + ex.getMessage())));
        }

    }
}
