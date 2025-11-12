package com.cmsr.onebase.module.bpm.core.vo.design.node.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础节点配置视图
 *
 * @author liyang
 * @data 2025-10-21
 */
@Data
public class BaseNodeDataVO {
    /**
     * 节点状态，运行实例使用
     */
    @Schema(description = "节点状态")
    private String runStatus;
}
