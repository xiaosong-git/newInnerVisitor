package com.xiaosong.validate.foreign;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.validate.Validator;
import com.xiaosong.RSA.RSAEncrypt;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VKey;
import org.apache.commons.codec.binary.Base64;

/**
 * @program: xiaosong
 * @description: 上位机校验rsa
 * @author: cwf
 * @create: 2019-12-28 10:48
 **/
public class ForeginValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        setRet(Ret.fail("sign","fail"));
        validateRequiredString("pospCode", "desc", "请输入上位机码");
        validateRequiredString("orgCode", "desc", "请输入大楼编码");
        validateRequiredString("mac", "desc", "请输入校验码");
        String mac = CacheKit.get("KEY", c.get("orgCode") + c.get("pospCode"));
        if (mac==null){
            VKey key = VKey.dao.findFirst(Db.getSql("foreign.findOrgCode"), c.get("pospCode"), c.get("orgCode"));
            if (key==null) {
                addError("desc", "未通过pospCode与orgCode找到上位机！");
            }
            try {
                byte[] macs = RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(key.getPublicKey()), Base64.decodeBase64(c.get("mac")));
                String str = new String(macs);
                CacheKit.put("KEY",c.get("orgCode") + c.get("pospCode"),c.get("mac"));
            } catch (Exception e) {
                e.printStackTrace();
                addError("desc", "校验错误！"+e);
            }
        }else if (!mac.equals(c.get("mac")) ){
            addError("desc", "校验错误！");
        }

    }
    @Override
    protected void handleError(Controller c) {
       Result result=new Result();
        result.setVerify(getRet());
        c.renderJson(result);
//        c.renderJson(Result.unDataResult("fail","缺少手机号"));
    }
}
