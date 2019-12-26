#namespace("sys_account")
	#sql("admin-paginate")
		select a.id,a.name,a.username,a.lastLoginAt,a.roleId,b.name as roleName from sys_account a
		left join sys_role b on a.roleId = b.id
		where 1 = 1
		#if(sk.notBlank(name))
			and a.name = #p(name)
		#end
		#if(sk.notBlank(username))
			and a.username = #p(username)
		#end
	#end
#end
#namespace("inAndOut")
	#include("com/xiaosong/common/access/inAndOut/inAndOut.sql")
#end
#namespace("demo")
	#include("com/xiaosong/common/admin/demo.sql")
#end
#namespace("companyUser")
	#include("com/xiaosong/common/access/companyUser/companyUser.sql")
#end
