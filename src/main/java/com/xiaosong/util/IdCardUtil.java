package com.xiaosong.util;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VSysUser;
import com.xiaosong.model.vo.UserVo;
import org.apache.commons.lang3.StringUtils;

import java.security.NoSuchAlgorithmException;

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
            else {
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1********$2");
            }
        }
        return idNumber;
    }

    /**
     * 根据角色脱敏数据
     * @param idNumber
     * @return
     */
    public static String desensitizedDesIdNumber(String idNumber,boolean isSuperAdmin){
        if (!isSuperAdmin){
            return desensitizedIdNumber(idNumber);
        }
        return idNumber;
    }
    /**
     * 判断是否超级管理员权限
     * @param
     * @return
     */
    public static boolean  isAdmin(String userId){
        try {
            UserVo user = CacheKit.get(Constant.SYS_ACCOUNT, userId);
            if (user.getUserRole() == 1L) {
                return true;
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }
}
