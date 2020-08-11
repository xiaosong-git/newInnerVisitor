package com.xiaosong.util;

import com.mysql.jdbc.StringUtils;

/**
 * Created by CNL on 2020/8/10.
 */
public class IdCardUtil {


    public static String desensitizedIdNumber(String idNumber){
        if (!StringUtils.isNullOrEmpty(idNumber)) {
            if (idNumber.length() == 15){
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1******$2");
            }
            if (idNumber.length() == 18){
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1*********$2");
            }
        }
        return idNumber;
    }

}
