#sql("findOrgCodeByUserId")
	SELECT
	org_code
FROM
	v_org o
	LEFT JOIN v_dept d ON d.org_id = o.id
	LEFT JOIN v_dept_user u
on u.deptId=d.id where u.id=#p(0)
#end
