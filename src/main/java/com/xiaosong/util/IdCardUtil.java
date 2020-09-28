package com.xiaosong.util;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by CNL on 2020/8/10.
 */
public class IdCardUtil {


    public static String desensitizedIdNumber(String idNumber){
        if (StringUtils.isNotEmpty(idNumber)) {
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
