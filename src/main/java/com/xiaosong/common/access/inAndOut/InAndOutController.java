package com.xiaosong.common.access.inAndOut;

import com.xiaosong.common.base.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author xiaojf
* @version 创建时间：2019年12月2日 下午2:27:17
* 通行记录接口（用于文件传输）
*/
public class InAndOutController extends BaseController {
	
	public InAndOutService inAndOutService = InAndOutService.me;
	
	public void save(HttpServletRequest request,HttpServletResponse response) {
		try {
            inAndOutService.save(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }

	}
}
