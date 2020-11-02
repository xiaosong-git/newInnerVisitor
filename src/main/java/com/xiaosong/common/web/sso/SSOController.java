package com.xiaosong.common.web.sso;

import com.gexin.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.MainConfig;
import com.xiaosong.common.visitDevice.CommonResult;
import com.xiaosong.common.visitDevice.NonceData;
import com.xiaosong.common.web.login.LoginService;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VSysUser;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.AuthUtil;
import com.xiaosong.util.SignUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CNL on 2020/9/19.
 */
public class SSOController extends Controller {

    String homePage = PropKit.get("homePage");
    public SSOService srv = SSOService.me;
    public LoginService loginService = LoginService.me;
    public static ParamService paramService = new ParamService();

    NonceData nonceData = NonceData.getInstance();
    /**
     * 单点登录入口
     */
    public void login() {
        String token = getPara("token");
        Map<String, Object> map = new HashMap<String, Object>();
        System.out.println(token);
        //根据token获取用信息
        String userInfo =srv.getUserInfoSync(token);
        JSONObject userJSON = JSONObject.parseObject(userInfo);
        if (userJSON != null) {
            String username = userJSON.getString("username");
            VSysUser user = loginService.getSysUserByUserName(username);
            user.setToken(token);
            user.update();
            map.put("token", token);
            map.put("result", "success");
            map.put("username", username);
            map.put("userRole", user.getRoleId());
            map.put("userId", user.getId().toString());
            map.put("resultMSG", "登录成功");
            CacheKit.put(Constant.SYS_ACCOUNT, user.getId().toString(), user);
            renderJson(map);
        } else {
            map.put("resultMSG", "登录失败，无效的token");
            renderJson(map);
        }
    }




    /**
     * 推送记录入口
     */
    public void pushInfo() {

        String data = HttpKit.readData(getRequest());
        if(!isValued(data)){
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String appType = jsonObject.getString("app_type");
        String registrationId = jsonObject.getString("registration_id");
        String phone = jsonObject.getString("phone");
        CommonResult result;
        if(StringUtils.isBlank(appType) || StringUtils.isBlank(registrationId) ||StringUtils.isBlank(phone)){
            result = new CommonResult(3,"参数不合法");
            renderJson(result);
            return;
        }

        SqlPara para = Db.getSqlPara("deptUser.findByPhone", phone);//根据手机查找用户
        VDeptUser user = VDeptUser.dao.findFirst(para);
        if(user == null)
        {
            result = new CommonResult(4,"无相关记录");
            renderJson(result);
            return ;
        }

        user.setAppType(appType);
        user.setRegistrationId(registrationId);
        boolean r = user.update();
        if(r)
        {
            result = new CommonResult(1,"操作成功");
            renderJson(result);
        }
        else
        {
            result = new CommonResult(99,"操作失败");
            renderJson(result);
        }



    }



    /**
     *   签名核验
     *
     *  timestamp    时间戳（秒）
     *  nonce     随机数
     *  sign     发送过来的签名
     *  mySign    我解密的签名
     * @return
     */
    private boolean isValued(String data){
        System.out.println("data:"+data);
        String sign = getRequest().getHeader("sign");
        String nonce = getRequest().getHeader("nonce");
        if(sign == null ||nonce ==null ||getRequest().getHeader("timestamp") ==null){
            renderJson(new CommonResult(1,"参数不完整"));
            return false;
        }
        long timestamp = Long.valueOf(getRequest().getHeader("timestamp"));
        sign = sign.toUpperCase();
        String key =paramService.findValueByName("ssoAesKey");
        String mySign = SignUtils.getSign(timestamp,key,nonce,data);
        if(!mySign.equals(sign)){
            System.out.println("签名:"+mySign+"  -------" + sign);
            renderJson(new CommonResult(4,"请求不合法"));
            return false;
        }
        for(String str: nonceData.nonceList){
            if(nonce.equals(str)){
                renderJson(new CommonResult(7,"请求重复"));
                return false;
            }
        }
        nonceData.nonceList.add(nonce);
        long dif = System.currentTimeMillis()/1000 - timestamp;
        long difminutes = dif/60;
        if( difminutes > 5 ){
            renderJson(new CommonResult(6,"请求已过期"));
            return false;
        }
        return true;
    }





}
