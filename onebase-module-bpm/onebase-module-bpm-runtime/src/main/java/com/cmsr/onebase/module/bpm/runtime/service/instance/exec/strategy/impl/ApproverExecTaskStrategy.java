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
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
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
     * 构建可编辑字段映射表
     * <p>
     * 逻辑说明：
     * - 如果子表本身在 writableFieldsMap 中（作为 fieldName），说明子表是 WRITE，可以新增/删除
     * - 如果子表不在 writableFieldsMap 中，但子表字段在 writableFieldsMap 中，说明子表是 READ，可以修改字段但不能新增/删除
     * - 如果子表不在 writableFieldsMap 中，且子表字段也不在 writableFieldsMap 中，说明子表是 HIDDEN，不能做任何操作
     *
     * @param mainTableName 主表名
     * @param extDTO 节点扩展信息
     * @return 可编辑字段映射表（tableName -> Set<fieldName>）
     */
    private Map<String, Set<String>> buildFieldPermMaps(String mainTableName, ApproverNodeExtDTO extDTO) {
        Map<String, Set<String>> writableFieldsMap = new HashMap<>();

        FieldPermCfgDTO fieldPermConfig = extDTO.getFieldPermConfig();
        if (fieldPermConfig == null || CollectionUtils.isEmpty(fieldPermConfig.getFieldConfigs())) {
            return writableFieldsMap;
        }

        // 第一步：先取出 writable和hidden 的子表，放到 Set<String> 里
        Set<String> writableSubTables = new HashSet<>();
        Set<String> hiddenSubTables = new HashSet<>();

        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            String tableName = fieldConfig.getTableName();
            String fieldName = fieldConfig.getFieldName();
            String fieldPermType = fieldConfig.getFieldPermType();

            // 如果子表本身是 WRITE（tableName = fieldName 且不是主表），添加到 writableSubTables
            if (Objects.equals(tableName, fieldName) && !Objects.equals(tableName, mainTableName)) {
                if (Objects.equals(fieldPermType, FieldPermTypeEnum.WRITE.name())) {
                    writableSubTables.add(tableName);
                } else if (Objects.equals(fieldPermType, FieldPermTypeEnum.HIDDEN.name())) {
                    hiddenSubTables.add(tableName);
                }
            }
        }

        // 第二步：构建可编辑字段映射表
        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            String tableName = fieldConfig.getTableName();
            String fieldName = fieldConfig.getFieldName();
            String fieldPermType = fieldConfig.getFieldPermType();

            // 如果是子表且子表本身是隐藏权限，跳过其字段权限
            if (!Objects.equals(tableName, mainTableName) && hiddenSubTables.contains(tableName)) {
                continue;
            }

            // 如果是可编辑权限，添加到映射表（包括主表字段、子表本身、子表字段）
            if (Objects.equals(fieldPermType, FieldPermTypeEnum.WRITE.getCode())) {
                writableFieldsMap.computeIfAbsent(tableName, k -> new HashSet<>())
                        .add(fieldName);
            }
        }

        return writableFieldsMap;
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

        // 获取语义化的实体值（包含字段类型信息）
        SemanticTargetBodyVO reqVO = new SemanticTargetBodyVO();
        reqVO.setTableName(tableName);
        reqVO.setId(entityId);
        // 需要包含子表数据
        reqVO.setContainSubTable(true);
        SemanticEntityValueDTO existEntityValueDTO = semanticDynamicDataApi.getDataById(reqVO);

        if (existEntityValueDTO == null) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS);
        }

        // 先查原始数据结构
        SemanticEntitySchemaDTO entitySchema = semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);

        // 查出非系统字段
        Map<String, Set<String>> nonSystemFields = bpmEntityHelper.getNonSystemFields(entitySchema);

        // 构建可编辑字段映射表
        Map<String, Set<String>> writableFieldsMap = buildFieldPermMaps(tableName, extDTO);

        // 查出子表名称
        Set<String> subTableNames = bpmEntityHelper.getSubTableNames(entitySchema);

        // 构建最终可编辑的实体数据
        Map<String, Object> editableEntityData = new HashMap<>();

        // 处理主表字段
        processMainTableFields(editableEntityData, entitySchema, nonSystemFields, writableFieldsMap,
                entityData, existEntityValueDTO, tableName);

        // 处理子表字段
        processSubTableFields(editableEntityData, subTableNames, nonSystemFields, writableFieldsMap,
                entityData, existEntityValueDTO, entitySchema);

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
                                       Map<String, Object> entityData, SemanticEntityValueDTO existEntityValueDTO,
                                       String mainTableName) {
        // 处理 id 字段，直接保留
        Object idValue = existEntityValueDTO.getId();
        if (idValue != null) {
            editableEntityData.put("id", idValue);
        }

        Set<String> mainTableNonSystemFields = nonSystemFields.get(mainTableName);
        if (mainTableNonSystemFields == null) {
            return;
        }

        int writableCount = 0;
        for (String fieldName : mainTableNonSystemFields) {
            // 使用 containsKey 区分 key 不存在和值为 null
            boolean hasKey = entityData.containsKey(fieldName);

            if (!hasKey) {
                // 如果 key 不存在，表示不更新该字段，跳过
                continue;
            }

            // key 存在，表示要更新该字段
            Object updateValue = entityData.get(fieldName);

            // 获取字段类型并转换值进行比对
            SemanticFieldTypeEnum fieldType = bpmEntityHelper.findFieldType(entitySchema, mainTableName, fieldName);
            boolean hasModified = isFieldValueModified(updateValue, existEntityValueDTO, mainTableName, fieldName, fieldType);

            if (hasWritableField(writableFieldsMap, mainTableName, fieldName)) {
                // 有权限，直接设置 EntityData 的值（包括 null）
                editableEntityData.put(fieldName, updateValue);
                writableCount++;
            } else if (hasModified) {
                // 没有权限但字段值有变化，抛出异常
                log.error("字段值有变化，但无编辑权限，tableName: {}, fieldName: {}", mainTableName, fieldName);
                throw exception(ErrorCodeConstants.MAIN_TABLE_FIELD_NO_EDIT_PERMISSION);
            }
            // 没有权限但字段值没有变化，不设置（保持原值）
        }
        log.debug("处理主表字段完成，tableName: {}, 总字段数: {}, 更新字段数: {}", mainTableName,
                mainTableNonSystemFields.size(), writableCount);
    }

    /**
     * 判断字段值是否有变化（使用类型转换和getStoreValue比对）
     *
     * @param updateValue 更新值
     * @param existEntityValueDTO 已存在的实体值DTO
     * @param tableName 表名
     * @param fieldName 字段名
     * @param fieldType 字段类型
     * @return 是否有变化
     */
    private boolean isFieldValueModified(Object updateValue, SemanticEntityValueDTO existEntityValueDTO,
                                        String tableName, String fieldName, SemanticFieldTypeEnum fieldType) {
        if (fieldType == null) {
            // 如果无法获取字段类型，使用简单比对
            SemanticFieldValueDTO<Object> existFieldValue = existEntityValueDTO.getFieldValueByTableAndField(tableName, fieldName);
            Object existValue = existFieldValue != null ? existFieldValue.getRawValue() : null;
            return !Objects.equals(updateValue, existValue);
        }

        // 转换update值
        SemanticFieldValueDTO<Object> updateFieldValue = SemanticFieldValueDTO.ofType(fieldType);
        updateFieldValue.setRawValue(updateValue);
        Object updateStoreValue = updateFieldValue.getStoreValue();

        // 获取exist值
        SemanticFieldValueDTO<Object> existFieldValue = existEntityValueDTO.getFieldValueByTableAndField(tableName, fieldName);
        Object existStoreValue = existFieldValue != null ? existFieldValue.getStoreValue() : null;

        return !Objects.equals(updateStoreValue, existStoreValue);
    }

    /**
     * 判断子表字段值是否有变化（使用类型转换和getStoreValue比对）
     *
     * @param updateValue 更新值
     * @param existEntityValueDTO 已存在的实体值DTO
     * @param subTableName 子表名
     * @param existIndex 已存在数据在列表中的索引
     * @param fieldName 字段名
     * @param fieldType 字段类型
     * @return 是否有变化
     */
    private boolean isSubTableFieldValueModified(Object updateValue, SemanticEntityValueDTO existEntityValueDTO,
                                                 String subTableName, int existIndex, String fieldName,
                                                 SemanticFieldTypeEnum fieldType) {
        if (fieldType == null) {
            // 如果无法获取字段类型，使用简单比对
            SemanticFieldValueDTO<Object> existFieldValue = existEntityValueDTO.getFieldValueByTableAndFieldAt(subTableName, existIndex, fieldName);
            Object existValue = existFieldValue != null ? existFieldValue.getRawValue() : null;
            return !Objects.equals(updateValue, existValue);
        }

        // 转换update值
        SemanticFieldValueDTO<Object> updateFieldValue = SemanticFieldValueDTO.ofType(fieldType);
        updateFieldValue.setRawValue(updateValue);
        Object updateStoreValue = updateFieldValue.getStoreValue();

        // 获取exist值
        SemanticFieldValueDTO<Object> existFieldValue = existEntityValueDTO.getFieldValueByTableAndFieldAt(subTableName, existIndex, fieldName);
        Object existStoreValue = existFieldValue != null ? existFieldValue.getStoreValue() : null;

        return !Objects.equals(updateStoreValue, existStoreValue);
    }

    /**
     * 处理子表字段
     */
    private void processSubTableFields(Map<String, Object> editableEntityData, Set<String> subTableNames,
                                       Map<String, Set<String>> nonSystemFields,
                                       Map<String, Set<String>> writableFieldsMap,
                                       Map<String, Object> entityData, SemanticEntityValueDTO existEntityValueDTO,
                                       SemanticEntitySchemaDTO entitySchema) {
        for (String subTableName : subTableNames) {
            // 获取子表的非系统字段
            Set<String> subTableNonSystemFields = nonSystemFields.get(subTableName);
            if (subTableNonSystemFields == null) {
                log.info("子表非系统字段不存在，subTableName: {}", subTableName);
                continue;
            }

            // 获取子表字段的可编辑权限
            Set<String> subWritableFieldNames = writableFieldsMap.get(subTableName);

            // 判断子表本身的权限：
            // - 如果子表本身在 writableFieldsMap 中（作为 fieldName），可以新增/删除
            // - 如果子表不在 writableFieldsMap 中，但子表字段在 writableFieldsMap 中，可以修改字段但不能新增/删除
            // - 如果子表不在 writableFieldsMap 中，且子表字段也不在 writableFieldsMap 中，没有可编辑权限，不能做任何操作
            boolean canAddOrDelete = subWritableFieldNames != null && subWritableFieldNames.contains(subTableName);
            // hasWritableFields 需要排除子表本身，只判断是否有可编辑的字段
            boolean hasWritableFields = subWritableFieldNames != null
                    && subWritableFieldNames.stream().anyMatch(fieldName -> !Objects.equals(fieldName, subTableName));

            // 检查是否传了子表数据（使用 containsKey 区分 key 不存在和值为 null）
            boolean hasSubTableKey = entityData.containsKey(subTableName);

            // 如果没传子表数据（key不存在），可以忽略，代表不做任何修改
            if (!hasSubTableKey) {
                log.debug("子表数据 key 不存在，跳过处理，subTableName: {}", subTableName);
                continue;
            }

            // 获取要更新的子表数据（key存在，但值可能为null或[]）
            Object updateSubTableDataObj = entityData.get(subTableName);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> updateSubTableList = updateSubTableDataObj instanceof List
                    ? (List<Map<String, Object>>) updateSubTableDataObj
                    : new ArrayList<>();

            // 获取已存在的子表数据（从SemanticEntityValueDTO中获取）
            List<Map<String, Object>> existSubTableList = existEntityValueDTO.getConnectorRawList(subTableName);
            if (existSubTableList == null) {
                existSubTableList = new ArrayList<>();
            }

            // 第一步：根据 updateData 和 existData 分离出新增、更新、删除数据
            // 构建 existSubTableIndexMap 用于快速查找（key统一使用String类型的ID，value为在列表中的索引）
            Map<String, Integer> existSubTableIndexMap = new HashMap<>();
            for (int i = 0; i < existSubTableList.size(); i++) {
                Map<String, Object> item = existSubTableList.get(i);
                Object id = item.get("id");
                if (id != null) {
                    String idStr = convertIdToString(id);
                    existSubTableIndexMap.put(idStr, i);
                }
            }
            // 新增数据（没有id）
            List<Map<String, Object>> newItems = new ArrayList<>();
            // 更新数据（有id且在exist中存在）
            List<Map<String, Object>> updateItems = new ArrayList<>();
            // 删除数据（exist中有但update中没有）
            Set<String> deleteIds = new HashSet<>();

            // 处理空列表或null的情况：如果传了空列表，所有exist中的id都应该在deleteIds中
            if (CollectionUtils.isEmpty(updateSubTableList)) {
                // 如果原来没有数据，不算删除，可以忽略（此时deleteIds为空）
                if (CollectionUtils.isEmpty(existSubTableList)) {
                    log.debug("子表原数据为空，传空列表不算删除，跳过处理，subTableName: {}", subTableName);
                    continue;
                }
                // 如果原来有数据，传空列表算删除，将所有exist中的id都加入到deleteIds中
                for (Map<String, Object> existItem : existSubTableList) {
                    Object existId = existItem.get("id");
                    if (existId != null) {
                        deleteIds.add(convertIdToString(existId));
                    }
                }
            } else {
                // 处理 updateSubTableList，分离新增和更新
                for (Map<String, Object> updateItem : updateSubTableList) {
                    Object updateId = updateItem.get("id");
                    if (updateId == null) {
                        // 没有id，是新增数据
                        newItems.add(updateItem);
                    } else {
                        // 有id，统一转成String后检查是否在exist中存在
                        String updateIdStr = convertIdToString(updateId);
                        Integer existIndex = existSubTableIndexMap.get(updateIdStr);
                        if (existIndex != null) {
                            // 存在，是更新数据
                            updateItems.add(updateItem);
                        } else {
                            // 不存在，是脏数据，抛出异常
                            throw exception(ErrorCodeConstants.SUB_TABLE_DATA_ID_NOT_EXISTS);
                        }
                    }
                }

                // 处理删除数据：exist中有但update中没有
                Set<String> updateIdSet = updateSubTableList.stream()
                        .map(item -> item.get("id"))
                        .filter(Objects::nonNull)
                        .map(this::convertIdToString)
                        .collect(java.util.stream.Collectors.toSet());

                for (Map<String, Object> existItem : existSubTableList) {
                    Object existId = existItem.get("id");
                    if (existId != null) {
                        String existIdStr = convertIdToString(existId);
                        if (!updateIdSet.contains(existIdStr)) {
                            deleteIds.add(existIdStr);
                        }
                    }
                }
            }

            // 第二步：根据权限判断是否可以执行这些操作
            // 2.1 检查新增权限
            if (!newItems.isEmpty()) {
                if (!canAddOrDelete) {
                    throw exception(ErrorCodeConstants.SUB_TABLE_NO_EDIT_PERMISSION);
                }
                if (!hasWritableFields) {
                    throw exception(ErrorCodeConstants.SUB_TABLE_NO_EDIT_PERMISSION);
                }
            }

            // 2.2 检查删除权限
            if (!deleteIds.isEmpty()) {
                if (!canAddOrDelete) {
                    throw exception(ErrorCodeConstants.SUB_TABLE_NO_EDIT_PERMISSION);
                }
            }

            // 2.3 检查更新权限（需要根据字段逐个判断），同时统计有实际修改的记录数量
            int actualUpdateCount = 0;
            if (!updateItems.isEmpty()) {
                // 检查是否有字段被修改，且该字段没有编辑权限
                for (Map<String, Object> updateItem : updateItems) {
                    Object updateId = updateItem.get("id");
                    String updateIdStr = convertIdToString(updateId);
                    Integer existIndex = existSubTableIndexMap.get(updateIdStr);
                    if (existIndex == null) {
                        // 这种情况已经在上面处理了
                        continue;
                    }

                    // 检查是否有字段被修改（只检查非系统字段）
                    boolean hasModified = false;
                    for (String fieldName : subTableNonSystemFields) {
                        Object updateValue = updateItem.get(fieldName);

                        // 获取字段类型并转换值进行比对
                        SemanticFieldTypeEnum fieldType = bpmEntityHelper.findFieldType(entitySchema, subTableName, fieldName);
                        boolean fieldModified = isSubTableFieldValueModified(updateValue, existEntityValueDTO,
                                subTableName, existIndex, fieldName, fieldType);

                        if (fieldModified) {
                            hasModified = true;
                            // 如果该字段不在可编辑字段列表中，抛出异常
                            if (subWritableFieldNames == null || !subWritableFieldNames.contains(fieldName)) {
                                throw exception(ErrorCodeConstants.SUB_TABLE_NO_EDIT_PERMISSION_FIELDS);
                            }
                        }
                    }
                    // 如果有实际修改，统计数量
                    if (hasModified) {
                        actualUpdateCount++;
                    }
                }
            }

            // 第三步：处理数据（如果权限检查通过）
            List<Map<String, Object>> finalSubTableList = new ArrayList<>();

            // 3.1 处理新增数据（只设置有权限的字段）
            for (Map<String, Object> newItem : newItems) {
                Map<String, Object> processedItem = new HashMap<>();
                // 只设置有权限的字段（前面已校验新增时 subWritableFieldNames 不为空）
                if (CollectionUtils.isNotEmpty(subWritableFieldNames)) {
                    for (String fieldName : subWritableFieldNames) {
                        Object value = newItem.get(fieldName);
                        processedItem.put(fieldName, value);
                    }
                }
                finalSubTableList.add(processedItem);
            }

            // 3.2 处理更新数据（处理所有更新记录，包括没有实际修改的，以保留所有记录）
            for (Map<String, Object> updateItem : updateItems) {
                Object updateId = updateItem.get("id");
                String updateIdStr = convertIdToString(updateId);
                Integer existIndex = existSubTableIndexMap.get(updateIdStr);
                if (existIndex != null) {
                    Map<String, Object> existItem = existSubTableList.get(existIndex);
                    if (CollectionUtils.isEmpty(subWritableFieldNames)) {
                        // 只读权限，返回数据库中的原始数据
                        finalSubTableList.add(new HashMap<>(existItem));
                    } else {
                        Map<String, Object> processedItem = processUpdateSubTableItem(updateItem, existItem, subTableNonSystemFields, subWritableFieldNames);
                        finalSubTableList.add(processedItem);
                    }
                }
            }

            // 第四步：将处理后的数据添加到结果中
            // 如果整个子表都没有变化（无新增、无删除、无实际更新），可以不传子表数据
            if (newItems.isEmpty() && deleteIds.isEmpty() && actualUpdateCount == 0) {
                log.debug("子表无任何变化，跳过处理，subTableName: {}", subTableName);
                continue;
            }

            if (!finalSubTableList.isEmpty()) {
                editableEntityData.put(subTableName, finalSubTableList);
                log.debug("处理子表完成，subTableName: {}, 新增: {}, 更新: {}, 删除: {}, 最终记录数: {}",
                        subTableName, newItems.size(), actualUpdateCount, deleteIds.size(), finalSubTableList.size());
            } else if (!deleteIds.isEmpty() && canAddOrDelete) {
                // 如果只有删除操作，且可以删除，设置为空列表
                editableEntityData.put(subTableName, Collections.emptyList());
                log.debug("子表数据全部删除，subTableName: {}", subTableName);
            }
        }
    }

    /**
     * 处理更新子表数据项（只设置有权限的字段）
     *
     * @param updateItem 要更新的数据项
     * @param existItem 已存在的数据项
     * @param subWritableFieldNames 子表可编辑字段集合
     * @return 处理后的更新数据项
     */
    private Map<String, Object> processUpdateSubTableItem(Map<String, Object> updateItem,
                                                          Map<String, Object> existItem,
                                                          Set<String> subTableNonSystemFields,
                                                          Set<String> subWritableFieldNames) {
        Map<String, Object> mergedItem = new HashMap<>();
        // 先设置 id，确保记录能正确更新
        Object existId = existItem.get("id");

        if (existId != null) {
            mergedItem.put("id", existId);
        }

        // 只设置有权限的字段
        for (String fieldName : subTableNonSystemFields) {
            // 没有权限的字段，跳过
            if (!subWritableFieldNames.contains(fieldName)) {
                continue;
            }

            // 使用 containsKey 区分 key 不存在和值为 null
            if (updateItem.containsKey(fieldName)) {
                // 如果 key 存在，无论值是否为 null，都使用 updateItem 的值（包括 null）
                mergedItem.put(fieldName, updateItem.get(fieldName));
            } else {
                // 如果 key 不存在，保留原值
                Object existValue = existItem.get(fieldName);
                if (existValue != null) {
                    mergedItem.put(fieldName, existValue);
                }
            }
        }
        return mergedItem;
    }

    /**
     * 将ID统一转换为String（前端可能传String，后端可能用Long）
     *
     * @param id ID值
     * @return String类型的ID
     */
    private String convertIdToString(Object id) {
        if (id == null) {
            return null;
        }
        return String.valueOf(id);
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
