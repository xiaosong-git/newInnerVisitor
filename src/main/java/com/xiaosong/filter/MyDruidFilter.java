package com.xiaosong.filter;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.jfinal.kit.StrKit;
import com.xiaosong.common.user.UserController;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Map;

/**
 * @program: jfinal_demo_for_maven
 * @description:
 * @author: cwf
 * @create: 2019-12-31 14:11
 **/
public class MyDruidFilter extends FilterAdapter {
    Logger logger = Logger.getLogger(UserController.class);
    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);
        Map<Integer, JdbcParameter> lParameters = statement.getParameters();
        String lSql = statement.getBatchSql();
        if(StrKit.notBlank(lSql)){
            for (Map.Entry<Integer,JdbcParameter> lEntry : lParameters.entrySet()){
                JdbcParameter lValue = lEntry.getValue();
                if(lValue == null){
                    continue;
                }
                Object lO = lValue.getValue();
                if(lO == null){
                    continue;
                }
                String lS = lO.toString();
                lSql = lSql.replaceFirst("\\?",lS);
            }
            logger.info(lSql);
        }
    }
}
