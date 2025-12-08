package com.cmsr.onebase.module.bpm.runtime.helper;

import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.runtime.vo.EntityVO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.*;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeConditionVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetBodyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * BPM 实体数据辅助类
 *
 * 封装基于 {@link SemanticDynamicDataApi} 的实体元数据与业务数据常用操作，
 * 统一校验逻辑，避免在各个 Service / Strategy 中重复拼装请求对象。
 *
 * @author liyang
 * @date 2025-12-04
 */
@Slf4j
@Component
public class BpmEntityHelper {

    @Resource
    private SemanticDynamicDataApi semanticDynamicDataApi;

    /**
     * 插入实体数据
     *
     * @param entityVO 实体数据 VO
     * @return 实体数据主键 ID（字符串）
     */
    public String insertEntityData(EntityVO entityVO) {
        String tableName = entityVO.getTableName();
        Map<String, Object> data = entityVO.getData();

        SemanticMergeConditionVO insertDataReqVO = new SemanticMergeConditionVO();
        insertDataReqVO.setTableName(tableName);
        insertDataReqVO.setData(data);

        SemanticEntityValueDTO entityValueDTO = semanticDynamicDataApi.insertData(insertDataReqVO);

        if (entityValueDTO == null) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_ID_NOT_EXISTS);
        }

        String entityDataId = String.valueOf(entityValueDTO.getId());

        if (StringUtils.isBlank(entityDataId)) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_ID_NOT_EXISTS);
        }

        return entityDataId;
    }

    /**
     * 根据表名和主键 ID 查询实体数据
     *
     * @param tableName   表名
     * @param entityDataId 实体数据主键 ID
     * @return 实体数据 Map
     */
    public Map<String, Object> getEntityData(String tableName, String entityDataId) {
        SemanticTargetBodyVO reqVO = new SemanticTargetBodyVO();
        reqVO.setTableName(tableName);
        reqVO.setId(entityDataId);

        SemanticEntityValueDTO respVO = semanticDynamicDataApi.getDataById(reqVO);

        if (respVO == null || respVO.getGlobalRawMap() == null) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS);
        }

        return respVO.getGlobalRawMap();
    }

    /**
     * 根据表名更新实体数据
     * @param entityVO 实体数据 VO
     *
     */
    public void updateEntityData(EntityVO entityVO) {
        if (entityVO == null) {
            return;
        }

        String tableName = entityVO.getTableName();
        Map<String, Object> data = entityVO.getData();

        SemanticMergeConditionVO updateDataReqVO = new SemanticMergeConditionVO();
        updateDataReqVO.setTableName(tableName);
        updateDataReqVO.setData(data);

        semanticDynamicDataApi.updateDataById(updateDataReqVO);
    }

    /**
     * 从实体 schema 中提取所有表（主表 + 子表）的非系统字段名称集合。
     * <p>
     * 返回结构：Map<表名, 非系统字段名集合>
     * - 主表字段来自 {@code entitySchema.getFields()}
     * - 子表字段来自关联关系中的 {@code connector.getRelationAttributes()}
     *
     * @param entitySchema 实体语义模型（包含主表及关联子表定义）
     * @return 表名到非系统字段名集合的映射
     */
    public Map<String, Set<String>> getNonSystemFields(SemanticEntitySchemaDTO entitySchema) {
        Map<String, Set<String>> nonSystemFieldMap = new HashMap<>();

        // 处理主表
        String mainTableName = entitySchema.getTableName();
        Set<String> mainTableFields = new HashSet<>();
        for (SemanticFieldSchemaDTO field : entitySchema.getFields()) {
            if (!field.getIsSystemField()) {
                mainTableFields.add(field.getFieldName());
            }
        }
        nonSystemFieldMap.put(mainTableName, mainTableFields);

        // 处理子表（通过关联关系）
        if (CollectionUtils.isNotEmpty(entitySchema.getConnectors())) {
            for (SemanticRelationSchemaDTO connector : entitySchema.getConnectors()) {
                String subTableName = connector.getTargetEntityTableName();
                Set<String> subTableFields = new HashSet<>();

                for (SemanticFieldSchemaDTO attr : connector.getRelationAttributes()) {
                    if (!attr.getIsSystemField()) {
                        subTableFields.add(attr.getFieldName());
                    }
                }

                nonSystemFieldMap.put(subTableName, subTableFields);
            }
        }

        return nonSystemFieldMap;
    }

    public SemanticFieldTypeEnum findFieldType(SemanticEntitySchemaDTO entitySchema, String tableName, String fieldName) {
        if (entitySchema == null || CollectionUtils.isEmpty(entitySchema.getFields())) {
            return null;
        }

        // 主表
        if (Objects.equals(entitySchema.getTableName(), tableName)) {
            for (SemanticFieldSchemaDTO field : entitySchema.getFields()) {
                if (Objects.equals(fieldName, field.getFieldName())) {
                    return field.getFieldTypeEnum();
                }
            }

            return null;
        }

        // 判断子表
        if (CollectionUtils.isEmpty(entitySchema.getConnectors())) {
            return null;
        }

        for (SemanticRelationSchemaDTO connector : entitySchema.getConnectors()) {
            if (!Objects.equals(connector.getTargetEntityTableName(), tableName)) {
               continue;
            }

            for (SemanticFieldSchemaDTO field : connector.getRelationAttributes()) {
                if (Objects.equals(fieldName, field.getFieldName())) {
                    return field.getFieldTypeEnum();
                }
            }
        }

        return null;
    }

    public Set<String> getSubTableNames(SemanticEntitySchemaDTO entitySchema) {
        Set<String> subTableNames = new HashSet<>();

        if (CollectionUtils.isNotEmpty(entitySchema.getConnectors())) {
            for (SemanticRelationSchemaDTO connector : entitySchema.getConnectors()) {
                subTableNames.add(connector.getTargetEntityTableName());
            }
        }

        return subTableNames;
    }
}


