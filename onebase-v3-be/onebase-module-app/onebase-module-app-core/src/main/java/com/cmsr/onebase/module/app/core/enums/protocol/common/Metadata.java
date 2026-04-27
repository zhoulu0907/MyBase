package com.cmsr.onebase.module.app.core.enums.protocol.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
     /**
     * name，资源唯一标识符（DNS-1123 格式）
     */
    private String name;

    /**
     * version，资源版本
     */
    private String version;

    /**
     * displayName，UI显示的友好名称
     */
    public String displayName;

    /**
     * description，资源详细描述
     */
    private String description;

    /**
     * labels，页面集合标签，使用键值对存储
     */
    private Map<String, Object> labels;

    /**
     * annotations，非标识性元数据（用于工具、第三方系统）
     */
    private Map<String, Object> annotations;
}
