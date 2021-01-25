package com.xiaosong.common.web.sso;

import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.AesUtils;
import org.apache.commons.lang3.StringUtils;

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
    public static String clientToken = null;



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
        if(StringUtils.isBlank(clientToken)) {
            clientToken = getToken(null, null, "client_credentials");
        }
        return clientToken;
    }

    /**
     * 刷新token
     */
    public String refreshToken() {
        clientToken = getToken(null, null, "client_credentials");
        return clientToken;
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
    public boolean userSync(String token ,String username,String password,String name,String phone,String idCard,String organCode)
    {
        url =paramService.findValueByName("ssoUrl");
        aesKey =paramService.findValueByName("ssoAesKey");
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("password", password);
        userMap.put("name", name);
        userMap.put("idCard", idCard);
        userMap.put("phone", phone);
        userMap.put("organCode", organCode);
        String userString = AesUtils.getAESEncrypt(JSON.toJSONString(userMap),aesKey);

        HashMap<String, String> map = new HashMap<>();
      //  map.put("accessToken", token);
        map.put("userString", userString);

        HashMap<String, String> head = new HashMap<>();
        head.put("accessToken", token);

        String result = HttpKit.post(url + "api/v1/userSync", map, null,head);

        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") == 200) {
            log.debug("同步用户数据成功");
            return true;
        }
        else
        {
            String msg = jsonResult.getString("msg");
            if("用户已存在".equals(msg))
            {
                log.debug("用户已存在");
                return true;
            }
            else if("accessToken错误".equals(msg) || "accessToken已过期".equals(msg)/*||"accessToken不能为空".equals(msg)*/)
            {
                token =  refreshToken();
                userSync( token , username, password, name, phone, idCard, organCode);
            }
            log.error("token:"+"同步用户数据失败："+msg);
            return false;
        }

    }



    /**
     * 同步部门数据
     * @param token
     * @param code
     * @param deptName
     * @return
     */
    public boolean deptSync(String token ,String code,String deptName)
    {
        url =paramService.findValueByName("ssoUrl");
        aesKey =paramService.findValueByName("ssoAesKey");
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("code", code);
        userMap.put("name", deptName);
        userMap.put("pCode", "0");
        String organString = AesUtils.getAESEncrypt(JSON.toJSONString(userMap),aesKey);

        HashMap<String, String> map = new HashMap<>();
        //  map.put("accessToken", token);
        map.put("organString", organString);

        HashMap<String, String> head = new HashMap<>();
        head.put("accessToken", token);

        String result = HttpKit.post(url + "api/v1/organASync", map, null,head);

        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") == 200) {
            log.debug("同步部门数据成功");
            return true;
        }
        else
        {
            String msg = jsonResult.getString("msg");
            if("组织机构已存在".equals(msg))
            {
                log.debug("组织机构已存在");
                return true;
            }
            else if("accessToken错误".equals(msg) || "accessToken已过期".equals(msg)/*||"accessToken不能为空".equals(msg)*/)
            {
                token =  refreshToken();
                deptSync( token , code, deptName);
            }
            log.error("token:"+"同步部门数据失败："+msg);
            return false;
        }



    }



    /**
     * 推送消息
     * @param token
     * @param type
     * @param registrationId
     * @param content
     * @return
     */
    public boolean push(String token ,String type,String registrationId,String content)
    {
        url =paramService.findValueByName("ssoUrl");
        aesKey =paramService.findValueByName("ssoAesKey");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("type", type);
        userMap.put("registrationId", registrationId);
        userMap.put("content", content);
        userMap.put("pushType", "alert");
        userMap.put("audience","one");
        if(!"android".equals(type)) {
            userMap.put("apns", true);
        }
        userMap.put("content", content);

        HashMap<String, String> map = new HashMap<>();
        //  map.put("accessToken", token);
        //map.put("pushPara", JSON.toJSONString(userMap));

        HashMap<String, String> head = new HashMap<>();
        head.put("accessToken", token);
        head.put("Content-Type", "application/json");

        String result = HttpKit.post(url + "api/v1/push", map, JSON.toJSONString(userMap),head);

        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") == 200) {
            log.debug("消息推送成功");
            return true;
        }
        else
        {
            String msg = jsonResult.getString("msg");
            if("accessToken错误".equals(msg) || "accessToken已过期".equals(msg)/*||"accessToken不能为空".equals(msg)*/)
            {
                token =  refreshToken();
                push( token , type, registrationId, content);
            }
            log.error("消息推送失败："+msg);
            return false;
        }


    }



    public static void main(String args[])
    {
        String token =  me.getToken();
        boolean result = me.push(token,"android","123456","测试测试测试");
        System.out.println(result);
    }

}
