package com.xiaosong.common.api.car;

import com.xiaosong.common.api.userPost.UserPostService;
import com.xiaosong.model.VCar;
import com.xiaosong.model.VUserPost;
import com.xiaosong.model.VVisitorRecord;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by CNL on 2020/10/20.
 */
public class CarService {


    public static final CarService me = new CarService();

    public boolean addCarInfo(VVisitorRecord visitorRecord)
    {
        if(StringUtils.isNotBlank(visitorRecord.getPlate())) {
            VCar vCar = new VCar();
            vCar.setPlate(visitorRecord.getPlate());
            vCar.setCStatus("applyConfirm");
            vCar.setVisitDate(visitorRecord.getVisitDate());
            vCar.setVisitTime(visitorRecord.getVisitTime());
            vCar.setVisitId(visitorRecord.getId()!=null?visitorRecord.getId().longValue():null);
            return vCar.save();
        }else{
            return true;
        }
    }



}
