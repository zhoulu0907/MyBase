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
     * 组件标题
     */
    private String label;

    /**
     * 组件宽度
     */
    private Integer width;

    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * readOnly
     */
    private String readOnly;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 配置
     */
    private String config;

    /**
     * 编辑数据
     */
    private String editData;
}
