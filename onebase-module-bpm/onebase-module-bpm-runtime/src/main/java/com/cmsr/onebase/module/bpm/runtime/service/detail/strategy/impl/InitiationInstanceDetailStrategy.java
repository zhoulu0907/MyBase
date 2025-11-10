package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmFlowTaskDetailVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 发起节点流程实例详情策略
 *
 * @author liyang
 * @date 2025-11-04
 */
@Component
public class InitiationInstanceDetailStrategy extends AbstractInstanceDetailStrategy<InitiationNodeExtDTO> {

    @Override
    public boolean supports(String bizNodeType) {
        return BpmNodeTypeEnum.INITIATION.getCode().equals(bizNodeType);
    }

    @Override
    protected void fillButtonConfigs(BpmFlowTaskDetailVO vo, InitiationNodeExtDTO extDTO) {
        for (BaseNodeBtnCfgDTO buttonConfig : extDTO.getButtonConfigs()) {
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

