package com.cmsr.onebase.module.bpm.core.dto.node;

import com.cmsr.onebase.module.bpm.core.dto.node.base.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 审批人节点里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-24
 */
@Data
public class ApproverNodeExtDTO extends BaseNodeExtDTO {
    /**
     * 审批人配置
     */
    @Valid
    @NotNull(message = "审批人配置不能为空")
    private ApproverConfigDTO approverConfig;

    /**
     * 按钮配置
     */
    @Valid
    @NotNull(message = "按钮配置不能为空")
    private List<ApproverNodeBtnCfgDTO> buttonConfigs;

    /**
     * 字段权限配置
     */
    private FieldPermCfgDTO fieldPermConfig;

    /**
     * 高级配置
     */
    private AdvancedConfigDTO advancedConfig;

}
