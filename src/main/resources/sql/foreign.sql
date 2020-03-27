#sql("findOrgCode")
   select swi_code from  v_key k left join v_org o on k.org_id=o.id where swi_code =? and org_code = ?  and status='normal'
#end



