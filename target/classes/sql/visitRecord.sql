#sql("check")
select id from  v_visitor_record  where userId = ? and visitorId =? and recordType = ?
 and cstatus<>'applyFail' and STR_TO_DATE(startDate,'%Y-%m-%d %H:%i')<STR_TO_DATE(?,'%Y-%m-%d %H:%i')
 and   STR_TO_DATE(endDate,'%Y-%m-%d %H:%i')>STR_TO_DATE(?,'%Y-%m-%d %H:%i')
#end