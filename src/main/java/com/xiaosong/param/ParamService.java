package com.xiaosong.param;

import com.jfinal.plugin.redis.Redis;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VParams;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2019-12-29 20:20
 **/
public class ParamService {
    public static final ParamService me = new ParamService();
    public String findValueByName(String paramName) {
        //先从缓存中读取数据
        String value = null;
        value = findValueByNameFromRedis(paramName);
        //缓存中不存在，就从数据库中取值，并把值存入缓存中
        if (value == null){
            value = findValueByNameFromDB(paramName);
            if(value != null){//默认redis库
                Redis.use().set("params_" + paramName,value);
            }
        }
        return value;
    }

    /**
     * 从缓存中获取参数
     * @param paramName 参数名
     * @return
     */
    private String findValueByNameFromRedis(String paramName){
        //默认redis库别名为REDIS 可省别名
        return Redis.use().get("params_" + paramName);
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
