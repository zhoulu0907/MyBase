package com.cmsr.onebase.module.bpm.api.dto.node;

import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import lombok.Data;

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
     * 部门配置
     */
    @Data
    public static class DeptConfigDTO {
        /**
         * 是否使用自定义配置
         */
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
