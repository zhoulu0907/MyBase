package com.cmsr.onebase.framework.ds.model.common;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {

    private List<T> totalList;

    private Integer total;

    private Integer totalPage;

    private Integer pageSize;

    private Integer currentPage;

    private Integer pageNo;
}
