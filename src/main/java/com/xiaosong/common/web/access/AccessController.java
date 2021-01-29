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
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.interceptor.jsonbody.JsonBody;
import com.xiaosong.model.TblAccess;
import com.xiaosong.util.RetUtil;

import java.util.List;


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

    private AccessService tblAccessService = AccessService.me;

    public void updateAccess(@JsonBody TblAccess tblAccess) {
        try {

            if (tblAccess == null||tblAccess._getAttrNames().length==0) {
                renderJson(RetUtil.fail("参数缺失！"));
            }else{
            renderJson(tblAccessService.updateAccess(tblAccess));
            }
        }catch (Exception e){
            log.error("错误信息：",e);

            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }
   public void  bandOrgdeleteAccess(){
       try {

           if (tblAccessService.bandOrgdeleteAccess(getLong("id"),getLong("orgId"),getInt("status"))>0){
               renderJson(RetUtil.ok());
           }else {
               renderJson(RetUtil.fail());
           }
       }catch (Exception e){
           log.error("错误信息：",e);

           renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
       }


   }
    public void getAccessList( ){
        try {

            List<TblAccess> accessList = tblAccessService.getAccessList(getLong("orgId"), get("name"), getInt("status"));
            renderJson(RetUtil.okData(accessList));
        }catch (Exception e){
            log.error("错误信息：",e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }

    /**
     * 根据部门查询门禁列表
     */
    public void getAccessDeptList( ){
        try {

            List<TblAccess> accessList = tblAccessService.getAccessDeptList(getLong("id"));
            renderJson(RetUtil.okData(accessList));
        }catch (Exception e){
            log.error("错误信息：",e);
            renderJson(RetUtil.fail(e.getCause().getLocalizedMessage()));
        }
    }
}
