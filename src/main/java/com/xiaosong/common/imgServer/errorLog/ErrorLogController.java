package com.xiaosong.common.imgServer.errorLog;

import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VErrorLog;


public class ErrorLogController extends Controller {
    Log log=Log.getLog(ErrorLogController.class);
    public void uploadErrorLog(@Para("")VErrorLog vErrorLog)  {
        System.out.println(vErrorLog.getErrorTime());
        try {
            renderJson(ErrorLogService.me.saveErrorLog(vErrorLog));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson( Result.unDataResult("fail", "系统异常"));
        }
    }

}
