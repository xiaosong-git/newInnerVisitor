package com.xiaosong.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

public class YunPainSmsUtil {

	//查账户信息的http地址
    private static String URI_GET_USER_INFO = "https://203.107.55.55/v2/user/get.json";

    //智能匹配模板发送接口的http地址
    private static String URI_SEND_SMS = "https://203.107.55.55/v2/sms/single_send.json";

    //模板发送接口的http地址
    private static String URI_TPL_SEND_SMS = "https://203.107.55.55/v2/sms/tpl_single_send.json";

    //发送语音验证码接口的http地址
    private static String URI_SEND_VOICE = "https://voice.yunpian.com/v2/voice/send.json";

    //绑定主叫、被叫关系的接口http地址
    private static String URI_SEND_BIND = "https://call.yunpian.com/v2/call/bind.json";

    //解绑主叫、被叫关系的接口http地址
    private static String URI_SEND_UNBIND = "https://call.yunpian.com/v2/call/unbind.json";

    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";

    public static Integer MSG_TYPE_WELCOME = 1;//其他

    public static String CHECK_CODE_WELCOME = "欢迎使用，您的手机验证码是code。本条信息无需回复";

    public static Integer MSG_TYPE_LIMIT = 2;//输入密码多次错误

    public static String CHECK_CODE_LIMIT = "尊敬的用户，由于date您账号为phone连续limit次错误输入登录密码，请确认是否为本人操作。";

    public static Integer MSG_TYPE_VISITORBY = 3;//访客

    public static String CHECK_CODE_VISITORBY = "您好，您有一条预约访客申请已审核，审核结果：visitorResult，被访者:visitorBy,访问时间:visitorDateTime";

    public static Integer MSG_TYPE_VISITOR = 4;//审核

    public static String CHECK_CODE_VISITOR = "您好，您有一条预约访客需审核，访问者:visitor1，被访者:visitorBy,访问时间:visitorDateTime";

    public static Integer MSG_TYPE_VERIFY = 5;//被访者

    public static String CHECK_CODE_VERIFY = "您好，您有一条预约访客需审核，访问者:visitor1,访问时间:visitorDateTime，请尽快到江西机关事务APP完成审核";

    public static Integer MSG_TYPE_INVITE = 6;//邀请

    public static String CHECK_CODE_INVITE = "visitorBy，您好，accName请您于visitorDateTime —— endDateTime到:companyAddr进行访谈\n";

    public static Integer MSG_TYPE_VISITORBY_QRCODE = 7;//访客

    public static String CHECK_CODE_VISITORBY_QRCODE = "您好，您有一条访客预约申请已审核，被访者:#visitorBy#,访问时间:#visitorDateTime#，审核结果：#visitorResult#，请您在访问时间内进出";


    public static Integer MSG_TYPE_ENTOURAGE_NOAUTH = 8;//随行人员

    public static String CHECK_CODE_ENTOURAGE_NOAUTH  = "您好，您有一条访客随行预约记录，访问者:visitor1,访问时间:visitorDateTime,请尽快到江西机关事务APP，完成实人认证";


    public static Integer MSG_TYPE_ENTOURAGE_AUTH =9;//随行人员

    public static String CHECK_CODE_ENTOURAGE_AUTH  = "您好，您有一条访客随行预约记录，访问者:visitor1,访问时间:visitorDateTime";


    public static Integer MSG_DEVICE_ERROR =10;//随行人员

    public static String CHECK_CODE_DEVICE_ERROR  = "以下设备#errorDevices#有异常，请及时查看";




    private final static String APIKEY = "a8c29253d3e40dfa59b0f677bdd3f6fd";

    /**
     * 取账户信息
     *
     * @return json格式字符串
     * @throws IOException
     */
     public static String getUserInfo(String apikey) throws IOException, URISyntaxException {
         Map<String, String> params = new HashMap<String, String>();
         params.put("apikey", apikey);
         return post(URI_GET_USER_INFO, params);
     }


    /**
     * 发送短信验证码(注册、修改密码、找回密码)
     * @param checkCode   　验证码
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */
    public static String sendSmsCode(String checkCode, String mobile, Integer type, String date, String limit, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String content = getSmsContent(checkCode,mobile,type,date,limit,visitorResult,visitorBy,visitorDateTime,visitor);
        return sendMsg(content,mobile);
    }


