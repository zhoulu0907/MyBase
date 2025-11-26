package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy.impl;

import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.*;
import com.cmsr.onebase.module.bpm.runtime.vo.EntityVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.bpm.core.utils.BpmUtil.getInitiationNodeJson;

/**
 * 审批节点策略
 *
 * @author liyang
 * @date 2025-11-03
 */
@Slf4j
@Component
public class ApproverExecTaskStrategy extends AbstractExecTaskStrategy<ApproverNodeExtDTO> {
    @Override
    public boolean supports(String bizNodeType) {
        return Objects.equals(bizNodeType, BpmNodeTypeEnum.APPROVER.getCode());
    }

    @Override
    public void execute(User matchedUser, BpmFlowAgentInsDO agentInsDO, Task task, ApproverNodeExtDTO extDTO, ExecTaskReqVO reqVO) {
        String buttonType = reqVO.getButtonType();

        // 原审批人
        String approverId = matchedUser.getProcessedBy();

        // 获取按钮权限
        List<ApproverNodeBtnCfgDTO> buttonConfigs = extDTO.getButtonConfigs();
        ApproverNodeBtnCfgDTO matchButtonConfig = null;

        for (ApproverNodeBtnCfgDTO buttonConfig : buttonConfigs) {
            if (buttonConfig.getEnabled() && Objects.equals(buttonConfig.getButtonType(), buttonType)) {
                matchButtonConfig = buttonConfig;
                break;
            }
        }

        // 未设置按钮，无权限
        if (matchButtonConfig == null) {
            throw exception(ErrorCodeConstants.NO_BUTTON_PERMISSION);
        }

        String comment = reqVO.getComment();
        if (StringUtils.isEmpty(comment)) {
            comment = matchButtonConfig.getDefaultApprovalComment();
        }

        Map<String, Object> variables = new HashMap<>();

        EntityVO entityVO = reqVO.getEntity();

        if (entityVO != null) {
            entityVO.getData().forEach((key, value) -> variables.put(String.valueOf(key), value));
        }

        // 基础 FlowParams
        FlowParams skipParams = FlowParams.build().variable(variables)
                 .message(comment);

        // 如果是代理人，需要忽略权限校验，以被代理人权限执行 todo 是否需要二次校验
        if (agentInsDO != null) {
            skipParams.ignore(true);
        }

        if (Objects.equals(buttonType, BpmActionButtonEnum.APPROVE.getCode())) {
            skipParams = skipParams.skipType(SkipType.PASS.getKey())
                .flowStatus(BpmBusinessStatusEnum.IN_APPROVAL.getCode())
                .hisStatus(BpmNodeApproveStatusEnum.POST_APPROVED.getCode())
                    .handler(approverId);

            taskService.skip(skipParams, task);
        } else if (Objects.equals(buttonType, BpmActionButtonEnum.REJECT.getCode())) {
            String nodeCode = task.getNodeCode();
            boolean hasRejectNode = false;

            List<Skip> skipList = FlowEngine.skipService().getByDefIdAndNowNodeCode(task.getDefinitionId(), nodeCode);
            for (Skip skip : skipList) {
                if (skip.getSkipType().equals(SkipType.REJECT.getKey())) {
                    hasRejectNode = true;
                    break;
                }
            }

            skipParams = skipParams.message(comment)
                    .skipType(SkipType.REJECT.getKey())
                    .flowStatus(BpmBusinessStatusEnum.REJECTED.getCode())
                    .hisStatus(BpmNodeApproveStatusEnum.POST_REJECTED.getCode())
                    .handler(approverId);

            // 有拒绝节点则走拒绝路线，否则退回至发起节点
            if (hasRejectNode) {
                taskService.skip(skipParams, task);
            } else {
                Instance instance = insService.getById(task.getInstanceId());
                NodeJson initNode = getInitiationNodeJson(instance.getDefJson());
                skipParams.nodeCode(initNode.getNodeCode());
                taskService.skip(skipParams, task);
            }
        } else {
            throw exception(ErrorCodeConstants.UNSUPPORT_ACTION_BUTTON_TYPE);
        }

        // 处理代理的场景，更新为代理人已执行
        if (agentInsDO != null) {
            agentInsDO.setIsExecutor(BooleanUtils.toInteger(true));
            agentInsRepository.update(agentInsDO);
        }

        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();

        // 实体数据更新
        if (entityVO != null && MapUtils.isNotEmpty(entityVO.getData())) {
            boolean useNodeConfig = false;

            if (fieldPermConfig != null && fieldPermConfig.getUseNodeConfig()) {
                useNodeConfig = true;
            }

            Map<Long, Object> updateEntityData;

            // 使用节点配置，则根据字段配置来更新数据
            if (useNodeConfig) {
                Map<Long, Boolean> fieldMap = new HashMap<>();

                // 重置，待过滤出可编辑的字段
                updateEntityData = new HashMap<>();

                for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
                    Long fieldId = fieldConfig.getFieldId();

                    // 只保留可编辑的字段
                    if (Objects.equals(fieldConfig.getFieldPermType(), FieldPermTypeEnum.WRITE.getCode())) {
                        fieldMap.put(fieldId, true);
                    }
                }

                // 审批节点默认所有字段都为只读
                entityVO.getData().forEach((key, value) -> {
                    if (fieldMap.containsKey(key)) {
                        updateEntityData.put(key, value);
                    }
                });
            } else {
                // todo：待完善，未开启字段权限配置，则以表单默认权限为准，此处不做校验
                updateEntityData = entityVO.getData();
            }

            // 更新数据
            if (!updateEntityData.isEmpty()) {
                UpdateDataReqDTO updateDataReqDTO = new UpdateDataReqDTO();
                updateDataReqDTO.setEntityId(entityVO.getEntityId());
                updateDataReqDTO.setData(List.of(updateEntityData));

                // 构建条件
                ConditionDTO conditionDTO = buildIdCondition(entityVO.getEntityId(), entityVO.getId());
                updateDataReqDTO.setConditionDTO(List.of(List.of(conditionDTO)));

                dataMethodApi.updateData(updateDataReqDTO);
            }
        }
    }
}


