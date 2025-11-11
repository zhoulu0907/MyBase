package com.cmsr.onebase.module.bpm.core.dto;

import lombok.Data;

/**
 * 流程定义表里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-21
 */
@Data
public class BpmDefinitionExtDTO {
     /**
      * 流程版本备注
      */
    private String versionAlias;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 流程定义JSON，完整存储前端定义的JSON数据
     */
    private String bpmDefJson;

    /**
     * 全局配置
     */
    private BpmGlobalConfigDTO globalConfig;
}
