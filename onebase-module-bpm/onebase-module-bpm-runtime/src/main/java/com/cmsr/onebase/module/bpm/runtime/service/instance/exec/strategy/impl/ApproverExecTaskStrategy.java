package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy.impl;

import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.ApproverNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.*;
import com.cmsr.onebase.module.bpm.runtime.vo.EntityVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeConditionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

import java.util.*;

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
    private boolean containsField(Map<String, Set<String>> setMap, String tableName, String fieldName) {
        return MapUtils.isNotEmpty(setMap) && setMap.containsKey(tableName) && setMap.get(tableName).contains(fieldName);
    }

    private Map<String, Set<String>> buildPermFieldMap(ApproverNodeExtDTO extDTO) {
        Map<String, Set<String>> writableFieldNamesMap = new HashMap<>();

        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();
        if (fieldPermConfig == null || CollectionUtils.isEmpty(fieldPermConfig.getFieldConfigs())) {
            return writableFieldNamesMap;
        }

        // 遍历字段配置，收集可编辑字段
        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            // 只保留可编辑的字段
            if (Objects.equals(fieldConfig.getFieldPermType(), FieldPermTypeEnum.WRITE.getCode())) {
                writableFieldNamesMap.computeIfAbsent(fieldConfig.getTableName(), k -> new HashSet<>())
                        .add(fieldConfig.getFieldName());
            }
        }

        return writableFieldNamesMap;
    }

    private Map<String, Object> buildEditableEntityData(EntityVO entityVO, ApproverNodeExtDTO extDTO) {
        // 不涉及更新
        if (entityVO == null || MapUtils.isEmpty(entityVO.getData())) {
            return null;
        }

        // 重置，待过滤出可编辑的字段
        Map<String, Object> editableEntityData = new HashMap<>();
        String tableName = entityVO.getTableName();
        Map<String, Object> entityData = entityVO.getData();

        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();

        // 未开启节点独立配置
        if (fieldPermConfig == null || fieldPermConfig.getUseNodeConfig()) {
            // todo：待完善，未开启字段权限配置，则以表单默认权限为准，此处不做校验
            editableEntityData = entityVO.getData();
            return editableEntityData;
        }

        Object idObj = entityData.get("id");

        if (idObj == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }

        // 查询当前数据
        Long entityId = Long.valueOf(String.valueOf(idObj));
        Map<String, Object> existEntityData = bpmEntityHelper.getEntityData(tableName, String.valueOf(entityId));

        if (MapUtils.isEmpty(existEntityData)) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS);
        }

        // 先查原始数据结构
        SemanticEntitySchemaDTO entitySchema = semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);

        // 查出非系统字段
        Map<String, Set<String>> nonSystemFields = bpmEntityHelper.getNonSystemFields(entitySchema);

        // 遍历字段配置，收集可编辑字段
        Map<String, Set<String>> writableFieldNamesMap = buildPermFieldMap(extDTO);

        // 查出子表名称
        Set<String> subTableNames = bpmEntityHelper.getSubTableNames(entitySchema);

        // 过滤出可编辑的字段
        Map<String, Object> finalEditableEntityData = editableEntityData;
        existEntityData.forEach((fieldName, value) -> {
            // 先判断是否子表
            boolean isSubTable = subTableNames.contains(fieldName);

            // 子表处理
            if (isSubTable) {
                Set<String> subWritableFieldNames = writableFieldNamesMap.get(fieldName);

                // 无子表操作权限，则不更新该数据
                if (CollectionUtils.isEmpty(subWritableFieldNames)) {
                    return;
                }

                // 类型不匹配，暂不处理
                if (!(value instanceof List)) {
                    return;
                }

                // 子表数据
                List<Map<String, Object>> subEntityDataList = (List<Map<String, Object>>) value;

                // 过滤出可编辑的子表字段
                subEntityDataList.forEach(subEntityData -> {
                    subEntityData.forEach((subFieldName, subValue) -> {
                        if (subWritableFieldNames.contains(subFieldName)) {
                            finalEditableEntityData.put(fieldName + "." + subFieldName, subValue);
                        }
                    });
                });

                return;
            }

            // 主表字段处理
            Set<String> nonSystemMainFields = nonSystemFields.get(fieldName);

            if (CollectionUtils.isNotEmpty(nonSystemMainFields) && nonSystemMainFields.contains(fieldName)) {
                // 先塞原数据
                finalEditableEntityData.put(fieldName, value);

                // 如果有权限
                if (containsField(writableFieldNamesMap, tableName, fieldName)) {
                    Object updateValue = entityData.get(fieldName);

                    if (updateValue != null) {
                        finalEditableEntityData.put(fieldName, updateValue);
                    }
                }
            }
        });

        return finalEditableEntityData;
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
            agentInsRepository.updateById(agentInsDO);
        }

        Map<String, Object> updateEntityData = buildEditableEntityData(entityVO, extDTO);

        // 更新数据
        if (!MapUtils.isNotEmpty(updateEntityData)) {
            SemanticMergeConditionVO conditionVO = new SemanticMergeConditionVO();
            conditionVO.setData(updateEntityData);
            conditionVO.setTableName(entityVO.getTableName());
            semanticDynamicDataApi.updateDataById(conditionVO);
        }
    }
}


