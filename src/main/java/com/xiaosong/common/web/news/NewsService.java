package com.xiaosong.common.web.news;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VNews;

public class NewsService {
public static final	NewsService me = new NewsService();
	
	public Page<Record> findList(String tittle,int currentPage,int pageSize){
		if(tittle!=null) {
			Page<Record> page = Db.paginate(currentPage, pageSize, "select *", "from (SELECT * from v_news WHERE newsName like CONCAT('%',?,'%')) as a", tittle);
			return page;
		}else {
			return Db.paginate(currentPage, pageSize, "select *", " from (SELECT * from v_news) as a");
		}
	}
	
	public boolean addNews(VNews news) {
		return news.save();
	}
	
	public boolean editNews(VNews news) {
		return news.update();
	}
	
	public boolean deleteNews(Long id) {
		return VNews.dao.deleteById(id);
	}
}
