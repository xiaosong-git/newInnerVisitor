package com.xiaosong.common.user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xiaosong.common.code.CodeService;
import com.xiaosong.common.compose.Result;
import com.xiaosong.common.password.PasswordService;
import com.xiaosong.constant.Status;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VAppUser;
import com.xiaosong.model.VAppUserAccount;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.ConsantCode;
import org.apache.log4j.Logger;

/**
 * @program: innerVisitor
 * @description: 用户接口
 * @author: cwf
 * @create: 2019-12-27 16:33
 **/
public class UserService {
    public static final UserService me = new UserService();
    Logger logger = Logger.getLogger(UserService.class);

    //密码登入
    public Result login(VAppUser appUser,String sysPwd,String style) throws Exception {
        SqlPara para = Db.getSqlPara("vAppUser.findByPhone", appUser.getPhone());//根据手机查找用户
        VAppUser user = VAppUser.dao.findFirst(para);
        if(user == null){
            return  Result.unDataResult("fail","用户不存在");
        }
        //判断密码输入次数是否超出限制，超出无法登录
        if(PasswordService.me.isErrInputOutOfLimit(user.getId(),Status.PWD_TYPE_SYS)){
            String limitTime = ParamService.me.findValueByName("errorInputSyspwdWaitTime");
            CodeService.me.sendMsg(user.getLoginName(), 2,null,null,null,null);
            return  Result.unDataResult("fail","由于您多次输入错误密码，为保证您的账户与资金安全，"+limitTime+"分钟内无法登录");
        }
        VAppUserAccount userAccount = VAppUserAccount.dao.findFirst("select * from " + TableList.USER_ACCOUNT + " where userId=?", user.getId());
        if(userAccount == null){
            return  Result.unDataResult(ConsantCode.FAIL,"未查询到相关账户信息");
        }
        //style为空默认选择：密码登录
        String dbPassword = style==null?userAccount.getSysPwd():userAccount.getGesturePwd();
        if(sysPwd.equals(dbPassword)) {
           //重置允许用户输入错误密码次数
            PasswordService.me.resetPwdInputNum(user.getId(), Status.PWD_TYPE_SYS);
            if ("normal".equals(userAccount.getCstatus())) {
                return UserUtil.me.loginSave(user, appUser);
            }else{
                String handleCause = userAccount.getHandleCause();
                return  Result.unDataResult("fail",handleCause);
            }
        }else {
            Long leftInputNum =PasswordService.me.addErrInputNum(user.getId(),Status.PWD_TYPE_SYS);
            return  Result.unDataResult("fail","密码错误:剩余" + leftInputNum + "次输入机会");
        }
    }
    //验证码登入
    public Result loginByVerifyCode(VAppUser appUser, String code) throws Exception {
        SqlPara para = Db.getSqlPara("vAppUser.findByPhone", appUser.getPhone());//根据手机查找用户
        VAppUser user = VAppUser.dao.findFirst(para);
        if (user==null){
            return Result.unDataResult(ConsantCode.FAIL,"用户不存在");
        }
        VAppUserAccount userAccount = VAppUserAccount.dao.findFirst("select * from " + TableList.USER_ACCOUNT + " where userId=?", user.getId());
        if(userAccount == null){
            return  Result.unDataResult(ConsantCode.FAIL,"未查询到相关账户信息");
        }
        //用户状态为normal
        if("normal".equals(userAccount.getCstatus())) {
            /**
             * 2,验证短信验证码
             */
            if (CodeService.me.verifyCode(appUser.getPhone(), code, 1)) {
                return UserUtil.me.loginSave(user, appUser);
            }else{
                return  Result.unDataResult(ConsantCode.FAIL,"验证码输入错误，请重新输入!");
            }
        }else{
            //返回账户冻结原因
            String handleCause = userAccount.getHandleCause();
            return  Result.unDataResult(ConsantCode.FAIL, handleCause);
            }
    }


}
