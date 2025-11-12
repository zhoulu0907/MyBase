package com.cmsr.onebase.module.bpm.build.vo.design.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.InitiationNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.design.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 发起节点配置策略实现
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
@Component
public class InitiationNodeVOStrategy extends AbstractNodeVOStrategy<InitiationNodeVO, InitiationNodeExtDTO> {

    @Override
    public InitiationNodeVO createNodeVO() {
        return new InitiationNodeVO();
    }

    @Override
    public void doParseExtData(InitiationNodeVO nodeVO, String extData) {
        // 解析JSON字符串为发起节点扩展数据结构
        InitiationNodeExtDTO extDataDTO = JsonUtils.parseObject(extData, InitiationNodeExtDTO.class);

        if (extDataDTO != null) {
            // 确保data对象存在
            if (nodeVO.getData() == null) {
                nodeVO.setData(new InitiationNodeVO.DataVO());
            }

            // 设置字段权限配置
            nodeVO.getData().setDeptConfig(extDataDTO.getDeptConfig());

            log.debug("成功解析发起节点扩展数据: {}", extData);
        }
    }

    @Override
    public InitiationNodeExtDTO buildExtData(InitiationNodeVO nodeVO, Long appId) {
        InitiationNodeExtDTO extDTO = new InitiationNodeExtDTO();
        // 设置节点类型
        extDTO.setNodeType(getSupportedNodeType());

        // 设置字段权限配置
        extDTO.setDeptConfig(nodeVO.getData().getDeptConfig());
        extDTO.setButtonConfigs(this.buildDefaultButtonConfigs());
        extDTO.setMeta(nodeVO.getMeta());

        return extDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.INITIATION.getCode();
    }
}
