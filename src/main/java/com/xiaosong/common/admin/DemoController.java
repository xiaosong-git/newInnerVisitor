package com.xiaosong.common.admin;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.util.RetUtil;

import java.util.List;

/**
 * 测试控制器
 * @author Administrator
 *
 */
public class DemoController extends Controller{
	public DemoService srv = DemoService.me;
	
	/*public void index(){
		System.out.println(srv.findTblNews());
		renderJson(RetUtil.ok("news",srv.findTblNews()));
	}*/
	
	public void index() {
		List<Record> list = srv.findDictionaty();
		System.out.println(list.get(0));
		renderJson(RetUtil.ok("Dictionaty",list));
	}
	public void aa(){
		renderText("test");
	}

}
