package com.xiaosong.common.access.companyUser;


import com.xiaosong.common.base.BaseController;
import com.xiaosong.compose.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
* @author xiaojf
* @version 创建时间：2019年12月4日 上午11:16:32
* 类说明
*/
public class CompanyUserController extends BaseController {
	Logger log = LoggerFactory.getLogger(CompanyUserController.class);
	
	public CompanyUserService companyUserService = CompanyUserService.me;
	
	  /**
	   * 确定数据
	 * @param request
	 * @return
	 */
	public Result findApplySucOrg(HttpServletRequest request){
		  try {
	            Map<String,Object> paramMap = getParamsToMap(request);
	            return companyUserService.findApplySucByOrg(paramMap);
	        }catch (Exception e){
	            e.printStackTrace();
	            return Result.unDataResult("fail", "系统异常");
	        }
		  
	  }
	
	 /**
     * 确认大楼全部记录
     * @param request
     * @return
     */
    public Result findApplyAllSucOrg(HttpServletRequest request){
        try {
            Map<String,Object> paramMap = getParamsToMap(request);
            return companyUserService.findApplyAllSucByOrg(paramMap);
        }catch (Exception e){
            e.printStackTrace();
            return Result.unDataResult("fail", "系统异常");
        }
    }
}
