package com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.StartNodeExtDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.StartNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 开始节点配置策略实现
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
@Component
public class StartNodeVOStrategy extends AbstractNodeVOStrategy<StartNodeVO, StartNodeExtDTO> {

    @Override
    public StartNodeVO createNodeVO() {
        return new StartNodeVO();
    }

    @Override
    public void doParseExtData(StartNodeVO nodeVO, String extData) {
       // 开始节点没有扩展数据，无需解析
    }

    @Override
    public StartNodeExtDTO buildExtData(StartNodeVO nodeVO, Long appId) {
        StartNodeExtDTO extDTO = new StartNodeExtDTO();
        // 设置节点类型
        extDTO.setNodeType(getSupportedNodeType());
        extDTO.setMeta(nodeVO.getMeta());
        extDTO.setButtonConfigs(this.buildDefaultButtonConfigs());

        return extDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.START.getCode();
    }
}
