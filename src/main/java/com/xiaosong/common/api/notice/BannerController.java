package com.xiaosong.common.api.notice;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.TableList;
import com.xiaosong.param.ParamService;

import java.util.List;

/**
 * 轮播图
 * @Author linyb
 * @Date 2017/5/8 15:36
 */

public class BannerController extends Controller {


    @Inject
    AdBannerService adBannerService;
    @ActionKey("/visitor/banner")
    public void list(){
        try {
            renderText(JSON.toJSONString(adBannerService.list()));
        }catch (Exception e){
            e.printStackTrace();
            renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
        }
    }
}
