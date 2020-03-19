package com.xiaosong.common.imgServer.inAndOut;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.stat.ast.If;
import com.jfinal.upload.UploadFile;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.xiaosong.MainConfig;
import com.xiaosong.common.imgServer.img.ImageService;
import com.xiaosong.compose.Result;
import com.xiaosong.compose.ResultData;
import com.xiaosong.constant.TableList;
import com.xiaosong.util.BaseUtil;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @program: jfinal_demo_for_maven
 * @description: 进出日志接口
 * @author: cwf
 * @create: 2020-01-08 17:32
 **/
public class InAndOutService {
    public static final InAndOutService me = new InAndOutService();
    private static ExecutorService exec = new ThreadPoolExecutor(1
            , 300
            , 0L
            , TimeUnit.MILLISECONDS
            , new LinkedBlockingQueue(1024)
            , new ThreadFactory(){ @Override public Thread newThread(Runnable r) { return new Thread(r); }}
            , new ThreadPoolExecutor.AbortPolicy());

    Log log=Log.getLog(InAndOutService.class);
    public Result save(UploadFile file, String orgCode, String pospCode, String sign) throws Exception {
        if (orgCode==null||pospCode==null||sign==null){
            return Result.unDataResult("fail","参数缺失");
        }
        String str = Db.queryStr("select pospCode from " + TableList.POSP + " p" +
                " left join " + TableList.ORG + " o on p.orgId =o.id where org_code='" + orgCode +
                "' and pospCode='" + pospCode + "'");
        if (str==null){
            return Result.unDataResult("fail","没有此上位机编码或没有此大楼编码");
        }
        String newPath= MainConfig.p.get("inOutDir");
        //上传txt格式验证
        ResultData result = (ResultData)ImageService.me.uploadTxt(file, null, newPath);

        if ("success".equals(result.getVerify().get("sign"))){
            Object data = result.getData();
           Map<String,Object> map = JSONObject.parseObject(JSONObject.toJSONString(data), Map.class);
            //新文件名称
           String  realFileName = BaseUtil.objToStr(map.get("imageFileName"),"无");
            exec .submit(() -> {
                //调用你的 业务代码
                try {
                    test(MainConfig.p.get("inOutDir")+realFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        return result;
//
//            //开启线程进行插入数据库
//            final String finalPath = path;
//            taskExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("开启线程");
//                    try {
//                        int count = fileService.batchUpdate(finalPath, TableList.IN_OUT, "null,null,null");
//                        System.out.println("插入数据数？"+count);
//                        if (count>0){
////                            File file = new File(finalPath);
////                            boolean delete = file.delete();
//                            log.info(finalPath+"插入通行日志总数："+count);
////                            log.info("临时文件删除成功？"+delete);
//                            log.info("没有删除？");
//                        }
//                        System.out.println(count);
//                        Thread.sleep(1000);
//                        Thread.interrupted();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            long end1=System.currentTimeMillis();
//            System.out.println(end1-end);
//            myResult=MyResult.unDataResult("success","上传成功");
//            return ;
//        }catch (Exception e){
//            e.printStackTrace();
//            myResult =MyResult.unDataResult("fail","系统错误，请联系管理员");
//            return ;
//        }finally {
//            System.out.println("有返回");
//            json = JsonUtils.toJson(myResult);
//            System.out.println("jswon:" + json);
//            ResponseUtil.responseJson(response, json);
        }
        //批量插入
//        public int batchUpdate(String path,String table,String suffix) throws Exception {
//            StringBuffer strRead = new StringBuffer("insert into " + table + " values");
//            //读取文本行数
//            int count = 0;
//            //每次插入数量为5000
//            int size = 5000;
//            //插入次数
//            int inTimes = 1;
//            String str = null;
//            //批量插入返回值
//            int[] ints = null;
//            //插入数据库条数
//            int inCount = 0;
//            InputStreamReader is = null;
//            BufferedReader reader = null;
//            try {
//                is = new InputStreamReader(new FileInputStream(path), "UTF-8");
//                reader = new BufferedReader(is);
//
//                while (true) {
//                    str = reader.readLine();
//                    if (str != null) {
//                        strRead.append("(0,'").append(str.replace("|", "','")).append("'," + suffix + "),");
//                        count++;
//                        //插入数大于5000分批插入
//                        //如果总插入条数除以每次插入条数大于等于插入次数，说明总还有数据未插入数据库
//                        if (count / size >= inTimes) {
//                            inTimes++;
//                            ints = Db.batch().batchUpdate(strRead.substring(0, strRead.length() - 1));
//                            strRead = new StringBuffer("insert into " + table + " values");
//                            inCount += ints[0];
//                        }
//                    } else {
//                        //如果插入数小于5000的方法
//                        if (count < size) {
//                            ints = baseDao.batchUpdate(strRead.substring(0, strRead.length() - 1));
//                            inCount += ints[0];
//                        }
//                        break;
//                    }
//
//                }
//                return count;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return 0;
//            } finally {
//                //读取文本条数>插入条数时，最后执行一次批量插入
//                if(count>inCount){
//                    baseDao.batchUpdate(strRead.substring(0, strRead.length() - 1));
//                }
//                if (is != null) {
//                    is.close();
//                }
//                if (reader!=null){
//                    reader.close();
//                }
//            }
//        }
//
//    }
        public Result test(String path) throws IOException {
            System.out.println("启动线程");
            long start=System.currentTimeMillis();
            String columns="orgCode,pospCode,scanDate,scanTime,inOrOut,outNumber,deviceType,deviceIp,userType,userName,idCard";
            String sql="insert into "+TableList.INOUT+"("+columns+") values(?,?,?,?,?,?,?,?,?,?,?)";
            BufferedReader reader = null;
            List< Record> records=new LinkedList<>();
            Record record=new Record();
            InputStreamReader  is = new InputStreamReader(new FileInputStream(path), "UTF-8");
            String str = null;
            reader = new BufferedReader(is);
            List<String> errorString=new LinkedList<>();
                while (true) {
                    str = reader.readLine();
                    if(str!=null) {
                        String[] strSplit = str.split("[|]");
                        String[] columnsSplit = columns.split(",");
                        if (strSplit.length != columnsSplit.length) {
                            errorString.add(str);
                            continue;
                        }
                        for (int i = 0; i < strSplit.length; i++) {
                            record.set(columnsSplit[i], strSplit[i]);
                        }
                        records.add(record);
                    }else {
                        break;
                    }
                }
            if (errorString.size()>0){
                log.error("进出日志数据有误{}",errorString);
            }
            int[] batch = Db.batch(sql, columns, records, 50000);
            log.info("上传数据库成功",batch[0]);
            long end=System.currentTimeMillis();
            log.info("上传时间",end-start);
            return ResultData.dataResult("success","上传成功",batch[0]);
        }
}
