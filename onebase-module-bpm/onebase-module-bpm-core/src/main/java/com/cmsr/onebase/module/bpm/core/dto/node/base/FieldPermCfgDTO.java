package com.cmsr.onebase.module.bpm.core.dto.node.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "是否使用节点配置不能为空")
    private Boolean useNodeConfig = false;

    /**
     * 字段配置列表
     */
    @Valid
    private List<FieldConfigDTO> fieldConfigs;

    @Schema(description = "字段配置视图")
    @Data
    public static class FieldConfigDTO {
        /**
         * 字段ID
         */
        @NotNull(message = "字段UUID不能为空")
        private String fieldUuid;

        /**
         * 字段名
         */
        @NotBlank(message = "字段名不能为空")
        private String fieldName;

        /**
         * 字段权限类型
         *
         * 可编辑 只读 隐藏
         *
         */
        @NotBlank(message = "字段权限类型不能为空")
        private String fieldPermType;
    }
}