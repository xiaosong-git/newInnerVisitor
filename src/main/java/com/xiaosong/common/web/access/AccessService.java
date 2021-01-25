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
import com.xiaosong.model.TblAccess;
import com.xiaosong.util.RetUtil;

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

    RetUtil updateAccess(TblAccess tblAccess) throws Exception{

        if (tblAccess.getId()==null){

            tblAccess.setUpdateTime(DateUtil.now());
            boolean update = tblAccess.update();
            //todo 下发上位机

            //todo 修改所有用户区域

            return update?RetUtil.ok("修改成功"):RetUtil.fail("修改失败");
        }else{
            tblAccess.setCreateTime(DateUtil.now());
            tblAccess.setUpdateTime(DateUtil.now());
            boolean save = tblAccess.save();
            return save?RetUtil.ok("新增成功"):RetUtil.fail("新增失败");
        }
    }
}
