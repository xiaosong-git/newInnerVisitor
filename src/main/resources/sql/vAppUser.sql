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