package com.xiaosong.common.web.params;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.model.VParams;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.AuthUtil;
import com.xiaosong.util.RetUtil;
import com.xiaosong.util.YunPainSmsUtil;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午5:13:01 
* 类说明 
*/
public class ParamsController extends Controller{
	private Log log = Log.getLog(ParamsController.class);
	public ParamService srv = ParamService.me;
	
	public void findList() {
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VParams> pagelist = srv.findList(currentPage,pageSize);
		renderJson(pagelist);
	}

	public void addParam() throws Exception {
		String paramName = getPara("paramName");
		String paramText = getPara("paramText");
		String remark = getPara("remark");
		VParams params = getModel(VParams.class);
		params.setParamName(paramName);
		params.setParamText(paramText);
		params.setRemark(remark);
		boolean bool = srv.addParams(params);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}

	public void editParam() {
		long id = getLong("id");
		String paramName = getPara("paramName");
		String paramText = getPara("paramText");
		String remark = getPara("remark");
		VParams params = getModel(VParams.class);
		params.setId(id);
		params.setParamName(paramName);
		params.setParamText(paramText);
		params.setRemark(remark);
		boolean bool = srv.editParams(params);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}

	public void delParam() {
		Long id = getLong("id");
		boolean bool = srv.deleteParams(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}


	public void test()
	{

		YunPainSmsUtil.test();
		String key = getPara("key");
		String test = CacheKit.get("PARAM",key);
		renderJson(RetUtil.ok(test));
	}

}
