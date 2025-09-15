package com.cmsr.onebase.module.metadata.build.service.datamethod.vo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 数据方法查询 VO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMethodQueryVO {

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 方法类型
     */
    private String methodType;

    /**
     * 关键词
     */
    private String keyword;
}
