#sql("findByPhone")
    select * from v_dept_user where phone=#p(0)
#end
#sql("findId")
    select id from v_dept_user where phone=?
#end
#sql("findIdName")
    select id from v_dept_user where phone=? and realName=?
#end
#sql("findFriend")
    select * from v_user_friend where userId=? and friendId=?
#end
#sql("isfriend")
     select id from v_user_friend where userId=? and friendId=?
#end
#sql("updateFriend")
    update v_user_friend set applyType=? where userId=? and friendId=?
#end
#sql("deleteUserFriend")
   update v_user_friend set applyType=2 where userId=? and friendId=?
#end
#sql("findUserFriend")
 SELECT
	uf.id ufId,
	u.id,
	u.realName,
	u.phone,
	d.org_id,
	u.addr,
	u.idHandleImgUrl,
	u.deptId,
	u.headImgUrl,
	uf.remark,
	d.dept_name companyName,
	( SELECT fuf.applyType FROM v_user_friend fuf WHERE fuf.userId = uf.friendId AND fuf.friendId = ? limit 1) applyType
FROM
	v_user_friend uf
	LEFT JOIN v_dept_user u ON uf.friendId = u.id
	LEFT JOIN v_dept d on d.id=u.deptId
WHERE
	uf.userId = ?
	AND uf.applyType =1
#end

#sql("findFriendApplyMe")
SELECT
	*
FROM
	(
SELECT
	uf.userId,
	uf.friendId,
	uf.applyType,
	u.realName,
	u.phone,
	d.org_id orgId,
	u.idHandleImgUrl,
	u.deptId companyId,
	u.headImgUrl
FROM
	v_user_friend uf
	LEFT JOIN v_dept_user u ON uf.friendId = u.id
	LEFT JOIN v_dept d on d.id=u.deptId
WHERE
	uf.userId = ? UNION ALL
SELECT
	uf.userId,
	uf.friendId,
	uf.applyType,
	u.realName,
	u.phone,
	d.org_id orgId,
	u.idHandleImgUrl,
	u.deptId companyId,
	u.headImgUrl
FROM
	v_user_friend uf
	LEFT JOIN v_dept_user u ON uf.userid = u.id
	LEFT JOIN v_dept d on d.id=u.deptId
WHERE
	uf.friendId = ?
	) x
GROUP BY
	realName,
	phone,
	companyId
#end


