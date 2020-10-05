package com.xiaosong.util;

import java.security.MessageDigest;

/**
 * @Author linyb
 * @Date 2016/12/8 13:36
 */
public class MD5Util {

    public final static String MD5(String s) throws Exception {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
//        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
       /* } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
    }

    public MD5Util()
    {
    }

    private static String byteArrayToHexString(byte b[])
    {
        StringBuffer resultSb = new StringBuffer();
        for(int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b)
    {
        int n = b;
        if(n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin)
    {
        String resultString = null;
        try
        {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes())).toUpperCase();
        }
        catch(Exception exception) { }
        return resultString;
    }
    public static String MD5Encode(String origin,String charset)
    {
        String resultString = null;
        try
        {
            //resultString = new String(origin.getBytes(charset));
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes(charset))).toUpperCase();
        }
        catch(Exception exception) { }
        return resultString;
    }

    private static final String hexDigits[] = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"
    };

    public static void main(String[] args) throws Exception {

        String s = "abc&2&1&1&1569811676678|W0M0MzQ3NzIzNjA5NDI1OTIwXVvlj7bpnJZdWzEyQTEzQ0ZFMDA0MDc0RTBERDVBMzJFRjkxMDM0QzEzQTc0QUU0NEVBQTk4NUU4Q11b6ZmI57u05Y+RXVsxODE1MDc5Nzc0OF1baGx4el1bMjAxOS0wOS0yMCAxMDo1MF1bMjAxOS0wOS0yMCAxMjo1MF0=";
        String s2 = "1601369053E10ADC3949BA59ABBE56E057F20F883Ecd7857d17eff48a18fdccc2e15ff2460{\"visitor_card_no\":\"36230119990609155X\",\"visitor_name\":\"朱宗文\",\"visitor_sex\":1,\"visitor_phone\":\"12312312312\",\"visitor_plate\":\"\",\"scene_photo\":\"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\",\"staff_id\":\"8\",\"appoint_time\":\"2020-09-29 16:54\",\"visit_hours\":2.0,\"visit_reason\":\"商务拜访\",\"apply_type\":3}";
        System.out.println(s2);
        System.out.println(MD5(s2).toUpperCase());
       /* System.out.println(MD5(s));
        System.out.println(MD5Encode(s));

        String nonce = "ee334";
        String key = "123456";
        String Md5Key  = MD5(key).toUpperCase();
        long timestamp = 1601288738;
        String date = "{name:lin}";
        String md5 = MD5(timestamp+Md5Key+nonce+date);
        System.out.println(md5);*/

    }
}
