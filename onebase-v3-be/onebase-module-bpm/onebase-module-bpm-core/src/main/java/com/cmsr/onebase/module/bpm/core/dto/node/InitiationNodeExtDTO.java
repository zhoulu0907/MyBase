package com.cmsr.onebase.module.bpm.core.dto.node;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 发起节点里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-24
 */
@Data
public class InitiationNodeExtDTO extends BaseNodeExtDTO {
    /**
     * 字段权限配置
     */
    private DeptConfigDTO deptConfig;

    /**
     * 按钮配置，前端暂不可配置，默认有保存和提交按钮
     */
    private List<BaseNodeBtnCfgDTO> buttonConfigs;

    /**
     * 部门配置
     */
    @Data
    public static class DeptConfigDTO {
        /**
         * 是否使用自定义配置
         */
        @NotBlank(message = "是否使用自定义配置不能为空")
        private Boolean useCustomDept;

        /**
         * 部门ID
         */
        private Long deptId;

        /**
         * 部门名称
         */
        private String deptName;
    }
}
