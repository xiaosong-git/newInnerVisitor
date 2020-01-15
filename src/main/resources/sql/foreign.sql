#sql("findOrgCode")
   select pospCode from  v_posp p left join v_org o on p.orgId=o.id where pospCode =? and orgCode = ?  and cstatus='normal'
#end



