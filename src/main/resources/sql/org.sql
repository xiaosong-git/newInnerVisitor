#sql("findOrgCodeByUserId")
	select org_code from  v_org  o left join v_company c on c.orgId=o.id
	left join v_app_user  u on u.companyId=c.id where u.id=#p(0)
#end
