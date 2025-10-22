package com.cmsr.onebase.module.bpm.api.dto.node.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 字段权限配置
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "字段权限配置")
public class FieldPermCfgDTO {
    /**
     * 是否使用节点配置
     */
    private Boolean useNodeConfig;

    /**
     * 字段配置列表
     */
    private List<FieldConfigDTO> fieldConfigs;

    @Schema(description = "字段配置视图")
    @Data
    public static class FieldConfigDTO {
        /**
         * 字段ID
         */
        private String fieldId;

        /**
         * 字段名
         */
        private String fieldName;

        /**
         * 字段权限类型
         *
         * 可编辑 只读 隐藏
         *
         */
        private String fieldPermType;
    }
}