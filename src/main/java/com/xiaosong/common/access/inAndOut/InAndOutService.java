package com.xiaosong.common.access.inAndOut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author xiaojf
* @version 创建时间：2019年12月2日 下午2:30:58
* 类说明
*/
public class InAndOutService {
	Logger log = LoggerFactory.getLogger(InAndOutService.class);
	
	public static final InAndOutService me = new InAndOutService();
	
	public void save(HttpServletRequest request,HttpServletResponse response) {
		
	}
}
