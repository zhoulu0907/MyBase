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
     * 实体UUID（优先使用）
     */
    private String entityUuid;

    /**
     * 实体ID（兼容旧版）
     * @deprecated 请使用 entityUuid
     */
    @Deprecated
    private Long entityId;

    /**
     * 方法类型
     */
    private String methodType;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 构造方法（仅使用entityUuid）
     */
    public DataMethodQueryVO(String entityUuid, String methodType, String keyword) {
        this.entityUuid = entityUuid;
        this.methodType = methodType;
        this.keyword = keyword;
    }
}
