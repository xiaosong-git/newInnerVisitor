package com.xiaosong.constant;

import java.util.List;

/**
 * @program: XiaoSong
 * @description: 转化为我的分页
 * @author: cwf
 * @create: 2020-01-05 20:27
 **/
public class MyPage<T>{

    private int pageNum = 1; //第几页
    private int pageSize =10;//一页几条
    private List<T> rows ;       //数据
    private int totalPage;//总页数
    private int total;//总记录数
    public MyPage(List<T> rows, int pageNum, int pageSize, int totalPage, int total) {
        this.rows = rows;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.total = total;
    }

    public MyPage() {
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
