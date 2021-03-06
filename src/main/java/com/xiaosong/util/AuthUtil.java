package com.xiaosong.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Params;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthUtil {



    public static void main(String[] args) throws Exception {
      //  fk("140107198411203046","陶鸥");
        //JSONObject result2 =  ys3("32042119760310271X","杨军","1375219980110032054");

     //   JSONArray jsonArray  = result.getJSONArray("data");

        //System.out.print(result.toJSONString());
        JSONObject result2 = fk2("500382198510163173","祖映兵");
        System.out.print(result2.toJSONString());


//        JSONObject result2 = yhk("350128198901124018","陈乃亮","6226631704274118","15005089512");
//        System.out.print(result2.toJSONString());
//
//       String idcard = "F39A27843EB5426502F323BD0F85D51BB878422DD6D94DE6";
//        String idNo = DESUtil.decode("iB4drRzSrC", idcard);
//        System.out.print(idNo);
    }

    public static JSONObject auth(String idNO, String realName, String idHandleImgUrl) throws Exception {

        //是否开启实人验证
        if("T".equals(Params.getStopAuthVerify()))
        {
            JSONObject result = new JSONObject();
            result.put("return_code","00000");
            return result;
        }
        return authResult( idNO, realName, idHandleImgUrl);
    }
    public static JSONObject authResult(String idNO, String realName, String idHandleImgUrl) throws Exception {

        String string= String.valueOf(System.currentTimeMillis())+new Random().nextInt(10);
        JSONObject itemJSONObj =new JSONObject();
        itemJSONObj.put("custid", "1000000008");//账号
        itemJSONObj.put("txcode", "tx00010");//交易码
        itemJSONObj.put("productcode", "000010");//业务编码
        itemJSONObj.put("serialno", string);//流水号
        itemJSONObj.put("mac", createSign(string));//随机状态码   --验证签名  商户号+订单号+时间+产品编码+秘钥
        String key="2B207D1341706A7R4160724854065153";
        String userName =DESUtil.encode(key,realName);
        String certNo = DESUtil.encode(key,idNO);
        itemJSONObj.put("userName", userName);
        itemJSONObj.put("certNo", certNo);
        itemJSONObj.put("imgData", idHandleImgUrl );
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body1 = RequestBody.create(mediaType, JSON.toJSONString(itemJSONObj));
        Request request = new Request.Builder()
                .url("http://47.99.129.98:8082/wisdom/entrance/pub")
                .method("POST", body1)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = null;
        JSONObject returnObject = new JSONObject();
        try {
            response = client.newCall(request).execute();
            string = response.body().string();
            // 解密响应数据
            returnObject= JSONObject.parseObject(string);
            return returnObject;
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.put("msg","系统错误");
            returnObject.put("code","-1");
            return returnObject;
        }
    }
    public static JSONObject phoneResult(String idNO, String realName, String idHandleImgUrl) throws Exception{
        String merchOrderId = OrderNoUtil.genOrderNo("V", 16);//商户请求订单号
        String merchantNo="100000000000006";//商户号
        String productCode="0003";//请求的产品编码
        String key="2B207D1341706A7R4160724854065152";//秘钥
        String dateTime=DateUtil.getSystemTimeFourteen();//时间戳
        String certNo = DESUtil.encode(key,idNO);
//        logger.info("名称加密前为：{}",realName);
        String userName =DESUtil.encode(key,realName);
//        logger.info("名称加密后为：{}",userName);
//        String imageServerUrl = paramService.findValueByName("imageServerUrl");
//        String photo=Base64.encode(FilesUtils.getImageFromNetByUrl("http://47.98.205.206/imgserver/"+idHandleImgUrl));
        String signSource = merchantNo + merchOrderId + dateTime + productCode + key;//原始签名值
        String sign = MD5Util.MD5Encode(signSource);//签名值


        Map<String, String> map = new HashMap<>();
        map.put("merchOrderId", merchOrderId);
//        logger.info(merchOrderId);
        map.put("merchantNo", merchantNo);
        map.put("productCode", productCode);
        map.put("userName", userName);//加密
        map.put("certNo", certNo);// 加密);
        map.put("dateTime", dateTime);
        map.put("photo", URLEncoder.encode(idHandleImgUrl,"utf-8").replace("\\",""));
        map.put("sign", sign);
        String content = AuthUtil.packgeSign(map);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body1 = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("http://47.99.1.34/wisdom/identity/fastIdentify")
                .method("POST", body1)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .build();
        Response response = null;
        JSONObject returnObject = null;
        try {
            response = client.newCall(request).execute();
            String string = response.body().string();

            // 解密响应数据
            returnObject= JSONObject.parseObject(string);
            return returnObject;
        } catch (IOException e) {
            returnObject.put("msg","系统错误");
            returnObject.put("code","-1");
            return returnObject;
        }
    }
    /**打包成网页参数格式 */
    public static String packgeSign(Map<String,String> map){
        StringBuilder sb = new StringBuilder();
        for(String key:map.keySet()){
            String value = map.get(key);
            if ( value == null || "".equals(value.trim())){
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }
        if(sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
    public static String createSign(String str) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("1000000008000010").append(str).append("2B207D1341706A7R4160724854065153");
        String newSign = MD5Util.MD5Encode(sb.toString(),"UTF-8");
        return newSign;
    }



    public static JSONObject fk(String idNO, String realName) throws Exception {
        String key="8f933a619a3042b597d8f99d712a2932";
        String string= String.valueOf(System.currentTimeMillis())+new Random().nextInt(10);
        JSONObject itemJSONObj =new JSONObject();
        itemJSONObj.put("custid", "1000000012");//账号
        itemJSONObj.put("txcode", "tx00012");//交易码
        itemJSONObj.put("productcode", "000012");//业务编码
        itemJSONObj.put("serialno", string);//流水号

        StringBuilder sb=new StringBuilder();
        sb.append("1000000012000012").append(string).append(key);
        String newSign = MD5Util.MD5Encode(sb.toString(),"UTF-8");

        itemJSONObj.put("mac", newSign);//随机状态码   --验证签名  商户号+订单号+时间+产品编码+秘钥
        itemJSONObj.put("name", realName);
        itemJSONObj.put("idNo", ""+idNO);
        itemJSONObj.put("timestamp", ""+System.currentTimeMillis());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body1 = RequestBody.create(mediaType, JSON.toJSONString(itemJSONObj));
        Request request = new Request.Builder()
                .url("http://47.99.129.98:8082/wisdom/entrance/pub")
                .method("POST", body1)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = null;
        JSONObject returnObject = new JSONObject();
        try {
            response = client.newCall(request).execute();
            string = response.body().string();
            // 解密响应数据
            returnObject= JSONObject.parseObject(string);
            return returnObject;
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.put("msg","系统错误");
            returnObject.put("code","-1");
            return returnObject;
        }
    }


    public static JSONObject fk2(String idNO, String realName) throws Exception {

        String key="8f933a619a3042b597d8f99d712a2932";
        String custid ="1000000012";
        String productcode ="000011";
        String string= String.valueOf(System.currentTimeMillis())+new Random().nextInt(10);
        JSONObject itemJSONObj =new JSONObject();
        itemJSONObj.put("custid", custid);//账号
        itemJSONObj.put("txcode", "tx00011");//交易码
        itemJSONObj.put("productcode", productcode);//业务编码
        itemJSONObj.put("serialno", string);//流水号

        StringBuilder sb=new StringBuilder();
        sb.append(custid);
        sb.append(productcode);
        sb.append(string).append(key);
        String newSign = MD5Util.MD5Encode(sb.toString(),"UTF-8");

        itemJSONObj.put("mac", newSign);//随机状态码   --验证签名  商户号+订单号+时间+产品编码+秘钥
        itemJSONObj.put("name", realName);
        itemJSONObj.put("idNo", ""+idNO);
        itemJSONObj.put("timestamp", ""+System.currentTimeMillis());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body1 = RequestBody.create(mediaType, JSON.toJSONString(itemJSONObj));
        Request request = new Request.Builder()
                .url("http://47.110.18.250/wisdom/entrance/pub")
                .method("POST", body1)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = null;
        JSONObject returnObject = new JSONObject();
        try {
            response = client.newCall(request).execute();
            string = response.body().string();
            // 解密响应数据
            returnObject= JSONObject.parseObject(string);
            return returnObject;
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.put("msg","系统错误");
            returnObject.put("code","-1");
            return returnObject;
        }
    }



    public static JSONObject ys3(String idNO, String realName,String card) throws Exception {
        String key="9A0723248F21943R4208534528919553";
        String string= String.valueOf(System.currentTimeMillis())+new Random().nextInt(10);
        JSONObject itemJSONObj =new JSONObject();
        itemJSONObj.put("custid", "1000000006");//账号
        itemJSONObj.put("txcode", "tx00009");//交易码
        itemJSONObj.put("productcode", "000009");//业务编码
        itemJSONObj.put("serialno", string);//流水号

        StringBuilder sb=new StringBuilder();
        sb.append("1000000006000009").append(string).append(key);
        String newSign = MD5Util.MD5Encode(sb.toString(),"UTF-8");

        itemJSONObj.put("mac", newSign);//随机状态码   --验证签名  商户号+订单号+时间+产品编码+秘钥
        itemJSONObj.put("idname", realName);
        itemJSONObj.put("idcard", idNO);
        itemJSONObj.put("bankcard",card);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body1 = RequestBody.create(mediaType, JSON.toJSONString(itemJSONObj));
        Request request = new Request.Builder()
                //.url("http://127.0.0.1:8882/wisdom-new/entrance/pub")
                .url("http://47.99.209.40:8082/wisdom/entrance/pub")
                .method("POST", body1)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = null;
        JSONObject returnObject = new JSONObject();
        try {
            response = client.newCall(request).execute();
            string = response.body().string();
            // 解密响应数据
            returnObject= JSONObject.parseObject(string);
            return returnObject;
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.put("msg","系统错误");
            returnObject.put("code","-1");
            return returnObject;
        }
    }


    public static JSONObject yhk(String idNO, String realName,String card,String mobile) throws Exception {
        String key="9dd72e1a4c5c4db29fe9f47e40e34e7e";
        String custid ="1000000014";
        String productcode ="000013";

        String string= String.valueOf(System.currentTimeMillis())+new Random().nextInt(10);
        JSONObject itemJSONObj =new JSONObject();
        itemJSONObj.put("custid", custid);//账号
        itemJSONObj.put("productcode", productcode);//业务编码
        itemJSONObj.put("serialno", string);//流水号

        StringBuilder sb=new StringBuilder();
        sb.append(custid).append(productcode).append(string).append(key);
        String newSign = MD5Util.MD5Encode(sb.toString(),"UTF-8");

        itemJSONObj.put("mac", newSign);//随机状态码   --验证签名  商户号+订单号+时间+产品编码+秘钥
        itemJSONObj.put("idname", realName);
        itemJSONObj.put("idcard", idNO);
        itemJSONObj.put("bankcard",card);
        itemJSONObj.put("mobile",mobile);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body1 = RequestBody.create(mediaType, JSON.toJSONString(itemJSONObj));
        Request request = new Request.Builder()
                //.url("http://127.0.0.1:8882/wisdom-new/entrance/pub")
                .url("http://t.pyblkj.cn:8082/wisdom/entrance/pub")
                .method("POST", body1)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = null;
        JSONObject returnObject = new JSONObject();
        try {
            response = client.newCall(request).execute();
            string = response.body().string();
            // 解密响应数据
            returnObject= JSONObject.parseObject(string);
            return returnObject;
        } catch (IOException e) {
            e.printStackTrace();
            returnObject.put("msg","系统错误");
            returnObject.put("code","-1");
            return returnObject;
        }
    }


}
