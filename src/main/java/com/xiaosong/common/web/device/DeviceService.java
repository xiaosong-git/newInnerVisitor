package com.xiaosong.common.web.device;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.expr.ast.Array;
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
public class DeviceService {
	public static final DeviceService me = new DeviceService();


	public Page<Record> findList(String ip,String status,String type,String gate,int currentPage,int pageSize){

		StringBuilder sbWhere  = new StringBuilder(" 1=1");
		List<Object> params = new ArrayList();
		if(StringUtils.isNotBlank(ip))
		{
			sbWhere.append(" and ip = ?");
			params.add(ip);
		}
		if(StringUtils.isNotBlank(status))
		{
			sbWhere.append(" and status = ?");
			params.add(status);
		}

		if(StringUtils.isNotBlank(type))
		{
			sbWhere.append(" and type = ?");
			params.add(type);
		}

		if(StringUtils.isNotBlank(gate))
		{
			sbWhere.append(" and gate = ?");
			params.add(gate);
		}

		return Db.paginate(currentPage, pageSize, "select *", "from "+ TableList.DEVICE+" left join "+TableList.ORG +" on gate=org_code",params.toArray());
	}
	
	public boolean addDevice(VDevice config) {
		return config.save();
	}
	
	public boolean editDevice(VDevice config) {
		return config.update();
	}
	
	public boolean deleteDevice(Long id) {
		return VDevice.dao.deleteById(id);
	}

	public Page<Record> findWinccList(String name,int currentPage,int pageSize){
		StringBuilder sql = new StringBuilder();
		sql.append("from "+ TableList.DEVICE+" a left join "+TableList.ORG +" b on a.gate=b.org_code where type = 'SWJ'");
		if(name != null && name != ""){
			return Db.paginate(currentPage, pageSize, "select a.*,b.org_code,b.org_name",sql.toString()+"and device_name = ?",name);

		}else{
			return Db.paginate(currentPage, pageSize, "select a.*,b.org_code,b.org_name",sql.toString());
		}
	}


}
