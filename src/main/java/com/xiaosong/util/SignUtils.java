package com.xiaosong.util;

/**
 * Created by CNL on 2020/9/27.
 */
public class SignUtils {


    /**
     * 签名协议md5(timestamp+md5(Key)+nonce+data) MD5全部大写
     * @param timestamp
     * @param key
     * @param nonce
     * @param data
     * @return
     */
    public  static String getSign(long timestamp,String key,String nonce,String data) {
        String sign = null;

        try {
            String s = timestamp + MD5Util.MD5(key).toUpperCase() + nonce + data;
            System.out.println(s);
            if(data == null || data.equals("null")){
                data = "";
            }
            sign = MD5Util.MD5(timestamp + MD5Util.MD5(key).toUpperCase() + nonce + data);
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return sign != null ? sign.toUpperCase() : null;
    }


}
