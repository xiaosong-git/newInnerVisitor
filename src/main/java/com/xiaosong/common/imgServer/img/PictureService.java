package com.xiaosong.common.imgServer.img;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.xiaosong.MainConfig;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class PictureService {
    public static final PictureService me = new PictureService();
	  /** 
     * @param filepath
	 * @throws Exception 
     */
      Prop p = PropKit.use("imgConfig_develop.properties");

    public  String getCardData(String filepath) throws Exception {
        /*   String filepath = "E:\\self.jpg";  */
        String api_key = p.get("api_key");//"aB11SPj0CkserfQApOoA4QaBbGC6dZKk";
    	String api_secret = p.get("api_secret");//"EUavFiqRwGx96c5Dj0uburiNadruzmxU";
        String urlStr = p.get("imgurl");//"https://api-cn.faceplusplus.com/cardpp/v1/ocridcard";
        Map<String, String> textMap = new HashMap<String, String>();  
        textMap.put("name", "testname");  
        textMap.put("api_key", api_key);
        textMap.put("api_secret", api_secret);
        Map<String, String> fileMap = new HashMap<String, String>();  
        fileMap.put("image_file", filepath);  
        String ret = formUpload(urlStr, textMap, fileMap);  
        String  cardData=new String(ret);
   
        return cardData;
    }  
  
    /** 
     * 上传图片 
     * @param urlStr 
     * @param textMap 
     * @param fileMap 
     * @return 
     * @throws Exception 
     */  
    public  String formUpload(String urlStr, Map<String, String> textMap, Map<String, String> fileMap) throws Exception { 
        String res = "";  
        HttpURLConnection conn = null;  
        String BOUNDARY =getBoundary(); //boundary就是request头和上传文件内容的分隔符    
        try {  
            URL url = new URL(urlStr);  
            conn = (HttpURLConnection) url.openConnection();  
            conn.setConnectTimeout(5000);  
            conn.setReadTimeout(30000);  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            conn.setRequestMethod("POST");  
            
            conn.setRequestProperty("Accept-Charset", "utf-8");  
            conn.setRequestProperty("contentType", "utf-8"); 
            
            conn.setRequestProperty("Connection", "Keep-Alive");  
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8;boundary=" + BOUNDARY);  
            OutputStream out = new DataOutputStream(conn.getOutputStream());  
            // text    
            if (textMap != null) {  
                StringBuffer strBuf = new StringBuffer();  
                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, String> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
              
                    strBuf.append(inputValue);  
                    
                }  
                out.write(strBuf.toString().getBytes("UTF-8"));  
           
             
            }  
  
            // file    
            if (fileMap != null) {  
                Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, String> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    File file = new File(inputValue);  
                    String filename = file.getName();  
                    MagicMatch match = Magic.getMagicMatch(file, false, true);
                    String contentType = match.getMimeType();  
               
                    StringBuffer strBuf = new StringBuffer();  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + encode(filename) + "\"\r\n");  
                   
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n"); 
                   
                    out.write(strBuf.toString().getBytes("UTF-8"));  
  
                    DataInputStream in = new DataInputStream(new FileInputStream(file));  
                    int bytes = 0;  
                    byte[] bufferOut = new byte[1024];  
                    while ((bytes = in.read(bufferOut)) != -1) {  
                        out.write(bufferOut, 0, bytes);  
                    }  
                    in.close();  
                }  
            }  
            
  
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");  
            out.write(endData);  
            out.flush();  
            out.close();  
  
            // 读取返回数据    
            StringBuffer strBuf = new StringBuffer();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                strBuf.append(line).append("\n");  
            }  
            res = strBuf.toString();  
            reader.close();  
            reader = null;  
        } catch (Exception e) {  
            System.out.println("发送POST请求出错。" + urlStr);  
            e.printStackTrace();  
            throw e;
        } finally {  
            if (conn != null) {  
                conn.disconnect();  
                conn = null;  
            }  
        }          
        return res;  
    }  
    
    
    private  String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }
    
    private  String encode(String value) throws Exception{
        return URLEncoder.encode(value, "UTF-8");
    }
