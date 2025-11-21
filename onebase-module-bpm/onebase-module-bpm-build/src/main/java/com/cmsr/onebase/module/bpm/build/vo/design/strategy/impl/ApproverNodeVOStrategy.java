package com.cmsr.onebase.module.bpm.build.vo.design.strategy.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.build.vo.design.strategy.AbstractNodeVOStrategy;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverConfigDTO;
import com.cmsr.onebase.module.bpm.core.enums.ApprovalModeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.vo.design.node.ApproverNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

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

    private void fillHandlerType(ApproverConfigDTO approverConfig) {
        // 兼容旧版本 todo： remove
        String handlerType = approverConfig.getHandlerType();

        if (StringUtils.isBlank(handlerType)) {
            handlerType = approverConfig.getApproverType();
        }

        approverConfig.setApproverType(handlerType);
        approverConfig.setHandlerType(handlerType);
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

            // 兼容旧版本 todo： remove
            fillHandlerType(nodeVO.getData().getApproverConfig());

            // 设置按钮配置
            nodeVO.getData().setButtonConfigs(nodeExtDTO.getButtonConfigs());

            // 设置字段权限配置
            nodeVO.getData().setFieldPermConfig(nodeExtDTO.getFieldPermConfig());

            // 设置高级配置
            nodeVO.getData().setAdvancedConfig(nodeExtDTO.getAdvancedConfig());

            log.debug("成功解析审批人节点扩展数据: {}", extData);
        }
    }
    private void checkHandlerType(ApproverConfigDTO approverConfig)    {
        String handlerType = approverConfig.getHandlerType();

        // 兼容旧版本，todo 删除
        if (StringUtils.isBlank(handlerType)) {
            handlerType = approverConfig.getApproverType();
            approverConfig.setHandlerType(handlerType);
            approverConfig.setApproverType(null);
        }
    }

    private void validateApproverConfig(ApproverConfigDTO approverConfig, Long appId) {
        checkHandlerType(approverConfig);

        validateHandlerConfig(approverConfig, appId, BpmConstants.MAX_NODE_APPROVER_USERS, BpmConstants.MAX_NODE_APPROVER_ROLES);

        String approvalMode = approverConfig.getApprovalMode();
        ApprovalModeEnum approvalModeEnum = ApprovalModeEnum.getByCode(approvalMode);

        if (approvalModeEnum == null) {
            log.error("未知的审批方式: {}", approvalMode);
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_APPROVAL_MODE);
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
        checkHandlerType(extDTO.getApproverConfig());

        return buildPermissionFlag(extDTO.getApproverConfig());
    }

    @Override
    public String getSupportedNodeType() {
        return BpmNodeTypeEnum.APPROVER.getCode();
    }
}
