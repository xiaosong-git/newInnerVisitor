package com.xiaosong.common.web.notice;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VNotice;

public class NoticeService {
public static final	NoticeService me = new NoticeService();
	
	public Page<Record> findList(String tittle,int currentPage,int pageSize){
		if(tittle!=null) {
			Page<Record> page = Db.paginate(currentPage, pageSize, "select *", "from (SELECT * from v_notice WHERE noticeTitle like CONCAT('%',?,'%')) as a", tittle);
			return page;
		}else {
			return Db.paginate(currentPage, pageSize, "select *", " from (SELECT * from v_notice) as a");
		}
	}
	
	public boolean addNotice(VNotice notice) {
		return notice.save();
	}
	
	public boolean editNotice(VNotice notice) {
		return notice.update();
	}
	
	public boolean deleteNotice(Long id) {
		return VNotice.dao.deleteById(id);
	}
}
