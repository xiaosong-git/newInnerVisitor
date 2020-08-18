package com.xiaosong.common.web.Ad;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.MainConfig;
import com.xiaosong.model.VAdBanner;
import com.xiaosong.util.RetUtil;

public class AdController extends Controller{
	private Log log = Log.getLog(AdController.class);
	public AdService srv = AdService.me;
	
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
	
	public void addAd() throws Exception {
		String title = getPara("title");
		String imgUrl = getPara("imgUrl");
		String hrefUrl = getPara("hrefUrl");
		int status = 1;
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime = dateformat.format(date);
		VAdBanner ad = getModel(VAdBanner.class);
		ad.setTitle(title);
		ad.setImgUrl("ad/"+imgUrl);
		ad.setHrefUrl(hrefUrl);
		ad.setStatus(status);
		ad.setCreateTime(createTime);
		ad.setOrders(1);
		srv.setOption();
		boolean bool = srv.addAd(ad);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void editAd() {
		int id = getInt("id");
		String title = getPara("title");
		String imgUrl = getPara("imgUrl");
		String hrefUrl = getPara("hrefUrl");
		int orders = getInt("orders");
		int ordersNum = getInt("ordersNum");
		int status = 1;
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String updateTime = dateformat.format(date);
		VAdBanner ad = getModel(VAdBanner.class);
		ad.setTitle(title);
		ad.setImgUrl("ad/"+imgUrl);
		ad.setHrefUrl(hrefUrl);
		ad.setStatus(status);
		ad.setUpdateTime(updateTime);
		ad.setOrders(ordersNum);
		ad.setId(id);
		srv.updateOrders(ordersNum,orders);
		boolean bool = srv.editAd(ad);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
	
	public void delAd() {
		Long id = getLong("id");
		srv.DelOrdersSet(id);
		boolean bool = srv.deleteAd(id);
		if(bool) {
			renderJson(RetUtil.ok());
		}else {
			renderJson(RetUtil.fail());
		}
	}
}
