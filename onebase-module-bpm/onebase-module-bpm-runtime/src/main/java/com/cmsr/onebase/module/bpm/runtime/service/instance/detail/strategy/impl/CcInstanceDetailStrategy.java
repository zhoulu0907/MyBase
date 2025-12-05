package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.CcNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import org.springframework.stereotype.Component;

/**
 * 审批节点流程实例详情策略
 *
 * @author liyang
 * @date 2025-11-04
 */
@Component
public class CcInstanceDetailStrategy extends AbstractInstanceDetailStrategy<CcNodeExtDTO> {

    @Override
    public boolean supports(String bizNodeType) {
        return BpmNodeTypeEnum.CC.getCode().equals(bizNodeType);
    }

    @Override
    protected void fillFieldPermConfig(BpmTaskDetailRespVO vo, CcNodeExtDTO extDTO, String tableName, boolean isTodo) {
        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();

        fillFieldPermFromConfig(vo, fieldPermConfig, tableName, isTodo);
    }
}

