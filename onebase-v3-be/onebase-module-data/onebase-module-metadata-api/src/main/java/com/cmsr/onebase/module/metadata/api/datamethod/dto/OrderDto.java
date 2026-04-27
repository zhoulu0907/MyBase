package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import lombok.Data;

@Data
public class OrderDto {
    /**
     * 排序字段ID
     */
    private String fieldId;
    /**
     * 排序顺序 asc/desc
     */
    private String sortOrder;

}
