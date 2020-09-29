#sql("check")
select id from  v_visitor_record  where userId = ? and visitorId =? and recordType = ?
 and cstatus<>'applyFail' and STR_TO_DATE(startDate,'%Y-%m-%d %H:%i')<STR_TO_DATE(?,'%Y-%m-%d %H:%i')
 and   STR_TO_DATE(endDate,'%Y-%m-%d %H:%i')>STR_TO_DATE(?,'%Y-%m-%d %H:%i')
#end

#sql("findRecordFromId")
SELECT
	vr.*,
	realName,
	sex,
	idHandleImgUrl,
	headImgUrl,
	d.dept_name companyName,
	u.addr
FROM
	v_visitor_record vr
	LEFT JOIN v_dept_user u ON u.id = vr.visitorId
	LEFT JOIN v_dept d ON d.id = vr.companyId
 where vr.id=? or vr.pid = ?
#end

#sql("findOrgCode")
FROM
	v_visitor_record vr
	LEFT JOIN v_dept_user du ON vr.visitorId = du.id
	LEFT JOIN v_dept_user u ON vr.userId = u.id
	LEFT JOIN v_dept d ON vr.companyId = d.id
	LEFT JOIN v_org o ON d.org_id = o.id
WHERE
	vr.cstatus = 'applySuccess'
	AND vr.orgCode = ?
	AND vr.startDate <= date_add( now( ), INTERVAL + 60 MINUTE ) AND vr.endDate >= date_add( now( ), INTERVAL - 60 MINUTE )
	AND vr.isFlag = 'F'
#end