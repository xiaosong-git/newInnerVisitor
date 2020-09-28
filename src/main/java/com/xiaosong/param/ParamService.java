package com.xiaosong.param;

import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VParams;

/**
 * @program: xiaosong
 * @description:
 * @author: cwf
 * @create: 2019-12-29 20:20
 **/
public class ParamService {
    public static final ParamService me = new ParamService();
    public String findValueByName(String paramName) {
        //先从缓存中读取数据
        String value = null;
        try {
            value = CacheKit.get("PARAM", paramName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //缓存中不存在，就从数据库中取值，并把值存入缓存中
        if (value == null){
            value = findValueByNameFromDB(paramName);
            if(value != null){//默认redis库
                CacheKit.put("PARAM" , paramName,value);
            }
        }
        return value;
    }

    /**
     * 从数据库中获取系统参数
     * @param paramName
     * @return
     */
    private String findValueByNameFromDB(String paramName){
        String sql =" select * from "+ TableList.PARAM +" where paramName = ?";
        VParams param = VParams.dao.findFirst(sql, paramName);
        if( param != null){
            return param.getParamText();
        }
        return null;
    }
}
