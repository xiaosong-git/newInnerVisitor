/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: AccessService
 * Author:   Administrator
 * Date:     2021/1/25 0025 21:03
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.xiaosong.common.web.access;


import cn.hutool.core.date.DateUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.TblAccess;
import com.xiaosong.util.NumberUtil;
import com.xiaosong.util.RetUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

//import com.xiaosong.model.TblAccess;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Administrator
 * @create 2021/1/25 0025
 * @since 1.0.0
 */
public class AccessService {
    public static final AccessService me = new AccessService();
    private AccessDao accessDao = AccessDao.me;
    RetUtil updateAccess(TblAccess tblAccess) throws Exception{

        if (tblAccess.getId()==null){

            TblAccess byAccessCode = null;
            String accessCode;
            do {
                accessCode = NumberUtil.getRandomHexCode(4);

                byAccessCode = accessDao.getByAccessCode(accessCode, tblAccess.getOrgId());
            } while (byAccessCode != null);
            //todo 下发上位机

            //todo 修改所有用户区域
            tblAccess.setAccessCode(accessCode);
            tblAccess.setCreateTime(DateUtil.now());
            tblAccess.setUpdateTime(DateUtil.now());
            boolean save = tblAccess.save();
            return save?RetUtil.ok("新增成功"):RetUtil.fail("新增失败");
        }else{

            tblAccess.setUpdateTime(DateUtil.now());
            boolean update = tblAccess.update();
            return update?RetUtil.ok("修改成功"):RetUtil.fail("修改失败");
        }
    }
    public int bandOrgdeleteAccess(Long id, Long orgId,int status) {

       return Db.update("update tbl_access set status=? where id=? ",status, id);
    }
    List<TblAccess>  getAccessList(Long orgId, String name, Integer status) {
        StringBuilder sql = new StringBuilder("  from tbl_access");
        StringBuilder whereSql =new StringBuilder(" where status<>3 ");
        if(StringUtils.isNotBlank(name)) {
            whereSql.append(" and name like CONCAT('%','").append(name).append("','%')");
        }
        if (status!=null){
            whereSql.append(" and status =").append(status);
        }
        List<TblAccess> tblAccesses = TblAccess.dao.find("select *"+sql.append(whereSql).toString());
        return tblAccesses;
    }


    public List<TblAccess> getAccessDeptList(Long id) {
     return TblAccess.dao.find("select a.* from tbl_access a left join tbl_access_dept d on a.id=d.access_id where d.dept_id=? and d.status =1", id);
    }
}
