package com.xiaosong.common.key;

import com.xiaosong.compose.Result;
import com.xiaosong.model.VKey;
import com.jfinal.core.Controller;


import java.util.List;

/**
* @author xiaojf
* @version 创建时间：2019年12月16日 下午5:42:26
* 类说明
*/
public class KeyController extends Controller{
	
	public KeyService srv = KeyService.me;

	/**
	 * @return
	 * 查询秘钥
	 */
	public List<VKey> findKey(){
		return srv.findKey();
	}
	
	/**
	 * @param key
	 * @return
	 * 添加秘钥
	 */
	public Result insertKey(VKey key) {
		Boolean bool = srv.insertKey(key);
		if(bool) {
			return Result.success();
		}else {
			return Result.fail();
		}
	}
	/**
	 * @param key
	 * @return
	 * 修改秘钥
	 */
	public Result updateKey(VKey key) {
		Boolean bool = srv.updateKey(key);
		if(bool) {
			return Result.success();
		}else {
			return Result.fail();
		}
	}
	/**
	 * @param id
	 * @return
	 * 删除秘钥
	 */
	public Result deleteKey(Long id) {
		Boolean bool = srv.deleteKey(id);
		if(bool) {
			return Result.success();
		}else {
			return Result.fail();
		}
	}
}
