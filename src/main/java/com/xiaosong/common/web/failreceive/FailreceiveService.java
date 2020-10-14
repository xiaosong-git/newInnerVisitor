package com.xiaosong.common.web.failreceive;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VDevice;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月14日 下午8:26:26 
* 类说明 
*/
public class FailreceiveService {
	public static final FailreceiveService me = new FailreceiveService();


	public Page<Record> findList(String ip,String status,String type,String gate,int currentPage,int pageSize){
		StringBuilder sbWhere  = new StringBuilder(" 1=1");
		List<Object> params = new ArrayList();
		return Db.paginate(currentPage, pageSize, "select *", "from tb_failreceive  order by failedId desc",params.toArray());
	}


}
