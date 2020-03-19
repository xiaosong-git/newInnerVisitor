package com.xiaosong.common.access.companyUser;
import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
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
public class CompanyUserController extends Controller {
	Logger log = LoggerFactory.getLogger(CompanyUserController.class);
	
	public CompanyUserService companyUserService = CompanyUserService.me;
	
	  /**
	   * 确定数据
	 * @param
	 * @return
	 */
	public void findApplySucOrg(){
		  try {
			  renderText(JSON.toJSONString(companyUserService.findApplySucByOrg(get("org_code"))));
	        }catch (Exception e){
	            e.printStackTrace();
			  renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
	        }
		  
	  }
	
	 /**
     * 确认大楼全部记录
     * @param
     * @return
     */
    public void findApplyAllSucOrg(){
        try {
        	renderText("test");
//            renderJson(companyUserService.findApplyAllSucByOrg());
        }catch (Exception e){
            e.printStackTrace();
			renderJson(Result.unDataResult("fail", "系统异常"));
        }
	}
}

