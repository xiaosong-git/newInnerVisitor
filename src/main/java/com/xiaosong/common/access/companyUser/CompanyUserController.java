package com.xiaosong.common.access.companyUser;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.xiaosong.common.api.deptUser.DeptUserService;
import com.xiaosong.compose.Result;
import com.xiaosong.validate.foreign.ForeginValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author xiaojf
* @version 创建时间：2019年12月4日 上午11:16:32
* 类说明
*/
public class CompanyUserController extends Controller {
	Logger log = LoggerFactory.getLogger(CompanyUserController.class);
	
	public CompanyUserService companyUserService = CompanyUserService.me;
	@Inject
	private DeptUserService deptUserService;
	/**
	 * 确认大楼全部记录
	 * @param
	 * @return
	 */
	@Before(ForeginValidator.class)
	public void newFindApplyAllSucOrg(){
		try {
			renderText("test");
			renderText(JSON.toJSONString(companyUserService.newFindApplyAllSucOrg(get("orgCode"),getInt("pageNum"),getInt("pageSize"),null)));
		}catch (Exception e){
			e.printStackTrace();
			renderJson(Result.unDataResult("fail", "系统异常"));
		}
	}
	/**
	 * 确认大楼全部记录
	 * @param
	 * @return
	 */
	@Before(ForeginValidator.class)
	public void newFindApplySucOrg(){
		try {
			renderText("test");
			renderText(JSON.toJSONString(companyUserService.newFindApplyAllSucOrg(get("orgCode"),getInt("pageNum"),getInt("pageSize"),"single")));
		}catch (Exception e){
			e.printStackTrace();
			renderJson(Result.unDataResult("fail", "系统异常"));
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



	/**
	 * 全部初始化数据
	 */
	public void findApplySuc(){
		try {
			renderText(JSON.toJSONString(deptUserService.findApplySuc(get("userId"))));
		}catch (Exception e){
			e.printStackTrace();
			renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));

		}
	}

	/**
	 * 查询访客所拥有的公司
	 */

	public void findVisitComSuc(){
		try {
			renderText(JSON.toJSONString(deptUserService.findApplySuc(get("visitorId"))));
		}catch (Exception e){
			e.printStackTrace();
			renderText(JSON.toJSONString(Result.unDataResult("fail", "系统异常")));
		}
	}
}