    /**
     * 发送短信验证码(注册、修改密码、找回密码)
     * @param checkCode   　验证码
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */
    public static String getSmsContent(String checkCode, String mobile, Integer type, String date, String limit, String visitorResult, String visitorBy, String visitorDateTime, String visitor) {
        String msg = "";
        String content = "";
        //其他
        if(MSG_TYPE_WELCOME == type){
            msg = CHECK_CODE_WELCOME;
            content= msg.replace("code", checkCode);
        }else if(MSG_TYPE_LIMIT == type){
            //输入密码多次错误
            msg = CHECK_CODE_LIMIT;
            msg= msg.replace("date", date);
            msg= msg.replace("phone", mobile);
            content= msg.replace("limit", limit);
        }else if(MSG_TYPE_VISITORBY == type){
            //访客
            msg = CHECK_CODE_VISITORBY;
            msg= msg.replace("visitorResult", visitorResult);
            msg= msg.replace("visitorBy", visitorBy);
            content= msg.replace("visitorDateTime", visitorDateTime);
        }else if(MSG_TYPE_VISITOR == type){
            //审核
            msg = CHECK_CODE_VISITOR;
            msg= msg.replace("visitor1", visitor);
            msg= msg.replace("visitorBy", visitorBy);
            content= msg.replace("visitorDateTime", visitorDateTime);
        }else if(MSG_TYPE_VERIFY == type){
            //被访者
            msg = CHECK_CODE_VERIFY;
            msg= msg.replace("visitor1", visitor);
            content= msg.replace("visitorDateTime", visitorDateTime);
        }else if(MSG_TYPE_INVITE==type){
//            "visitorBy，您好，companyName的accName邀请您于visitorDateTime —— endDateTime到:companyAddr进行访谈，同意请点击：#url\n";

            msg = CHECK_CODE_INVITE;
            msg= msg.replace("accName", visitor);
            msg=msg.replace("visitorBy",visitorBy);
            msg=msg.replace("companyName",limit);
            msg=msg.replace("companyAddr",date);
            msg=msg.replace("endDateTime",visitorResult);
            msg=msg.replace("url",checkCode);
            content= msg.replace("visitorDateTime", visitorDateTime);
        }else if(MSG_TYPE_VISITORBY_QRCODE==type){
            msg=CHECK_CODE_VISITORBY_QRCODE;
            msg= msg.replace("#visitorResult#", visitorResult);
            msg= msg.replace("#visitorBy#", visitorBy);
            content= msg.replace("#visitorDateTime#", visitorDateTime);
        }
        else if(MSG_TYPE_ENTOURAGE_NOAUTH == type)
        {
            msg = CHECK_CODE_ENTOURAGE_NOAUTH;
            msg= msg.replace("visitor1", visitor);
            content= msg.replace("visitorDateTime", visitorDateTime);
        }
        else if(MSG_TYPE_ENTOURAGE_AUTH == type)
        {
            msg = CHECK_CODE_ENTOURAGE_AUTH;
            msg= msg.replace("visitor1", visitor);
            content= msg.replace("visitorDateTime", visitorDateTime);
        }

        return content;
    }




    /**
     * 智能匹配模板接口发短信
     *
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

     public static String sendSms(String text, String mobile) throws IOException {
         Map<String, String> params = new HashMap<String, String>();
         params.put("apikey", APIKEY);
         params.put("text", text);
         params.put("mobile", mobile);
         return post(URI_SEND_SMS, params);
     }

     /**
     * 通过模板发送短信(不推荐)
     *
     * @param apikey    apikey
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

     public static String tplSendSms(String apikey, long tpl_id, String tpl_value, String mobile) throws IOException {
         Map<String, String> params = new HashMap<String, String>();
         params.put("apikey", apikey);
         params.put("tpl_id", String.valueOf(tpl_id));
         params.put("tpl_value", tpl_value);
         params.put("mobile", mobile);
         return post(URI_TPL_SEND_SMS, params);
     }

     /**
     * 通过接口发送语音验证码
     * @param apikey apikey
     * @param mobile 接收的手机号
     * @param code   验证码
     * @return
     */

     public static String sendVoice(String apikey, String mobile, String code) {
         Map<String, String> params = new HashMap<String, String>();
         params.put("apikey", apikey);
         params.put("mobile", mobile);
         params.put("code", code);
         return post(URI_SEND_VOICE, params);
     }

     /**
     * 通过接口绑定主被叫号码
     * @param apikey apikey
     * @param from 主叫
     * @param to   被叫
     * @param duration 有效时长，单位：秒
     * @return
     */

