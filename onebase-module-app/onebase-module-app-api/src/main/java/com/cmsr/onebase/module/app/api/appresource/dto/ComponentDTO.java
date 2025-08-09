package com.cmsr.onebase.module.app.api.appresource.dto;

import lombok.Data;

@Data
public class ComponentDTO {
    /**
     * 组件编码
     */
    private String componentCode;

    /**
     * 组件类型
     */
    private String componentType;

    /**
     * 配置
     */
    private String config;

    /**
     * 编辑数据
     */
    private String editData;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 块索引
     */
    private Integer blockIndex;

    /**
     * 容器索引
     */
    private Integer containerIndex;
}
