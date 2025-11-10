package com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.impl;

import com.cmsr.onebase.module.bpm.core.dto.node.EndNodeExtDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.EndNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结束节点配置策略实现
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
@Component
public class EndNodeVOStrategy extends AbstractNodeVOStrategy<EndNodeVO, EndNodeExtDTO> {

    @Override
    public EndNodeVO createNodeVO() {
        return new EndNodeVO();
    }

    @Override
    public void doParseExtData(EndNodeVO nodeVO, String extData) {
        // 结束节点没有扩展数据，无需解析
    }

    @Override
    public EndNodeExtDTO buildExtData(EndNodeVO nodeVO, Long appId) {
        EndNodeExtDTO extDTO = new EndNodeExtDTO();
        // 设置节点类型
        extDTO.setNodeType(nodeVO.getType());
        extDTO.setMeta(nodeVO.getMeta());
        return extDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.END.getCode();
    }
}
