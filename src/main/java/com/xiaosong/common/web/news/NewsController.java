package com.xiaosong.common.web.news;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.MainConfig;
import com.xiaosong.model.VNews;
import com.xiaosong.util.RetUtil;

public class NewsController extends Controller{
	private Log log = Log.getLog(NewsController.class);
	public NewsService srv = NewsService.me;
	
	public void findList() {
		String notice = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		String imageSaveDir =  MainConfig.p.get("imageSaveDir")+"";
		Page<Record> pagelist = srv.findList(notice,currentPage,pageSize);
		if(pagelist.getList()!=null) {
			for(Record record:pagelist.getList()) {
				if(record.getObject("newsImageUrl")!=null&&!record.getObject("newsImageUrl").equals("")) {
					record.set("newsImageUrl", imageSaveDir+record.getObject("newsImageUrl"));
				}
			}
		}
		renderJson(pagelist);
	}
	
	public void addNews() throws Exception {
		String newsTitle = getPara("newsName");
		String newsDetail = getPara("newsDetail");
		String newsImageUrl = getPara("newsImageUrl");
		String newsUrl = getPara("newsUrl");
		String newsStatus = "normal";
		String headline = getPara("headline");
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newsDate = dateformat.format(date);
		VNews news = getModel(VNews.class);
		news.setNewsName(newsTitle);
		news.setNewsDetail(newsDetail);
		news.setNewsImageUrl("news/"+newsImageUrl);
		news.setNewsUrl(newsUrl);
		news.setNewsStatus(newsStatus);
		news.setHeadline(headline);
		news.setNewsDate(newsDate);
		if(headline.equals("T")) {
			Db.update("update v_news set headline='F'");
			//srv.editNews(news1);
		}
		boolean bool = srv.addNews(news);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editNews() {
		long id = getLong("id");
		String newsTitle = getPara("newsName");
		String newsDetail = getPara("newsDetail");
		String newsImageUrl = getPara("newsImageUrl");
		String newsUrl = getPara("newsUrl");
		String headline = getPara("headline");
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String updateTime = dateformat.format(date);
		VNews news = getModel(VNews.class);
		news.setNewsName(newsTitle);
		news.setNewsDetail(newsDetail);
		news.setNewsImageUrl("news/"+newsImageUrl);
		news.setNewsUrl(newsUrl);
		news.setHeadline(headline);
		news.setUpdateTime(updateTime);
		news.setId(id);
		if(headline.equals("T")) {
			Db.update("update v_news set headline='F'");
			//srv.editNews(news1);
		}
		boolean bool = srv.editNews(news);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delNews() {
		Long id = getLong("id");
		boolean bool = srv.deleteNews(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
