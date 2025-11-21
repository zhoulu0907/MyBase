package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import org.dromara.warm.flow.core.entity.Instance;
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

    /**
     * 填充视图页面信息
     *
     * @param instance 流程实例
     */
    @Override
    protected void fillPageViewInfo(BpmTaskDetailRespVO vo, Instance instance, Long pageSetId, boolean isTodo) {
        PageViewGroupDTO viewGroupDTO = getPageViewGroupDTO(instance, pageSetId);

        // 发起节点比较特殊，会返回编辑视图
        if (isTodo) {
            vo.setPageView(viewGroupDTO.getEditPageView());
        } else {
            vo.setPageView(viewGroupDTO.getDetailPageView());
        }
    }

    @Override
    protected void fillButtonConfigs(BpmTaskDetailRespVO vo, InitiationNodeExtDTO extDTO) {
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

