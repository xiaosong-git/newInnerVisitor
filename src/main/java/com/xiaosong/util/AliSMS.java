package com.xiaosong.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import com.google.gson.Gson;
import com.xiaosong.common.api.code.CodeMsg;

/**
 * @program: innerVisitor
 * @description: 阿里短信接口
 * @author: cwf
 * @create: 2021-03-29 10:34
 **/

public final class AliSMS {


    private static class Holder {
        private static AliSMS instance = new AliSMS();
    }

    public static AliSMS getInstance() {
        return Holder.instance;
    }

    private Client client;

    private void setClient() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId("LTAI5tHgB9qkb84UKcJPVUgf")
                // 您的AccessKey Secret
                .setAccessKeySecret("jaxsZYcus9uhfOcD9g5U3OB729M88v");
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";

        client = new Client(config);
    }
    private Client getClient() throws Exception {
        if (client == null) {
            setClient();
        }
        return client;
    }

    public static String sendMsg(String content,String mobile) throws Exception {

        String templateCode = CodeMsg.MAP.get(content);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName("朋悦比邻")
                .setTemplateCode(templateCode);
        AliSMS instance = AliSMS.getInstance();

        Client client = instance.getClient();
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
        String code = sendSmsResponse.body.code;
        System.out.println(new Gson().toJson(sendSmsResponse));
        if ("ok".equals(code)){
            return "0000";
        }  else{

            return "9999";
        }
    }

    public static void main(String[] args_) throws Exception {
        sendMsg(" 您有一条人员访客通行申请待审核，请及时处理！","18150797748");
//        java.util.List<String> args = java.util.Arrays.asList(args_);
//        AliSMS instance = AliSMS.getInstance();
//
//        Client client = instance.getClient();
//
//        SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                .setPhoneNumbers("18150797748")
//                .setSignName("123")
//                .setTemplateCode("123")
//                .setTemplateParam("{\"code\":\"1234\"}");
//        // 复制代码运行请自行打印 API 的返回值
//        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
//        String code = sendSmsResponse.body.code;
//        System.out.println(code);
//        System.out.println(new Gson().toJson(sendSmsResponse));
    }


}