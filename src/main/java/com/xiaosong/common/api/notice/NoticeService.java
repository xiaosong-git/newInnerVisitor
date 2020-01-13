package com.xiaosong.common.api.notice;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.MyPage;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VCompany;
import com.xiaosong.model.VNews;
import com.xiaosong.model.VNotice;

/**
 * @description:首页公告
 * @author: cwf
 * @create: 2020-01-10 10:53
 **/
public class NoticeService {
    Log log = Log.getLog(NoticeService.class);
    public Result findNoticeByUser(String userId, Integer pageNum, Integer pageSize) {
//        Record appUser = Db.findById(TableList.APP_USER, userId);
        //String relationNo = BaseUtil.objToStr(user.get("relationNo"),null);
        //String sql = "  from "+TableList.NOTICE +" where relationNo like '%"+relationNo+"%' and castatus = 'normal' order by createDate desc ";
        String coloumSql="select *";
        String from = "  from " + TableList.NOTICE + " where  cstatus = 'normal'  ";
        String oderBy="order by createDate desc";
        String totalRowSql = "select count(*) " + from;
        System.out.println(coloumSql + from + oderBy);
        //jfinal模板用到ordeby时需要如此转换，具体看jfinal官网的回答
        Page<VNotice> records = VNotice.dao.paginateByFullSql(pageNum, pageSize, totalRowSql, coloumSql + from + oderBy);
        MyPage<VNotice> myPage= new MyPage(records.getList(),pageNum,pageSize,records.getTotalPage(),records.getTotalRow());
        return ResultData.dataResult("success", "获取成功", myPage);
    }
    public Result findBySidCompany(String userId, Integer pageNum, Integer pageSize) throws Exception {
        //获取用户信息
        VAppUser user = VAppUser.dao.findById(userId);
        //获取用户的公司Id
        if (user.get("companyId")==null){
            return ResultData.unDataResult("fail", "该员工缺少公司！");
        }
        //获取用户companyId
        Long companyId = user.getCompanyId();
        //获取用户公司信息
        VCompany company = VCompany.dao.findById(companyId);
        String coloumSql = "select * ";
        String from = "from ( select a.*,'company' orgType from " + TableList.NOTICE + "a where  cstatus = 'normal' and a.companyId=" + companyId + " ";
        String union = "";
        String companyUnion = "";
        String Suffix  = ")x";
        //获取tbl_user的orgId
        Long userOrgId = user.getOrgId();
        //用户有orgId，则添加根据orgId与sId进行搜索
        if (userOrgId !=  null) {
            union = union(userOrgId);
        }
        //获取用户公司的orgId
        if (company.getOrgId()!=null){
            companyUnion=union(company.getOrgId());
        }
        log.info("查询notice:: {}",from + union+companyUnion+Suffix);
        Page<VNotice> records = VNotice.dao.paginate(pageNum, pageSize, coloumSql , from + union+companyUnion+Suffix );
        MyPage<VNotice> myPage= new MyPage(records.getList(),pageNum,pageSize,records.getTotalPage(),records.getTotalRow());
        return myPage.getTotalPage()==0?ResultData.dataResult("success", "获取成功,暂无数据", myPage):
                ResultData.dataResult("success", "获取成功", myPage);
    }

    public String union(Object orgId){
        String union = "union \n" +
                "select b.*,c.orgType from tbl_notice b\n" +
                "\tleft join t_org c on c.id=b.orgId\twhere b.cstatus = 'normal' and b.orgId in\n" +
                "\t\t(select t2.id\n" +
                "\t\t\tfrom\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\tselect @r AS _id,\n" +
                "        (SELECT @r := sid FROM t_org WHERE id = _id) AS sid,\n" +
                "        @l := @l + 1 AS lvl\n" +
                "\t\t\t\tfrom\n" +
                "\t\t\t\t(select @r := "+orgId+", @l := 0) vars,\n" +
                "\t\t\t\tt_org h\n" +
                "    where @r <> 0\n" +
                "\t\t) t1\n" +
                "join t_org t2 ON t1._id = t2.id\n" +
                "order BY t1.lvl desc)";
        return union;
    }
    //新闻接口
    public Result findByStatus(String status, Integer pageNum, Integer pageSize) throws Exception {
        String coloumSql="select *";
        String from = "  from "+ TableList.NEWS + " where  newsStatus = '"+status+"' ";
        String totalRowSql = "select count(*) " + from;
        String orderBy="order by newsDate desc";
        Page<VNews> records = VNews.dao.paginateByFullSql(pageNum, pageSize, totalRowSql , coloumSql+from+orderBy);
        MyPage<VNews> myPage= new MyPage(records.getList(),pageNum,pageSize,records.getTotalPage(),records.getTotalRow());

        return ResultData.dataResult("success","获取成功",myPage);
    }
}