//    public String getBankCardDiscern(String filepath) throws Exception {
//
//        String key = MainConfig.p.get("bank_key"); //"aB11SPj0CkserfQApOoA4QaBbGC6dZKk";// 用户ocrKey
//        String secret =MainConfig.p.get("bank_secret");// "EUavFiqRwGx96c5Dj0uburiNadruzmxU";// // 用户ocrSecret
//        String url =MainConfig.p.get("bank_url");// "https://api-cn.faceplusplus.com/cardpp/beta/ocrbankcard";//
//        String json = null;
//        String time_used = "";
//        String request_id = "";
//        String error_message = "";
//        String bank_cards = "";
//        String cardsStr = "";
//        String cardNumber = "";
//        String bankName = "";
//        String resultback = "";
//        File file = new File(filepath);
//        resultback = doPost(url, file, key, secret, filepath);
//
//        System.out.println("返回数据：" + resultback);
//
//        if(StringUtils.isBlank(resultback) || "[]".equals(resultback) ){
//
//            ResponseBank bank = new ResponseBank();
//            json = JsonUtils.toJson(bank);
//            return json;
//        }
//
//
//        JSONObject jsonObject = JSON.parseObject(resultback);
//
//        time_used = jsonObject.getString("time_used");
//
//        request_id = jsonObject.getString("request_id");
//
//        error_message = jsonObject.getString("error_message");
//
//        if (StringUtils.isBlank(error_message)) {
//            System.out.println("识别成功");
//
//            System.out.println("======================");
//
//            bank_cards = jsonObject.getJSONArray("bank_cards").toJSONString();// bank_card信息
//
//            System.out.println("卡信息：" + bank_cards);
//
//            if (StringUtils.isBlank(bank_cards) || "[]".equals(bank_cards)) {
//                System.out.println("bank_cards信息为空");
//                ResponseBank bank = new ResponseBank();
//                json = JsonUtils.toJson(bank);
//                return json;
//
//            }
//
//            cardsStr = bank_cards.substring(1, bank_cards.length() - 1);
//
//            Map<String, String> cardMap = new HashMap<String, String>();
//            cardMap = (Map<String, String>) JSON.parse(cardsStr);
//
//            cardNumber = cardMap.get("number");
//            bankName = cardMap.get("bank");
//
//            System.out.println("银行卡号：" + cardNumber);
//            System.out.println("银行名称：" + bankName);
//
//            ResponseBank bank = new ResponseBank();
//
//            bank.setBankName(bankName);
//            bank.setCardNumber(cardNumber);
//            json = JsonUtils.toJson(bank);
//            return json;
//
//        } else {
//            System.out.println("识别失败");
//            ResponseBank bank = new ResponseBank();
//            json = JsonUtils.toJson(bank);
//
//            return json;
//        }
//
//    }
//
//    public String doPost(String url, File file, String key, String secret,
//                         String filepath) throws Exception {
//        String result = null;
//
//        CloseableHttpClient client = HttpClients.createDefault(); // 1.创建httpclient对象
//        HttpPost post = new HttpPost(url); // 2.通过url创建post方法
//
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create(); // 实例化实体构造器
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); // 设置浏览器兼容模式
//
//        builder.addPart("image_file", new FileBody(file)); // 添加"file"字段及其值；此处注意字段名称必须是"file"
//        builder.addPart("api_key",new StringBody(key, ContentType.create("text/plain",
//                Consts.UTF_8))); // 添加"key"字段及其值
//        builder.addPart(
//                "api_secret",
//                new StringBody(secret, ContentType.create("text/plain",
//                        Consts.UTF_8))); // 添加"secret"字段及其值
//
//
//        HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8"))
//                .build(); // 设置请求的编码格式，并构造实体
//
//        post.setEntity(reqEntity);
//        // **************************************</向post方法中封装实体>************************************
//
//        CloseableHttpResponse response = client.execute(post); // 4.执行post方法，返回HttpResponse的对象
//
//        System.out.println("返回码：" + response.getStatusLine().getStatusCode());
//        if (response.getStatusLine().getStatusCode() == 200) { // 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
//            result = EntityUtils.toString(response.getEntity(), "UTF-8");
//        } else {
//            System.out.println("服务器返回异常");
//        }
//
//        HttpEntity e = response.getEntity(); // 6.关闭资源
//        try {
//            if (e != null) {
//                InputStream instream = e.getContent();
//                instream.close();
//            }
//        } catch (Exception e2) {
//            e2.printStackTrace();
//            System.out.println("银行卡异常");
//        } finally {
//
//            response.close();
//
//        }
//        return result;// 7.返回识别结果
//
//    }
}
