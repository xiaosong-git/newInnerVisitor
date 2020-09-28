package com.xiaosong.common.web.sso;

import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.AesUtils;

import java.util.HashMap;

/**
 * Created by CNL on 2020/9/19.
 */
public class SSOService {

    public static final SSOService me = new SSOService();
    public static ParamService  paramService = new ParamService();
    private  String url ="http://111.75.240.74:56794/jx_offical_affair/";
    String aesKey ="W%d69mBbi$jRW^Qg";
    String client_id ="78b6cd20208f4c7899e24db3f0673d04";
    String client_secret="69595e41e4d345e0bc682f10d7f5ce97";
    private Log log = Log.getLog(SSOService.class);


    /**
     * 获取用户信息
     * @param token
     * @return
     */
    public String getUserInfoSync(String token) {

        url = paramService.findValueByName("ssoUrl");
        aesKey =paramService.findValueByName("ssoAesKey");

        String userInfo = null;
        HashMap<String, String> map = new HashMap<>();
        map.put("accessToken", token);
        String result = HttpKit.post(url + "api/v1/getUserInfoSync", map, null);

        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") == 200) {
            String data = jsonResult.getString("data");
            userInfo = AesUtils.getAESDecrypt(data, aesKey);
        }
        else
        {
            log.error("获取用户信息失败："+jsonResult.getString("msg"));
        }
        return userInfo;
    }


    /**
     * 获取token
     */
    public String getToken() {
        return getToken(null,null,"client_credentials");
    }

    /**
     * 获取token
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public String getToken(String username,String password) {
        return getToken(username,password,"password");
    }

    /**
     * 获取token
     * @param username 用户名
     * @param password 密码
     * @param grant_type  1.password、2.client_credentials
     * @return
     */
    public String getToken(String username,String password,String grant_type) {

        url =paramService.findValueByName("ssoUrl");
        client_id =paramService.findValueByName("ssoClientId");
        client_secret=paramService.findValueByName("ssoClientSecret");

        String access_token = null;
        HashMap<String, String> map = new HashMap<>();
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        map.put("grant_type", grant_type);
        map.put("username", username);
        map.put("password", password);
        String result = HttpKit.post(url + "api/oauth/token", map, null);

        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") == 200) {
            JSONObject jsonData = jsonResult.getJSONObject("data");
            access_token = jsonData.getString("access_token");
        }
        else
        {
            log.error("获取token："+jsonResult.getString("msg"));
        }
        return access_token;
    }


    /**
     * 同步用户数据
     * @param token
     * @param username
     * @param password
     * @param name
     * @param phone
     * @param organCode
     * @return
     */
    public boolean userSync(String token ,String username,String password,String name,String phone,String organCode)
    {
        url =paramService.findValueByName("ssoUrl");
        aesKey =paramService.findValueByName("ssoAesKey");
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("password", password);
        userMap.put("name", name);
        userMap.put("phone", phone);
        userMap.put("organCode", organCode);
        String userString = AesUtils.getAESEncrypt(JSON.toJSONString(userMap),aesKey);

        HashMap<String, String> map = new HashMap<>();
        map.put("accessToken", token);
        map.put("userString", userString);

        String result = HttpKit.post(url + "api/v1/userSync", map, null);

        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") == 200) {
            return true;
        }
        else
        {
            log.error("同步用户数据失败："+jsonResult.getString("msg"));
            return false;
        }

    }


    public static void main(String args[])
    {
        String token =  me.getToken();
        boolean result = me.userSync(token,"chennl","123456","陈","15000000000",null);
    }

}
