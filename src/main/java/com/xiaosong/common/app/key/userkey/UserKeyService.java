package com.xiaosong.common.app.key.userkey;


import com.alibaba.druid.util.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.constant.TableList;
import com.xiaosong.util.BaseUtil;
import com.xiaosong.util.RedisUtil;

/**
 * 密钥相关的Service
 * Created by LZ on 2017/5/23.
 */
public class UserKeyService  {
    public static final UserKeyService me = new UserKeyService();
    public String findKeyByStatus(String cstatus) throws  Exception{
        String key = null;
        //redis修改，原dbNum=8 现在dbNum=32
        key = RedisUtil.getStrVal("key_workKey",3);
        if(key == null){
            key =  findKeyFromDB(cstatus);
            if(key != null){
                //redis修改，原dbNum=8 现在dbNum=32
                RedisUtil.setStr(3,"key_workKey",key,null);
            }
        }
        return key;
    }

    /**
     * 从数据库中获取密钥
     * @param cstatus
     * @return
     */
    private String findKeyFromDB(String cstatus){
        if(!StringUtils.isEmpty(cstatus)){
            String sql = "select workKey from"+ TableList.KEY +" where cstatus = '"+cstatus+"'";
            Record param = Db.findFirst(sql);
            if(param != null){
                return BaseUtil.objToStr(param.get("workKey"),null);
            }
        }
        return null;
    }

}
