package com.cmsr.onebase.module.bpm.build.vo.design.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.RoleDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.UserDTO;
import com.cmsr.onebase.module.bpm.core.enums.ApproverTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.core.vo.design.node.ApproverNodeVO;
import com.cmsr.onebase.module.bpm.build.vo.design.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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

            // 设置高级配置
            nodeVO.getData().setAdvancedConfig(nodeExtDTO.getAdvancedConfig());

            log.debug("成功解析审批人节点扩展数据: {}", extData);
        }
    }

    @Override
    public ApproverNodeExtDTO buildExtData(ApproverNodeVO nodeVO, Long appId) {
        ApproverNodeExtDTO extDTO = new ApproverNodeExtDTO();

        // 设置节点类型
        extDTO.setNodeType(getSupportedNodeType());

        // 设置审批人配置
        ApproverNodeVO.ApproverNodeDataVO dataVO = nodeVO.getData();

        // 校验审批人配置
        validateApproverConfig(dataVO.getApproverConfig(), appId);

        // 校验按钮配置
        validateButtonConfigs(dataVO.getButtonConfigs());

        // 去除提交按钮
        dataVO.getButtonConfigs().removeIf(buttonConfig -> BpmActionButtonEnum.SUBMIT.getCode().equals(buttonConfig.getButtonType()));

        extDTO.setApproverConfig(nodeVO.getData().getApproverConfig());
        extDTO.setButtonConfigs(nodeVO.getData().getButtonConfigs());
        extDTO.setFieldPermConfig(nodeVO.getData().getFieldPermConfig());
        extDTO.setAdvancedConfig(nodeVO.getData().getAdvancedConfig());

        // 设置元数据
        extDTO.setMeta(nodeVO.getMeta());

        return extDTO;
    }

    @Override
    public NodePermFlagDTO buildPermissionFlag(ApproverNodeExtDTO extDTO) {
        NodePermFlagDTO nodePermTagDTO = new NodePermFlagDTO();
        ApproverConfigDTO approverConfig = extDTO.getApproverConfig();

        String approverType = approverConfig.getApproverType();
        ApproverTypeEnum approverTypeEnum = ApproverTypeEnum.getByCode(approverType);

        if (approverTypeEnum == ApproverTypeEnum.USER) {
            for (UserDTO user : approverConfig.getUsers()) {
                if (nodePermTagDTO.getUserIds() == null) {
                    nodePermTagDTO.setUserIds(new ArrayList<>());
                }

                nodePermTagDTO.getUserIds().add(user.getUserId());
            }
        } else if (approverTypeEnum == ApproverTypeEnum.ROLE) {
            for (RoleDTO role : approverConfig.getRoles()) {
                if (nodePermTagDTO.getRoleIds() == null) {
                    nodePermTagDTO.setRoleIds(new ArrayList<>());
                }

                nodePermTagDTO.getRoleIds().add(role.getRoleId());
            }
        }

        return nodePermTagDTO;
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.APPROVER.getCode();
    }
}
