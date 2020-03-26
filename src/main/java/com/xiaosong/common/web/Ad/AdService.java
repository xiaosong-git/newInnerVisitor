package com.xiaosong.common.web.Ad;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VAdBanner;

public class AdService {
public static final	AdService me = new AdService();
	
	public Page<Record> findList(String title,int currentPage,int pageSize){
		if(title!=null) {
			Page<Record> page = Db.paginate(currentPage, pageSize, "select *", "from (SELECT * from v_ad_banner where title like CONCAT('%',?,'%') ORDER BY orders asc) as a", title);
			return page;
		}else {
			return Db.paginate(currentPage, pageSize, "select *", " from (SELECT * from v_ad_banner ORDER BY orders asc) as a");
		}
	}
	public void setOption(){
		 Db.update("update v_ad_banner set orders=orders+1");
	}
	/**
	 * 编辑列表时候修改排序
	 * @param ordersNum
	 * @param orders
	 */
	public void updateOrders(int ordersNum,int orders) {
		if(ordersNum>orders) {
			Db.update("update v_ad_banner set orders=orders-1 WHERE orders>="+orders+" and orders<="+ordersNum);
		}else {
			Db.update("update v_ad_banner set orders=orders+1 WHERE orders>="+ordersNum);
		}
	}
	
	public void DelOrdersSet(Long id) {
		Db.update("update v_ad_banner INNER JOIN(SELECT orders as ordersNum from v_ad_banner where id="+id+") a set orders=orders-1 WHERE orders>a.ordersNum");
	}
	
	public boolean addAd(VAdBanner ad) {
		return ad.save();
	}
	
	public boolean editAd(VAdBanner ad) {
		return ad.update();
	}
	
	public boolean deleteAd(Long id) {
		return VAdBanner.dao.deleteById(id);
	}
}
