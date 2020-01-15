#sql("findByPhone")
    select * from v_app_user where phone=#p(0)
#end
#sql("findId")
    select id from v_app_user where phone=?
#end
#sql("findIdName")
    select id from v_app_user where phone=? and realName=?
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
  select uf.id ufId,u.id,u.realName,u.phone,u.orgId,u.province,u.city,u.area,u.addr,u.idHandleImgUrl,u.companyId,u.niceName,u.headImgUrl,uf.remark,c.companyName,
  (select fuf.applyType from v_user_friend fuf where fuf.userId=uf.friendId and fuf.friendId="+userId+") applyType
   from  v_user_friend uf
   left join v_app_user u on uf.friendId=u.id
   left join v_company c on c.id=u.companyId
   where uf.userId = ? and uf.applyType=1
#end


