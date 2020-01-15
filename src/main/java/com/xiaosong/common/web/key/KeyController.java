package com.xiaosong.common.web.key;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VKey;
import com.xiaosong.util.RetUtil;

/**
 * @author 作者 : xiaojf
 * @Date 创建时间：2020年1月13日 下午8:01:01 类说明
 */
public class KeyController extends Controller {
	private Log log = Log.getLog(KeyController.class);
	public KeyService srv = KeyService.me;

	public void findList() {
		String swiCode = getPara("queryString");
		int currentPage = getInt("currentPage");
		int pageSize = getInt("pageSize");
		Page<VKey> pagelist = srv.findList(swiCode, currentPage, pageSize);
		renderJson(pagelist);
	}

	public void addKey() throws Exception {
		String swiCode = getPara("swi_code");
		String mac = getPara("mac");
		String begintime = getPara("begintime");
		String endtime = getPara("endtime");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VKey key = getModel(VKey.class);
		key.setSwiCode(swiCode);
		key.setMac(mac);
		key.setBegintime(begintime);
		key.setEndtime(endtime);
		key.setCreatetime(createtime);
		boolean bool = srv.addKey(key);
		if (bool) {
			renderJson(RetUtil.ok());
		} else {
			renderJson(RetUtil.fail());
		}
	}

	public void editKey() throws Exception {
		long id = getLong("id");
		String swiCode = getPara("swi_code");
		String mac = getPara("mac");
		String begintime = getPara("begintime");
		String endtime = getPara("endtime");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createtime = df.format(new Date());
		VKey key = getModel(VKey.class);
		key.setId(id);
		key.setSwiCode(swiCode);
		key.setMac(mac);
		key.setBegintime(begintime);
		key.setEndtime(endtime);
		key.setCreatetime(createtime);
		boolean bool = srv.editKey(key);
		if (bool) {
			renderJson(RetUtil.ok());
		} else {
			renderJson(RetUtil.fail());
		}
	}

	public void delKey() {
		Long id = getLong("id");
		boolean bool = srv.deleteKey(id);
		if (bool) {
			renderJson(RetUtil.ok());
		} else {
			renderJson(RetUtil.fail());
		}
	}

}
