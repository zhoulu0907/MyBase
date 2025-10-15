package com.cmsr.onebase.framework.remote.model;

import java.util.List;

/**
 * 分页信息
 *
 * @param <T> 列表元素类型
 * @author matianyu
 * @date 2025-10-15
 */
public class PageInfo<T> {

    private int total;
    private int totalPage;
    private int pageNo;
    private int pageSize;
    private List<T> totalList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getTotalList() {
        return totalList;
    }

    public void setTotalList(List<T> totalList) {
        this.totalList = totalList;
    }
}
