package com.xiaosong.common.web.key;

import org.apache.commons.codec.binary.Base64;

import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.RSA.RSAEncrypt;
import com.xiaosong.model.VKey;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月13日 下午8:01:20 
* 类说明 
*/
public class KeyService {
	public static final	KeyService me = new KeyService();
	
	public Page<VKey> findList(String swjCode,int currentPage,int pageSize){
		if(swjCode!=null) {
			Page<VKey> page = VKey.dao.paginate(currentPage, pageSize, "select *", "from v_key where type like CONCAT(?,'%')", swjCode);
			return page;
		}else {
			return VKey.dao.paginate(currentPage, pageSize, "select *", "from v_key");
		}
	}
	
	public boolean addKey(VKey key) throws Exception {
		key = getlicense(key);
		return key.save();
	}
	
	public boolean editKey(VKey key) throws Exception {
		String params = key.getSwiCode()+"|"+key.getMac()+"|"+key.getBegintime()+"|"+key.getEndtime();
		String license=Base64.encodeBase64String(RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(key.getPublicKey()), params.getBytes()));
		key.setLicense(license);
		return key.update();
	}
	
	public boolean deleteKey(Long id) {
		return VKey.dao.deleteById(id);
	}
	
	private VKey getlicense(VKey key) throws Exception {
		VKey keys = RSAEncrypt.genKeyPair();
		String params = key.getSwiCode()+"|"+key.getMac()+"|"+key.getBegintime()+"|"+key.getEndtime();
		String license=Base64.encodeBase64String(RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(keys.getPublicKey()), params.getBytes()));
		 key.setLicense(license);
		 key.setPrivateKey(keys.getPrivateKey());
		 key.setPublicKey(keys.getPublicKey());
		 //System.out.println(new String(RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(key.getPrivateKey()), Base64.decodeBase64(license))));
		return key;
	}
}
