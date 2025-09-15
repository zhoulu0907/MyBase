package com.cmsr.onebase.module.metadata.core.service.datamethod.vo;

import lombok.Data;

/**
 * @ClassName DataMethodQueryVO
 * @Description 数据方法查询VO，用于Service层参数封装
 * @Author mickey
 * @Date 2025/1/25 14:30
 */
@Data
public class DataMethodQueryVO {

    /**
     * 实体ID
     */
    private String entityId;

    /**
     * 方法类型
     */
    private String methodType;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 构造方法
     *
     * @param entityId 实体ID
     * @param methodType 方法类型
     * @param keyword 搜索关键词
     */
    public DataMethodQueryVO(String entityId, String methodType, String keyword) {
        this.entityId = entityId;
        this.methodType = methodType;
        this.keyword = keyword;
    }

    /**
     * 默认构造方法
     */
    public DataMethodQueryVO() {
    }
}
