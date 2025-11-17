package com.cmsr.onebase.module.bpm.build.vo.design.strategy.impl;

import com.cmsr.onebase.module.bpm.build.vo.design.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.dto.node.CondNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.vo.design.node.CondNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 条件节点配置策略实现
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
@Component
public class CondNodeVOStrategy extends AbstractNodeVOStrategy<CondNodeVO, CondNodeExtDTO> {

    @Override
    public CondNodeVO createNodeVO() {
        return new CondNodeVO();
    }

    @Override
    public void doParseExtData(CondNodeVO nodeVO, String extData) {
        // 结束节点没有扩展数据，无需解析
    }

    @Override
    public CondNodeExtDTO buildExtData(CondNodeVO nodeVO, Long appId) {
        CondNodeExtDTO extDTO = new CondNodeExtDTO();
        // 设置节点类型
        extDTO.setNodeType(nodeVO.getType());
        extDTO.setMeta(nodeVO.getMeta());
        return extDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.CONDITION.getCode();
    }
}
