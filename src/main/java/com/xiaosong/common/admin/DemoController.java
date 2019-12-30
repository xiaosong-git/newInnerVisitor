package com.xiaosong.common.admin;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.common.compose.ResultData;
import com.xiaosong.test.TestCache;
import com.xiaosong.util.RetUtil;

import java.util.List;

/**
 * 测试控制器
 * @author Administrator
 *
 */
public class DemoController extends Controller{
	public DemoService srv = DemoService.me;
	@Inject
	TestCache testCache;
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
		testCache.incrThenGet();
//		testCache.setThenIncr();
		renderText("test");
	}

	public void demo1(){
		renderJson(srv.demo1());
	}
	public void demo2(){

		renderJson(RetUtil.ok("user",Db.findFirst(Db.getSqlPara("demo.findUser"))));
	}

}
