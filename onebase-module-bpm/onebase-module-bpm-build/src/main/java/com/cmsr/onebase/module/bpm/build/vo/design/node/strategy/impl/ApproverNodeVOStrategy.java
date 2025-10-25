package com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.ApproverNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.design.node.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 审批人节点配置策略实现
 *
 * @author liyang
 * @date 2025-10-23
 */
@Slf4j
@Component
public class ApproverNodeVOStrategy extends AbstractNodeVOStrategy<ApproverNodeVO, ApproverNodeExtDTO> {

    @Override
    public ApproverNodeVO createNodeVO() {
        return new ApproverNodeVO();
    }

    @Override
    public void doParseExtData(ApproverNodeVO nodeVO, String extData) {
        // 解析JSON字符串为审批人扩展数据结构
        ApproverNodeExtDTO nodeExtDTO = JsonUtils.parseObject(extData, ApproverNodeExtDTO.class);

        if (nodeExtDTO != null) {
            // 确保data对象存在
            if (nodeVO.getData() == null) {
                nodeVO.setData(new ApproverNodeVO.ApproverNodeDataVO());
            }

            // 设置审批人配置
            nodeVO.getData().setApproverConfig(nodeExtDTO.getApproverConfig());

            // 设置按钮配置
            nodeVO.getData().setButtonConfigs(nodeExtDTO.getButtonConfigs());

            // 设置字段权限配置
            nodeVO.getData().setFieldPermConfig(nodeExtDTO.getFieldPermConfig());

            log.debug("成功解析审批人节点扩展数据: {}", extData);
        }
    }

    @Override
    public ApproverNodeExtDTO buildExtData(ApproverNodeVO nodeVO) {
        ApproverNodeExtDTO extDTO = new ApproverNodeExtDTO();

        // 设置节点类型
        extDTO.setNodeType(getSupportedNodeType());

        // 设置审批人配置
        if (nodeVO.getData() != null) {
            extDTO.setApproverConfig(nodeVO.getData().getApproverConfig());
            extDTO.setButtonConfigs(nodeVO.getData().getButtonConfigs());
            extDTO.setFieldPermConfig(nodeVO.getData().getFieldPermConfig());
        }

        // 设置元数据
        extDTO.setMeta(nodeVO.getMeta());

        return extDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.APPROVER.getCode();
    }
}
