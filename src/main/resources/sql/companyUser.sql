#sql("findApplySucByOrg")
	select cu.id,cu.companyId,cu.sectionId,cu.userId,cu.userName,cu.createDate,cu.createTime, 
                cu.roleType,cu.status,cu.currentStatus,cu.postId,u.idHandleImgUrl idHandleImgUrl,u.idType idType, 
                u.idNO idNO,c.companyFloor companyFloor,u.phone ;
        String fromSql =  from   v_company_USER   cu  
                 left join   v_app_user   u on cu.userId=u.id 
                 left join   v_company   c on cu.companyId=c.id 
                 left join   v_company_section   cs on cu.sectionId=cs.id 
                 join   v_org  og on c.orgid=og.id
                 left join   v_dict_item   d on d.dict_code='companyUserRoleType' and d.item_code=cu.roleType  
                 left join   v_dict_item   i on i.dict_code='companyUserStatus' and i.item_code=cu.status  
                 where og.org_code = #p(org_code) and cu.status = 'applySuc' and (u.authDate = #p(create_date) or 
                 cu.createDate=#p(create_date)) and u.isAuth = 'T' 
                 UNION all  
                 select null id ,u.companyid  ,null sectionId, userId, userName, ovu.createDate,ovu.createtime, roleType,status, 
                currentStatus, postId,u.idHandleImgUrl,u.idType ,u.idno idNO,null companyFloor,u.phone  
                 from v_comp_vip_user ovu 
                left join v_app_user u on ovu.userId=u.id 
                left join v_org org on org.id=ovu.orgId    
                where org.org_code=#p(org_code)  and u.isAuth = 'T' and DATE_FORMAT(ovu.createDate, '%Y-%m-%d') = #p(create_date)
#end
#sql("findValueByNameFromDB")
	select * from v_params where paramName =#p(imageServerUrl)
#end
#sql("findApplyAllSucByOrg")
	select cu.id,cu.companyId,cu.sectionId,cu.userId,cu.userName,cu.createDate,cu.createTime, 
                cu.roleType,cu.status,cu.currentStatus,cu.postId,u.idHandleImgUrl idHandleImgUrl,u.idType idType, 
                u.idNO idNO,c.companyFloor companyFloor,u.phone
        from   v_company_USER   cu  
                 left join   v_app_user   u on cu.userId=u.id 
                 left join   v_company   c on cu.companyId=c.id 
                 left join   v_company_section   cs on cu.sectionId=cs.id 
                 join   v_org  og on c.orgid=og.id
                 left join   v_dict_item   d on d.dict_code='companyUserRoleType' and d.item_code=cu.roleType  
                 left join   v_dict_item   i on i.dict_code='companyUserStatus' and i.item_code=cu.status  
                 where og.org_code = 'org_code' and cu.status = 'applySuc'  and u.isAuth = 'T' 
                
                 UNION all  
                 select null id ,u.companyid  ,null sectionId, userId, userName, ovu.createDate,ovu.createtime, roleType,status, 
                currentStatus, postId,u.idHandleImgUrl,u.idType ,u.idno idNO,null companyFloor,u.phone  
                 from v_comp_vip_user ovu 
                left join v_app_user u on ovu.userId=u.id  
                left join v_org org on org.id=ovu.orgId    
                where org.org_code='org_code'  and u.isAuth = 'T'
#end