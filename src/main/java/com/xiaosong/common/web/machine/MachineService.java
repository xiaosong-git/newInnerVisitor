package com.xiaosong.common.web.machine;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xiaosong.model.VMachine;

import java.util.List;

/**
 * Created by CNL on 2020/11/26.
 */
public class MachineService {

    public static final	MachineService me = new MachineService();

    public List<VMachine> getMachineList()
    {
       return   VMachine.dao.find("select a.machine_code,a.machine_name,b.org_name from v_machine a left join v_org b on a.org_code = b.org_code ");
    }

}
