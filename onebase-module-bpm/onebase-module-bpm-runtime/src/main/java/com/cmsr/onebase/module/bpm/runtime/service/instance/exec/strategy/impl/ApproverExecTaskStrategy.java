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
    /**
     * 判断字段是否有可编辑权限
     *
     * @param writableFieldsMap 可编辑字段映射表（tableName -> Set<fieldName>）
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 是否有可编辑权限
     */
    private boolean hasWritableField(Map<String, Set<String>> writableFieldsMap, String tableName, String fieldName) {
        return MapUtils.isNotEmpty(writableFieldsMap) && writableFieldsMap.containsKey(tableName)
                && writableFieldsMap.get(tableName).contains(fieldName);
    }

    /**
     * 字段权限映射结果
     */
    private static class FieldPermResult {
        /**
         * 字段权限映射表（tableName -> Map<fieldName, fieldPermType>）
         */
        Map<String, Map<String, String>> fieldPermMap;
        /**
         * 可编辑字段映射表（tableName -> Set<fieldName>）
         */
        Map<String, Set<String>> writableFieldsMap;

        FieldPermResult() {
            this.fieldPermMap = new HashMap<>();
            this.writableFieldsMap = new HashMap<>();
        }
    }

    /**
     * 构建字段权限映射表和可编辑字段映射表（一次遍历同时构建）
     *
     * @param extDTO 节点扩展信息
     * @return 字段权限映射结果
     */
    private FieldPermResult buildFieldPermMaps(ApproverNodeExtDTO extDTO) {
        FieldPermResult result = new FieldPermResult();

        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();
        if (fieldPermConfig == null || CollectionUtils.isEmpty(fieldPermConfig.getFieldConfigs())) {
            return result;
        }

        // 一次遍历同时构建两个 Map
        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            String tableName = fieldConfig.getTableName();
            String fieldName = fieldConfig.getFieldName();
            String fieldPermType = fieldConfig.getFieldPermType();

            // 构建字段权限映射表
            result.fieldPermMap.computeIfAbsent(tableName, k -> new HashMap<>())
                    .put(fieldName, fieldPermType);

            // 如果是可编辑权限，同时构建可编辑字段映射表
            if (Objects.equals(fieldPermType, FieldPermTypeEnum.WRITE.getCode())) {
                result.writableFieldsMap.computeIfAbsent(tableName, k -> new HashSet<>())
                        .add(fieldName);
            }
        }

        return result;
    }

    private Map<String, Object> buildEditableEntityData(EntityVO entityVO, ApproverNodeExtDTO extDTO) {
        // 不涉及更新
        if (entityVO == null || MapUtils.isEmpty(entityVO.getData())) {
            log.debug("实体数据为空，跳过构建可编辑数据");
            return null;
        }

        String tableName = entityVO.getTableName();
        Map<String, Object> entityData = entityVO.getData();

        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();

        // 未开启节点独立配置
        if (fieldPermConfig == null || !fieldPermConfig.getUseNodeConfig()) {
            log.debug("未开启节点字段权限配置，直接返回原始数据，tableName: {}", tableName);
            // todo：待完善，未开启字段权限配置，则以表单默认权限为准，此处不做校验
            return new HashMap<>(entityData);
        }

        Object idObj = entityData.get("id");
        if (idObj == null) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_ID_NOT_EXISTS);
        }

        // 查询当前已存在的数据
        String entityId = String.valueOf(idObj);
        log.debug("开始构建可编辑实体数据，tableName: {}, entityId: {}", tableName, entityId);
        Map<String, Object> existEntityData = bpmEntityHelper.getEntityData(tableName, entityId);

        if (MapUtils.isEmpty(existEntityData)) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS);
        }

        // 先查原始数据结构
        SemanticEntitySchemaDTO entitySchema = semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);

        // 查出非系统字段
        Map<String, Set<String>> nonSystemFields = bpmEntityHelper.getNonSystemFields(entitySchema);

        // 构建字段权限映射表和可编辑字段映射表（一次遍历同时构建）
        FieldPermResult fieldPermResult = buildFieldPermMaps(extDTO);
        Map<String, Map<String, String>> fieldPermMap = fieldPermResult.fieldPermMap;
        Map<String, Set<String>> writableFieldsMap = fieldPermResult.writableFieldsMap;

        // 查出子表名称
        Set<String> subTableNames = bpmEntityHelper.getSubTableNames(entitySchema);

        // 构建最终可编辑的实体数据
        Map<String, Object> editableEntityData = new HashMap<>();

        // 处理主表字段
        processMainTableFields(editableEntityData, entitySchema, nonSystemFields, writableFieldsMap,
                entityData, existEntityData);

        // 处理子表字段
        processSubTableFields(editableEntityData, subTableNames, fieldPermMap, writableFieldsMap,
                entityData, existEntityData);

        // 如果只包含 id 字段，没有必要更新，返回 null
        if (editableEntityData.size() == 1 && editableEntityData.containsKey("id")) {
            log.debug("可编辑实体数据只包含 id 字段，无需更新，tableName: {}, entityId: {}", tableName, entityId);
            return null;
        }

        // 计算主表字段数（排除子表字段）
        int mainTableFieldCount = (int) editableEntityData.keySet().stream()
                .filter(key -> !subTableNames.contains(key))
                .count();
        log.debug("构建可编辑实体数据完成，tableName: {}, entityId: {}, 主表字段数: {}, 子表数: {}",
                tableName, entityId, mainTableFieldCount, subTableNames.size());
        return editableEntityData;
    }

    /**
     * 处理主表字段（增量更新：只更新有变化的字段）
     */
    private void processMainTableFields(Map<String, Object> editableEntityData, SemanticEntitySchemaDTO entitySchema,
                                       Map<String, Set<String>> nonSystemFields,
                                       Map<String, Set<String>> writableFieldsMap,
                                       Map<String, Object> entityData, Map<String, Object> existEntityData) {
        String mainTableName = entitySchema.getTableName();

        // 处理 id 字段，直接保留
        Object idValue = existEntityData.get("id");
        if (idValue != null) {
            editableEntityData.put("id", idValue);
        }

        Set<String> mainTableNonSystemFields = nonSystemFields.get(mainTableName);
        if (mainTableNonSystemFields == null) {
            return;
        }

        int writableCount = 0;
        for (String fieldName : mainTableNonSystemFields) {
            if (hasWritableField(writableFieldsMap, mainTableName, fieldName)) {
                // 有权限，直接设置 EntityData 的值（增量更新）
                Object updateValue = entityData.get(fieldName);
                if (updateValue != null) {
                    editableEntityData.put(fieldName, updateValue);
                    writableCount++;
                }
            }
            // 没有权限的字段，不设置（保持原值）
        }
        log.debug("处理主表字段完成，tableName: {}, 总字段数: {}, 更新字段数: {}", mainTableName,
                mainTableNonSystemFields.size(), writableCount);
    }

    /**
     * 处理子表字段
     */
    private void processSubTableFields(Map<String, Object> editableEntityData, Set<String> subTableNames,
                                       Map<String, Map<String, String>> fieldPermMap,
                                       Map<String, Set<String>> writableFieldsMap,
                                       Map<String, Object> entityData, Map<String, Object> existEntityData) {
        for (String subTableName : subTableNames) {
            // 获取子表本身的权限类型（tableName = subTable, fieldName = subTable）
            Map<String, String> subTableFieldPerms = fieldPermMap.get(subTableName);
            String subTablePermType = subTableFieldPerms != null ? subTableFieldPerms.get(subTableName) : null;

            // 如果子表是隐藏，无法新增、删除和修改数据
            if (Objects.equals(subTablePermType, FieldPermTypeEnum.HIDDEN.getCode())) {
                log.debug("子表为隐藏状态，无法操作，subTableName: {}", subTableName);
                continue;
            }

            // 获取子表字段的可编辑权限
            Set<String> subWritableFieldNames = writableFieldsMap.get(subTableName);

            // 如果子表没有可编辑字段权限，跳过处理
            if (CollectionUtils.isEmpty(subWritableFieldNames)) {
                log.debug("子表无可编辑字段权限，跳过处理，subTableName: {}", subTableName);
                continue;
            }

            // 获取要更新的子表数据
            Object updateSubTableDataObj = entityData.get(subTableName);

            // updateSubTableDataObj = null 表示什么都不更新，直接跳过
            if (updateSubTableDataObj == null) {
                log.debug("子表数据为 null，跳过更新，subTableName: {}", subTableName);
                continue;
            }

            // 获取已存在的子表数据
            Object existSubTableDataObj = existEntityData.get(subTableName);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> existSubTableList = existSubTableDataObj instanceof List
                    ? (List<Map<String, Object>>) existSubTableDataObj
                    : new ArrayList<>();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> updateSubTableList = updateSubTableDataObj instanceof List
                    ? (List<Map<String, Object>>) updateSubTableDataObj
                    : new ArrayList<>();

            // 判断是否可以新增和删除
            boolean canAddOrDelete = Objects.equals(subTablePermType, FieldPermTypeEnum.WRITE.getCode());

            // 如果是空列表，则相当于全部删除
            if (CollectionUtils.isEmpty(updateSubTableList)) {
                if (canAddOrDelete) {
                    log.debug("子表数据为空列表，执行全部删除，subTableName: {}", subTableName);
                    editableEntityData.put(subTableName, Collections.emptyList());
                } else {
                    log.debug("子表为只读状态，无法删除，跳过，subTableName: {}", subTableName);
                }
                continue;
            }

            // 构建子表 id 到数据的映射，方便查找
            Map<Object, Map<String, Object>> existSubTableMap = buildSubTableIdMap(existSubTableList);

            // 处理子表数据
            List<Map<String, Object>> finalSubTableList = processSubTableItems(updateSubTableList, existSubTableMap,
                    existSubTableList, subWritableFieldNames, subTableName, canAddOrDelete, subTableFieldPerms);

            // 如果有子表数据，则添加到最终结果中
            if (!finalSubTableList.isEmpty()) {
                editableEntityData.put(subTableName, finalSubTableList);
                log.debug("处理子表完成，subTableName: {}, 最终记录数: {}", subTableName, finalSubTableList.size());
            }
        }
    }

    /**
     * 处理子表数据项：有 id 就是更新，否则就是新增
     *
     * @param updateSubTableList 要更新的子表数据列表
     * @param existSubTableMap 已存在的子表数据映射（id -> data）
     * @param existSubTableList 已存在的子表数据列表
     * @param subWritableFieldNames 子表可编辑字段集合
     * @param subTableName 子表名
     * @param canAddOrDelete 是否可以新增和删除
     * @param subTableFieldPerms 子表字段权限映射（fieldName -> fieldPermType）
     * @return 处理后的子表数据列表
     */
    private List<Map<String, Object>> processSubTableItems(List<Map<String, Object>> updateSubTableList,
                                                           Map<Object, Map<String, Object>> existSubTableMap,
                                                           List<Map<String, Object>> existSubTableList,
                                                           Set<String> subWritableFieldNames, String subTableName,
                                                           boolean canAddOrDelete,
                                                           Map<String, String> subTableFieldPerms) {
        List<Map<String, Object>> finalSubTableList = new ArrayList<>();
        int addCount = 0;
        int updateCount = 0;
        int discardCount = 0;

        for (Map<String, Object> updateItem : updateSubTableList) {
            Object updateId = updateItem.get("id");

            if (updateId == null) {
                // 处理新增
                Map<String, Object> newItem = processNewSubTableItem(updateItem, subWritableFieldNames,
                        canAddOrDelete, subTableName);
                if (newItem != null) {
                    finalSubTableList.add(newItem);
                    addCount++;
                } else {
                    discardCount++;
                }
                continue;
            }

            // 有 id，查找已存在的记录
            Map<String, Object> existItem = existSubTableMap.get(updateId);

            // 如果有 id 但没匹配到 exist 数据，说明是异常数据，直接丢弃
            if (existItem == null) {
                log.warn("子表数据带 id 但不存在对应记录，视为脏数据，直接丢弃，subTableName: {}, id: {}", subTableName, updateId);
                discardCount++;
                continue;
            }

            // 处理更新
            Map<String, Object> mergedItem = processUpdateSubTableItem(updateItem, existItem, subWritableFieldNames);
            finalSubTableList.add(mergedItem);
            updateCount++;
        }

        // 如果子表是只读，需要保留 existData 中存在但 updateData 中不存在的记录
        if (!canAddOrDelete) {
            retainReadOnlySubTableItems(finalSubTableList, updateSubTableList, existSubTableList);
        }

        log.debug("处理子表数据项完成，subTableName: {}, 新增: {}, 更新: {}, 丢弃: {}, 总计: {}",
                subTableName, addCount, updateCount, discardCount, finalSubTableList.size());
        return finalSubTableList;
    }

    /**
     * 处理新增子表数据项
     *
     * @param updateItem 要新增的数据项
     * @param subWritableFieldNames 子表可编辑字段集合
     * @param canAddOrDelete 是否可以新增和删除
     * @param subTableName 子表名
     * @return 处理后的新增数据项，如果无法新增则返回 null
     */
    private Map<String, Object> processNewSubTableItem(Map<String, Object> updateItem,
                                                       Set<String> subWritableFieldNames,
                                                       boolean canAddOrDelete,
                                                       String subTableName) {
        // 判断是否可以新增
        if (!canAddOrDelete) {
            log.debug("子表为只读状态，无法新增，subTableName: {}", subTableName);
            return null;
        }

        // 即使子表可编辑，如果无可编辑字段，新增数据也应该被忽略
        if (CollectionUtils.isEmpty(subWritableFieldNames)) {
            log.debug("子表可编辑但无可编辑字段，跳过新增，subTableName: {}", subTableName);
            return null;
        }

        // 可以新增，但需要过滤不可编辑字段（设置为空）
        Map<String, Object> newItem = new HashMap<>();
        for (Map.Entry<String, Object> entry : updateItem.entrySet()) {
            String fieldName = entry.getKey();
            // 如果是可编辑字段，保留值；否则设置为空
            if (subWritableFieldNames.contains(fieldName)) {
                newItem.put(fieldName, entry.getValue());
            } else {
                // 不可编辑字段强制设置为空
                newItem.put(fieldName, null);
            }
        }

        // 如果 newItem 为空或只包含 null 值，跳过新增（避免创建无效数据）
        boolean hasValidValue = newItem.values().stream().anyMatch(Objects::nonNull);
        if (!hasValidValue) {
            log.debug("子表新增数据无可编辑字段有效值，跳过新增，subTableName: {}", subTableName);
            return null;
        }

        return newItem;
    }

    /**
     * 处理更新子表数据项（增量更新：只更新有权限的字段）
     *
     * @param updateItem 要更新的数据项
     * @param existItem 已存在的数据项
     * @param subWritableFieldNames 子表可编辑字段集合
     * @return 处理后的更新数据项
     */
    private Map<String, Object> processUpdateSubTableItem(Map<String, Object> updateItem,
                                                          Map<String, Object> existItem,
                                                          Set<String> subWritableFieldNames) {
        Map<String, Object> mergedItem = new HashMap<>();
        // 先设置 id，确保记录能正确更新
        Object existId = existItem.get("id");
        if (existId != null) {
            mergedItem.put("id", existId);
        }
        // 只设置有权限的字段
        for (String fieldName : subWritableFieldNames) {
            Object updateValue = updateItem.get(fieldName);
            if (updateValue != null) {
                // 有权限的字段，直接设置 EntityData 的值
                mergedItem.put(fieldName, updateValue);
            } else {
                // 如果 updateValue 为 null，保留原值（避免空对象导致更新失败）
                Object existValue = existItem.get(fieldName);
                if (existValue != null) {
                    mergedItem.put(fieldName, existValue);
                }
            }
        }
        return mergedItem;
    }

    /**
     * 构建子表 id 到数据的映射
     *
     * @param subTableList 子表数据列表
     * @return id 到数据的映射
     */
    private Map<Object, Map<String, Object>> buildSubTableIdMap(List<Map<String, Object>> subTableList) {
        Map<Object, Map<String, Object>> idMap = new HashMap<>();
        for (Map<String, Object> item : subTableList) {
            Object id = item.get("id");
            if (id != null) {
                idMap.put(id, item);
            }
        }
        return idMap;
    }

    /**
     * 保留只读状态下的子表数据项（existData 中存在但 updateData 中不存在的记录）
     *
     * @param finalSubTableList 最终的子表数据列表
     * @param updateSubTableList 要更新的子表数据列表
     * @param existSubTableList 已存在的子表数据列表
     */
    private void retainReadOnlySubTableItems(List<Map<String, Object>> finalSubTableList,
                                            List<Map<String, Object>> updateSubTableList,
                                            List<Map<String, Object>> existSubTableList) {
        // 找出 updateSubTableList 中所有已处理的 id
        Set<Object> processedIdSet = updateSubTableList.stream()
                .map(item -> item.get("id"))
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        // 将 existSubTableList 中不在 updateSubTableList 中的记录添加到最终结果
        existSubTableList.stream()
                .filter(existItem -> {
                    Object existId = existItem.get("id");
                    return existId != null && !processedIdSet.contains(existId);
                })
                .forEach(existItem -> finalSubTableList.add(new HashMap<>(existItem)));
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
        if (MapUtils.isNotEmpty(updateEntityData)) {
            SemanticMergeConditionVO conditionVO = new SemanticMergeConditionVO();
            conditionVO.setData(updateEntityData);
            conditionVO.setTableName(entityVO.getTableName());
            semanticDynamicDataApi.updateDataById(conditionVO);
        }
    }
}



