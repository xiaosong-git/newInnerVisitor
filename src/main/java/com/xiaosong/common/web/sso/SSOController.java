package com.xiaosong.common.web.sso;

import com.gexin.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.common.web.login.LoginService;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VSysUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CNL on 2020/9/19.
 */
public class SSOController extends Controller {

    String homePage = PropKit.get("homePage");
    public SSOService srv = SSOService.me;
    public LoginService loginService = LoginService.me;
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










}
