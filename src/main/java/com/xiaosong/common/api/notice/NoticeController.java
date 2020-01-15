package com.xiaosong.common.api.notice;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.constant.Constant;

/**
 * @Author linyb
 * @Date 2017/4/16 10:06
 */

public class NoticeController extends Controller {
    Log log=Log.getLog(NoticeController.class);
   @Inject
    private NoticeService noticeService;

    /**
     * 获取公告信息
     * @Author linyb
     * @Date 2017/4/16 10:10
     */
    public void list(){
        try {
            renderText(JSON.toJSONString(noticeService.findNoticeByUser(get("userId"), getInt("pageNum"), getInt("pageSize"))));
        } catch (Exception e) {
            log.error("获取公告信息错误{}",e);
            renderText(JSON.toJSONString(Result.fail()));
        }
    }
    /**
     * 获取所有上级组织的公告
     * @return com.goldccm.model.compose.Result
     * @throws Exception
     * @author chenwf
     * @date 2019/8/5 9:53
     */

    public void allList(){

        Integer pageNum = getAttrForInt("pageNum");
        Integer pageSize = getAttrForInt("pageSize");
        try {
            renderText(JSON.toJSONString(noticeService.findBySidCompany(get("userId"), pageNum, pageSize)));
        } catch (Exception e) {
            log.error("获取所有上级组织的公告错误{}",e);
            renderText(JSON.toJSONString(Result.fail()));
        }
    }
    @ActionKey("/visitor/news/list")
    public void news(){
        try {
            renderText(JSON.toJSONString(noticeService.findByStatus(Constant.KEY_STATUS_NORMAL, getInt("pageNum"), getInt("pageSize"))));
        } catch (Exception e) {
            log.error("获取所有上级组织的公告错误{}",e);
            renderText(JSON.toJSONString(Result.fail()));
        }
    }
}
