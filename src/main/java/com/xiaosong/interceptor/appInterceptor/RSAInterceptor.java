package com.xiaosong.interceptor.appInterceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
* @author xiaojf
* @version 创建时间：2019年12月16日 下午5:38:56
* 类说明
*/
public class RSAInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		inv.invoke();
	}

	
}