     public static String bindCall(String apikey, String from, String to , Integer duration ) {
         Map<String, String> params = new HashMap<String, String>();
         params.put("apikey", apikey);
         params.put("from", from);
         params.put("to", to);
         params.put("duration", String.valueOf(duration));
         return post(URI_SEND_BIND, params);
     }

     /**
     * 通过接口解绑绑定主被叫号码
     * @param apikey apikey
     * @param from 主叫
     * @param to   被叫
     * @return
     */
     public static String unbindCall(String apikey, String from, String to) {
         Map<String, String> params = new HashMap<String, String>();
         params.put("apikey", apikey);
         params.put("from", from);
         params.put("to", to);
         return post(URI_SEND_UNBIND, params);
     }

     /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

     public static String post(String url, Map<String, String> paramsMap) {
         //CloseableHttpClient client = HttpClients.createDefault();
         String responseText = "";
         CloseableHttpResponse response = null;
             try {
                 HttpPost method = new HttpPost(url);
                 if (paramsMap != null) {
                     List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                     for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                         NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                         paramList.add(pair);
                     }
                     method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
                 }
                 response = client.execute(method);
                 HttpEntity entity = response.getEntity();
                 if (entity != null) {
                     responseText = EntityUtils.toString(entity, ENCODING);
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             } finally {
                 try {
                     response.close();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
             return responseText;
      }


    private static CloseableHttpClient client;
    static {
        try {
            SSLContext sslContext = SSLContextBuilder.create().useProtocol(SSLConnectionSocketFactory.SSL).loadTrustMaterial((x, y) -> true).build();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
            client = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLContext(sslContext).setSSLHostnameVerifier((x, y) -> true).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void test()
    {
        sendSmsCode("123456","15005089512",1,null,null,null,null,null,null);

    }


    public static String sendSmsErrorDevices(String mobile,String errorDevices) {
        String content = CHECK_CODE_DEVICE_ERROR.replace("#errorDevices#",errorDevices);
        return sendMsg(content,mobile);
    }


    public static String sendMsg(String content,String mobile)
    {
        System.out.println("发送短信的内容： "+content);
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", APIKEY);
        params.put("text", content);
        System.out.println("content:"+content);
        params.put("mobile", mobile);
        String  backJson =  post(URI_SEND_SMS, params);
        JSONObject jsonObject = JSON.parseObject(backJson);
        System.out.println("jsonObject:"+jsonObject);
        if(jsonObject!=null) {
            if (jsonObject.getString("code").equals("0")) {
                return "0000";
            }
            return jsonObject.getString("msg");
        }
        else
        {
            return "9999";
        }
    }


    public static void main(String[] args) throws Exception{


        sendSmsCode("123456","15005089512",1,null,null,null,null,null,null);

       //  String sid =Base64.encode((String.valueOf(150)).getBytes("UTF-8"));
//         String sendMsg =	YunPainSmsUtil.sendSmsCode(Constant.URL+sid,"18150797748",YunPainSmsUtil.MSG_TYPE_VISITORBY_QRCODE,null,null,
//                 "审核已通过","fafa","2019-09-20 10:19",null);
//         sendSmsCode("c", "18150797748", 6, DateUtil.getCurDate(), "4", "hoho", "fafa",DateUtil.getCurTime(), "guigui");
//         sendSmsCode("c", "18150797748", 2, "1", "4", null, null,DateUtil.getCurTime(), null);

//         Map<String, String> params = new HashMap<String, String>();
//         params.put("apikey", "3f053c1807c71729fbc4574b7eaef5e0");
//         String CHECK_CODE_VERIFY = "您好，您有一条预约访客需审核，访问者:visitor1,访问时间:visitorDateTime";
//         String msg = CHECK_CODE_VERIFY;
//         msg= msg.replace("visitor1", "123456");
//         String content= msg.replace("visitorDateTime", "2019-11-12");
//         params.put("text", content);
//         System.out.println("content:"+content);
//         params.put("mobile", "18150797748");
//         String URI_SEND_SMS = "https://203.107.55.55/v2/sms/single_send.json";
//         String  backJson =  post(URI_SEND_SMS, params);
//         JSONObject jsonObject = JSON.parseObject(backJson);
//         System.out.println("jsonObject:"+jsonObject);
//         if (jsonObject.getString("code").equals("0")){
//
//         }
     }
}
