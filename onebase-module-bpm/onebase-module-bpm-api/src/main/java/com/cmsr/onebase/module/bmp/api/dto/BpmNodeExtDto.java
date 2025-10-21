package com.cmsr.onebase.module.bmp.api.dto;

import lombok.Data;

/**
 * 流程节点表里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-21
 */
@Data
public class BpmNodeExtDto {
    /**
     * 流程类型
     */
    private String nodeType;
}
