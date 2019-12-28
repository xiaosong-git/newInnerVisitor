package com.xiaosong.common.key;

import com.xiaosong.model.VKey;

import java.util.List;

/**
* @author xiaojf
* @version 创建时间：2019年12月16日 下午5:42:54
* 类说明
*/
public class KeyService {

	public static final	KeyService me = new KeyService();
	public static final VKey dao = VKey.dao;
	
	public List<VKey> findKey() {
		return VKey.dao.find("selecet * from v_key");
	}
	
	public boolean insertKey(VKey key) {
		return key.dao.save();
	}
	
	public boolean updateKey(VKey key) {
		return key.update();
	}
	
	public boolean deleteKey(Long id) {
		return dao.deleteById(id);
	}
}
