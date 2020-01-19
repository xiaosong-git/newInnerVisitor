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
 where vr.id=?
#end