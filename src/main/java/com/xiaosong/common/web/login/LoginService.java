package com.xiaosong.common.web.login;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import com.xiaosong.model.VDeptUser;
import com.xiaosong.model.VSysUser;

/** 
* @author 作者 : xiaojf
* @Date 创建时间：2020年1月8日 上午10:58:50 
* 类说明 
*/
public class LoginService {
	public static final	LoginService me = new LoginService();
	public VSysUser checkLoginUser(String userName, String passWord) {
		VSysUser sysUser = VSysUser.dao.findFirst("select * from v_sys_user where (username='" + userName + "') and password='" + passWord + "'");
		if (sysUser==null||sysUser._getAttrNames().length==0){
			VDeptUser vDeptUser = VDeptUser.dao.findFirst("select realName,sysPwd,phone,idNO from v_dept_user where phone=? and sysPwd=?",userName,passWord);
			if (vDeptUser!=null&&vDeptUser._getAttrNames().length>0){
				sysUser=new VSysUser();
				sysUser.setTrueName(vDeptUser.getRealName())
						.setTel(vDeptUser.getPhone())
						.setPassword(vDeptUser.getSysPwd())
						.setRoleId(1L)
                        .setUsername(vDeptUser.getPhone())
                        .setCreatetime(DateUtil.now())
                        .setIsSync("F")
				.setParentId(1L);
                 sysUser.save();
            }
		}

		return sysUser;
	}
	
	public List<VSysUser> checkPwd(Long id, String passWord) {
		return VSysUser.dao.find("select * from v_sys_user where id="+id+" and password='"+passWord+"'");
	}


	public VSysUser getSysUserByUserName(String userName) {
		return VSysUser.dao.findFirst("select * from v_sys_user where username='"+userName+"'");
	}
	
	public boolean editPwd(Long id, String passWord) {
		VSysUser user = new VSysUser();
		user.setId(id);
		user.setPassword(passWord);
		return user.update();
	}
	
}
