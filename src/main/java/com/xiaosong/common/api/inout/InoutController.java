package com.xiaosong.common.api.inout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.xiaosong.common.api.websocket.WebSocketMonitor;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VDInout;
import com.xiaosong.model.VDevice;
import com.xiaosong.util.ConsantCode;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by CNL on 2020/9/7.
 */
public class InoutController extends Controller {

    public void save(){

        String jsonStr = HttpKit.readData(getRequest());
        System.out.println("接收的JSON参数：" + jsonStr);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String deviceIp = jsonObject.getString("deviceIp");  //设备IP
        String idCard = jsonObject.getString("idCard"); //卡号
        String deviceType  = jsonObject.getString("deviceType"); //设备类型
        String inOrOut  = jsonObject.getString("inOrOut");  //进出  in 或者 out
        String orgCode  = jsonObject.getString("orgCode");
        String scanDate  = jsonObject.getString("scanDate"); //通行日期
        String scanTime  = jsonObject.getString("scanTime"); //通行时间
        String userName  = jsonObject.getString("userName"); //姓名
        String userType  = jsonObject.getString("userType"); //人员类型

        if(StringUtils.isBlank(userName)||StringUtils.isBlank(userType)||StringUtils.isBlank(deviceIp)  || StringUtils.isBlank(deviceType)|| StringUtils.isBlank(scanDate)|| StringUtils.isBlank(scanTime))
        {
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常,参数不能为空")));
            return;
        }
        VDInout vdInout = new VDInout();
        vdInout.setDeviceIp(deviceIp);
        vdInout.setDeviceType(deviceType);
        vdInout.setIdCard(idCard);
        vdInout.setInOrOut(inOrOut);
        vdInout.setOrgCode(orgCode);
        vdInout.setScanDate(scanDate);
        vdInout.setScanTime(scanTime);
        vdInout.setUserName(userName);
        vdInout.setUserType(userType);
        boolean succ = vdInout.save();
        if(succ)
        {
            WebSocketMonitor.me.getPassData();
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.SUCCESS, "同步进出记录成功")));
        }
        else
        {
            renderText(JSON.toJSONString(Result.unDataResult(ConsantCode.FAIL, "系统异常")));
        }
    }




}
