package com.cmsr.onebase.module.bpm.api.dto.node;

import com.cmsr.onebase.module.bpm.api.dto.node.base.ApproverConfigDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
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
    private ApproverConfigDTO approverConfig;

    /**
     * 按钮配置
     */
    private List<ApproverNodeBtnCfgDTO> buttonConfigs;

    /**
     * 字段权限配置
     */
    private FieldPermCfgDTO fieldPermConfig;
}
