#sql("find")
	select * from v_dictionaries where dictionaries_key=#p(dictionariesKey)
#end

#sql("findUser")
	select * from v_app_user
#end