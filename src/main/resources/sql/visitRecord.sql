#sql("check")
select id from  v_visitor_record  where userId = ? and visitorId =? and recordType = ?
 and cstatus<>'applyFail' and STR_TO_DATE(startDate,'%Y-%m-%d %H:%i')<STR_TO_DATE(?,'%Y-%m-%d %H:%i')
 and   STR_TO_DATE(endDate,'%Y-%m-%d %H:%i')>STR_TO_DATE(?,'%Y-%m-%d %H:%i')
#end

#sql("findRecordFromId")
select vr.*,realName,niceName,sex,idHandleImgUrl,headImgUrl,c.companyName,c.addr from v_visitor_record vr
left join v_app_user  u  on u.id=vr.visitorId
 left join v_company c on c.id=vr.companyId
 where vr.id=?
#end