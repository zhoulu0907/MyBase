package com.cmsr.onebase.module.bpm.runtime.helper;

import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.FieldPermTypeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.EntityVO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicMergeConditionVO;
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
     * 根据表名构建实体元数据 Schema
     *
     * @param tableName 表名
     * @return 实体 Schema
     */
    public SemanticEntitySchemaDTO buildSchemaByTableName(String tableName) {
        return semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);
    }

    /**
     * 根据表名构建实体 Schema，并校验实体 UUID 是否与菜单绑定实体一致
     *
     * @param tableName         表名
     * @param expectedEntityUuid 菜单绑定的实体 UUID
     * @return 实体 Schema
     */
    public SemanticEntitySchemaDTO buildAndValidateSchema(String tableName, String expectedEntityUuid) {
        SemanticEntitySchemaDTO schemaDTO = semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);

        if (schemaDTO == null || !Objects.equals(schemaDTO.getEntityUuid(), expectedEntityUuid)) {
            throw exception(ErrorCodeConstants.INVALID_ENTITY_TABLE_NAME);
        }

        return schemaDTO;
    }

    /**
     * 插入实体数据
     *
     * @param entityVO 实体数据 VO
     * @return 实体数据主键 ID（字符串）
     */
    public String insertEntityData(EntityVO entityVO) {
        String tableName = entityVO.getTableName();
        Map<String, Object> data = entityVO.getData();

        SemanicMergeConditionVO insertDataReqVO = new SemanicMergeConditionVO();
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

        SemanicMergeConditionVO updateDataReqVO = new SemanicMergeConditionVO();
        updateDataReqVO.setTableName(tableName);
        updateDataReqVO.setData(data);

        semanticDynamicDataApi.updateDataById(updateDataReqVO);
    }

    public void filterEntityData(EntityVO entityVO, FieldPermCfgDTO fieldPermConfig) {
        String tableName = entityVO.getTableName();
        Map<String, Object> data = entityVO.getData();

        Map<String, Boolean> editFieldUuidMap = new HashMap<>();

        // 重置，待过滤出可编辑的字段
        Map<String, Object> updateEntityData = new HashMap<>();

        for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldPermConfig.getFieldConfigs()) {
            String fieldUuid = fieldConfig.getFieldUuid();

            // 只保留可编辑的字段
            if (Objects.equals(fieldConfig.getFieldPermType(), FieldPermTypeEnum.WRITE.getCode())) {
                editFieldUuidMap.put(fieldUuid, true);
            }
        }

        SemanticEntitySchemaDTO entitySchema = semanticDynamicDataApi.buildEntitySchemaByTableName(entityVO.getTableName());
        Map<String, SemanticFieldSchemaDTO> fieldSchemaMap = new HashMap<>();
        Set<String> connectorTableNameSet = new HashSet<>();

        if (CollectionUtils.isNotEmpty(entitySchema.getConnectors())) {
            for (SemanticRelationSchemaDTO connector : entitySchema.getConnectors()) {
                connectorTableNameSet.add(connector.getTargetEntityTableName());
            }
        }

        for (SemanticFieldSchemaDTO schemaDTO : entitySchema.getFields()) {
            fieldSchemaMap.put(schemaDTO.getFieldName(), schemaDTO);
        }

        // todo: 处理子表字段
        log.info("connectorTableNameSet: {}", connectorTableNameSet);

        // 审批节点默认所有字段都为只读 todo: 待完善
        entityVO.getData().forEach((key, value) -> {
            // id字段，直接保留
            if ("id".equalsIgnoreCase(key)) {
                updateEntityData.put(key, value);
            } else {
                // 字段权限配置
                SemanticFieldSchemaDTO fieldSchema = fieldSchemaMap.get(key);

                if (fieldSchema == null) {
                    return;
                }

                if (editFieldUuidMap.containsKey(fieldSchema.getFieldUuid())) {
                    updateEntityData.put(key, value);
                }
            }
        });
    }
}


