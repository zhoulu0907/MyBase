package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 审批节点流程实例详情策略
 *
 * @author liyang
 * @date 2025-11-04
 */
@Component
public class ApproverInstanceDetailStrategy extends AbstractInstanceDetailStrategy<ApproverNodeExtDTO> {

    @Override
    public boolean supports(String bizNodeType) {
        return BpmNodeTypeEnum.APPROVER.getCode().equals(bizNodeType);
    }

    @Override
    protected void fillFieldPermConfig(BpmTaskDetailRespVO vo, ApproverNodeExtDTO extDTO, Long entityId, boolean isTodo) {
        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();

        fillFieldPermFromConfig(vo, fieldPermConfig, entityId, isTodo);
    }

    @Override
    protected void fillButtonConfigs(BpmTaskDetailRespVO vo, ApproverNodeExtDTO extDTO) {
        for (ApproverNodeBtnCfgDTO buttonConfig : extDTO.getButtonConfigs()) {
            if (!buttonConfig.getEnabled()) {
               continue;
            }

            if (vo.getButtonConfigs() == null) {
                vo.setButtonConfigs(new ArrayList<>());
            }

            vo.getButtonConfigs().add(buttonConfig);
        }
    }
}

