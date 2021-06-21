package com.xiaosong.common.api.code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;
import com.xiaosong.model.VSysUser;
import com.xiaosong.model.vo.UserVo;
import com.xiaosong.util.RetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: xiaosong
 * @description: 验证码
 * @author: cwf
 * @create: 2019-12-29 17:00
 **/
public class CodeController extends Controller {
    @Inject
    CodeService codeService;
    Logger logger = LoggerFactory.getLogger(CodeController.class);

    //    //测试接口
//    public void index(){
//        codeService.verifyCode(get("phone"),get("code"),getInt("type"));
//        renderText("haha");
//    }
    public void sendCode() {

        String phone = get("phone");
        Integer type = getInt("type");
        if (phone == null) {
            phone = getAttrForStr("phone");
        }
        if (type == null) {
            type = getAttrForInt("type");
        }
        Object ok = codeService.sendMsg(phone, type, null, null, null, null, Constant.SYS_CODE);
        Result object = ok != null ? Result.success() : Result.fail();
        renderText(JSON.toJSONString(object, SerializerFeature.WriteNullStringAsEmpty));

    }

    /**
     * @Author: cwf
     * @Date: 2021/6/21 9:54
     * @Description: 1分钟短信
     */
    public void sendCodeMinute() {

        String userId = get("userId");

        UserVo user = CacheKit.get(Constant.SYS_ACCOUNT, userId);
        Integer type = getInt("type");
        if (user.getTel() == null) {
            user.setTel(getAttrForStr("phone"));
        }
        if (type == null) {
            type = getAttrForInt("type");
        }
        Object ok = codeService.sendMsg(user.getTel(), type, null, null, null, null,  Constant.SYS_CODEMINUTE);
        RetUtil retUtil = ok != null ? RetUtil.ok() : RetUtil.fail();
        renderText(JSON.toJSONString(retUtil, SerializerFeature.WriteNullStringAsEmpty));
    }
//    public  void testCode(){
//
//        String phone = get("phone");
//        if (phone==null){
//            phone= getAttrForStr("phone");
//        }
//
//        try {
//            renderText(JSON.toJSONString(codeService.testCode(phone), SerializerFeature.WriteNullStringAsEmpty));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
