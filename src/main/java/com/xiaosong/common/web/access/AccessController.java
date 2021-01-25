/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: accessController
 * Author:   Administrator
 * Date:     2021/1/25 0025 20:48
 * Description: 门禁
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.xiaosong.common.web.access;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.xiaosong.interceptor.jsonbody.JsonBody;
import com.xiaosong.model.TblAccess;
import com.xiaosong.util.RetUtil;

/**
 * 〈一句话功能简述〉<br> 
 * 〈门禁〉
 *
 * @author cwf
 * @create 2021/1/25 20:48
 * @since 1.0.0
 */
public class AccessController extends Controller {
    private Log log= Log.getLog(AccessController.class);

    private AccessService accessService = AccessService.me;

    public void updateAccess(@JsonBody TblAccess tblAccess) throws Exception {
        if (tblAccess==null){
            renderJson(RetUtil.fail("参数缺失！"));
            return;
        }
        renderJson( accessService.updateAccess(tblAccess));
    }

}
