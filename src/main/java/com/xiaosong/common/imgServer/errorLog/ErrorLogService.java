package com.xiaosong.common.imgServer.errorLog;

import com.jfinal.log.Log;
import com.xiaosong.compose.Result;
import com.xiaosong.model.VErrorLog;

/**
 * @program: jfinal_demo_for_maven
 * @description: 错误日志
 * @author: cwf
 * @create: 2020-01-09 17:35
 **/
public class ErrorLogService {
    private Log log= Log.getLog(ErrorLogService.class);
    public static final ErrorLogService me = new ErrorLogService();

    public Result saveErrorLog(VErrorLog vErrorLog) {
        return   vErrorLog.save()? Result.success():Result.fail();
    }
}
