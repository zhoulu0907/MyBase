package com.cmsr.onebase.framework.remote.dto;

import lombok.Data;

import java.util.List;

/** 通用分页 DTO */
@Data
public class PageInfoDTO<T> {
    private int total;
    private int totalList;
    private int pageSize;
    private int pageNo;
    private List<T> totalListData;
    private List<T> records;
}

