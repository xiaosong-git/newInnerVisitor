package com.xiaosong.util;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
public class MenuUtil {
	public static List<Record> getTreeList(List<Record> entityList) {
        List<Record> resultList = new ArrayList<>();

        //获取顶层元素集合
        String parentCode;
        for (Record entity : entityList) {
            parentCode = entity.getStr("parentId");
            //顶层元素的parentCode==null或者为0
            if (parentCode == null || "0".equals(parentCode)) {
                resultList.add(entity);
            }
        }

        //获取每个顶层元素的子数据集合
        for (Record entity : resultList) {
        	entity.set("subs", getSubList(entity.getStr("id"), entityList));
        }

        return resultList;
    }

    /**
     * 获取子数据集合
     *
     * @param id
     * @param entityList
     * @return
     * @author jianda
     * @date 2017年5月29日
     */
    private static List<Record> getSubList(String id, List<Record> entityList) {
        List<Record> childList = new ArrayList<>();
        String parentId;

        //子集的直接子对象
        for (Record entity : entityList) {
            parentId = entity.getStr("parentId");
            if (id!=null && id.equals(parentId)) {
                childList.add(entity);
            }
        }

        //子集的间接子对象
        for (Record entity : childList) {
        	entity.set("subs", getSubList(entity.getStr("id"), entityList));
        }

        //递归退出条件
        if (childList.size() == 0) {
            return null;
        }

        return childList;
    }

}
