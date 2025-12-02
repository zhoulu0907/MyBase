package com.cmsr.onebase.module.app.core.dto.appresource;

import lombok.Data;

@Data
public class ComponentDTO {
    private String componenetUuid;

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
     * 父组件编码
     */
    private String parentCode;

    /**
     * 块索引
     */
    private Integer blockIndex;

    /**
     * 容器索引
     */
    private Integer containerIndex;

    /**
     * 组件索引
     */
    private Integer componentIndex;
}
