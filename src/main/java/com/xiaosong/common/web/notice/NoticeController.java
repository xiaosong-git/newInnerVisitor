package com.xiaosong.common.web.notice;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VNotice;
import com.xiaosong.util.RetUtil;

public class NoticeController extends Controller{
	private Log log = Log.getLog(NoticeController.class);
	public NoticeService srv = NoticeService.me;
	
	public void findList() {
		String notice = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<Record> pagelist = srv.findList(notice,currentPage,pageSize);
		renderJson(pagelist);
	}
	
	public void addNotice() throws Exception {
		String tittle = getPara("noticeTitle");
		String content = getPara("content");
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
		String createdate = dateformat.format(date);
		String createtime = timeformat.format(date);
		String status = "normal";
		VNotice notice = getModel(VNotice.class);
		notice.setNoticeTitle(tittle);
		notice.setContent(content);
		notice.setCreateDate(createdate);
		notice.setCreateTime(createtime);
		notice.setCstatus(status);
		boolean bool = srv.addNotice(notice);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editNotice() {
		long id = getLong("id");
		String tittle = getPara("noticeTitle");
		String content = getPara("content");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String updatetime = df.format(new Date());
		VNotice notice = getModel(VNotice.class);
		notice.setNoticeTitle(tittle);
		notice.setContent(content);
		notice.setUpdateTime(updatetime);
		notice.setId(id);
		boolean bool = srv.editNotice(notice);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delNotice() {
		Long id = getLong("id");
		boolean bool = srv.deleteNotice(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
