package com.cmsr.onebase.module.metadata.runtime.semantic.service.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataSystemFieldsCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticTableNameQuoter;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticWorkflowExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.util.SemanticRefResolver;
import com.cmsr.onebase.module.metadata.runtime.semantic.util.SemanticValueAssembler;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.runtime.semantic.dal.database.DynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 动态数据 CRUD 服务（运行态，基于 RecordDTO）
 *
 * <p>职责：
 * - 接收语义化载体 `RecordDTO`
 * - 基于实体与字段元数据执行主表的创建、更新、删除、读取与批量查询
 * - 统一处理软删除（存在 `deleted` 字段时）与物理删除
 *
 * 设计约束：
 * - 仅处理主表逻辑；子表与关系后续统一在更高层处理
 * - 不直接暴露 DO；遵循分层与职责分离
 * </p>
 */
@Service
@Slf4j
public class SemanticDataCrudService {

    @Resource
    private SemanticTableNameQuoter tableNameQuoter;
    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Resource
    private SemanticTemporaryDatasourceService semanticTemporaryDatasourceService;

    @Resource
    private MetadataSystemFieldsCoreService metadataSystemFieldsCoreService;

    @Resource
    private DynamicMetadataRepository dynamicMetadataRepository;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private AutoNumberService autoNumberService;

    @Resource
    private SemanticWorkflowExecutor semanticWorkflowExecutor;

    @Resource
    private SemanticRefResolver semanticRefResolver;


    /**
     * 根据上下文中的操作类型分派具体 CRUD 方法
     */
    public void execute(SemanticRecordDTO recordDTO) {
        MetadataDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        if (op == MetadataDataMethodOpEnum.CREATE) { create(recordDTO); }
        else if (op == MetadataDataMethodOpEnum.UPDATE) { update(recordDTO); }
        else if (op == MetadataDataMethodOpEnum.DELETE) { delete(recordDTO); }
    }

    /**
     * 创建主表数据
     */
    public void create(SemanticRecordDTO recordDTO) {
        
        // 前置工作流执行触发
        semanticWorkflowExecutor.preExecute(recordDTO);

        // 提取实体与值
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) { return; }
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        generateAndApplyAutoNumbers(fields, value);

        Row row = SemanticValueAssembler.buildMainRow(entity, value, uidGenerator);

        // 切换到目标数据源
        semanticTemporaryDatasourceService.switchMybatisFlex(entity.getDatasourceId());
        log.info("create record: {}", row);
        dynamicMetadataRepository.insert(entity.getTableName(), row);

        recordDTO.getEntityValue().setId(row.get("id"));
        recordDTO.getEntitySchema().setId(Long.valueOf(row.get("id").toString()));

        Long parentId = (Long) row.get("id");
        
        // 子表与关系表数据批量插入
        List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
        if (connectors != null && recordDTO.getEntityValue() != null) {
            Map<String, List<Row>> subBatches = collectSubRows(recordDTO, connectors, parentId);
            Map<String, List<Row>> relBatches = collectRelationRows(recordDTO, connectors);
            insertBatches(subBatches);
            insertBatches(relBatches);
        }
        semanticTemporaryDatasourceService.clearMybatisFlex();

        // 后置工作流执行触发
        semanticWorkflowExecutor.postExecute(recordDTO);

    }

    /**
     * 更新主表数据（按主键）
     */
    public void update(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entity.getId());
        


        // Map<String, Object> data = extractData(recordDTO);
        // String pkField = getPrimaryKeyFieldName(fields);
        // Object id = data.get(pkField);
        // QueryWrapper qw = QueryWrapper.create().where(pkField + " = ?", id);
        // Row row = new Row();
        // for (Map.Entry<String, Object> e : data.entrySet()) { row.put(e.getKey(), e.getValue()); }
        // Db.updateByQuery(tableNameQuoter.quote(entity.getTableName()), row, qw);
    }

    /**
     * 删除主表数据（软删优先，物理删回退）
     */
    public void delete(SemanticRecordDTO recordDTO) {
        // SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        // List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entity.getId());
        // String pkField = getPrimaryKeyFieldName(fields);
        // Object id = extractData(recordDTO).get(pkField);
        // QueryWrapper qw = QueryWrapper.create().where(pkField + " = ?", id);
        // if (hasDeletedField(fields)) { softDelete(entity.getTableName(), qw); }
        // else { Db.deleteByQuery(tableNameQuoter.quote(entity.getTableName()), qw); }
    }

    /**
     * 按主键读取一条主表数据
     */
    public Map<String, Object> readById(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        Object id = recordDTO.getEntityValue().getId();
        semanticTemporaryDatasourceService.switchMybatisFlex(entity.getDatasourceId());
        try {
            log.info("read record: table={}, {}={}", entity.getTableName(), pkField, id);
            QueryWrapper qw = QueryWrapper.create()
                                .where(new QueryColumn(pkField).eq(String.valueOf(id)));
            if (hasDeletedField(entity.getFields())) { qw.and(new QueryColumn("deleted").eq("0")); }
            log.info("read record: table={}, {}={}", entity.getTableName(), pkField, id);
            Row row = dynamicMetadataRepository.selectOneByQuery(entity.getTableName(), qw);
            if (row == null) { return null; }

            Map<String, Object> result = new HashMap<>();
            List<SemanticFieldSchemaDTO> fieldsDto = entity.getFields();
            if (fieldsDto != null) {
                for (SemanticFieldSchemaDTO f : fieldsDto) {
                    String name = f.getFieldName();
                    if (name != null) { result.put(name, row.get(name)); }
                }
            }

            SemanticEntityValueDTO resultVal = SemanticValueAssembler.toEntityValue(entity, row);

            List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
            if (connectors != null && !connectors.isEmpty()) {
                Map<String, SemanticRelationValueDTO> connVals = new HashMap<>();
                for (SemanticRelationSchemaDTO c : connectors) {
                    if (c == null || c.getTargetEntityTableName() == null) { continue; }
                    if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                        SemanticRelationValueDTO rv = readSubtableConnector(c, id);
                        if (rv != null) { connVals.put(c.getTargetEntityTableName(), rv); }
                    } else if (RelationshipTypeEnum.isConnectorRelationTable(c.getRelationshipType().getRelationshipType())) {
                        SemanticRelationValueDTO rv = readRelationConnector(c, id);
                        if (rv != null) { connVals.put(c.getTargetEntityTableName(), rv); }
                    }
                }
                resultVal.setConnectors(connVals);
            }

            semanticRefResolver.enrich(entity, resultVal);
            log.info("read record result resultVal: {}", resultVal);
            recordDTO.setResultValue(resultVal);
            Map<String, Object> resultData = recordDTO.getResultValue().getGlobalRawMapForJson();
            log.info("read record result: {}", resultData);
            return resultData;
        } catch (Exception e) {
            log.error("read record error: table={}, {}={}", entity.getTableName(), pkField, id, e);
            throw e;
        } finally {
            semanticTemporaryDatasourceService.clearMybatisFlex();
        }
    }

    private SemanticRelationValueDTO readSubtableConnector(SemanticRelationSchemaDTO c, Object parentId) {
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn("parent_id").eq(String.valueOf(parentId)));
        List<Row> rows = dynamicMetadataRepository.selectListByQuery(c.getTargetEntityTableName(), qw);
        if (rows == null || rows.isEmpty()) { return null; }
        SemanticRelationValueDTO relation = new SemanticRelationValueDTO();
        List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
        if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
            Row r = rows.get(0);
            SemanticRowValueDTO rowDto = SemanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName());
            relation.setRowValue(rowDto);
        } else {
            List<SemanticRowValueDTO> list = new ArrayList<>();
            for (Row r : rows) { list.add(SemanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName())); }
            relation.setRowValueList(list);
        }
        return relation;
    }

    private SemanticRelationValueDTO readRelationConnector(SemanticRelationSchemaDTO c, Object parentId) {
        String srcKey = "parent_id";
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(srcKey).eq(String.valueOf(parentId)));
        List<Row> rows = dynamicMetadataRepository.selectListByQuery(c.getTargetEntityTableName(), qw);
        if (rows == null || rows.isEmpty()) { return null; }
        SemanticRelationValueDTO relation = new SemanticRelationValueDTO();
        List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
        if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
            Row r = rows.get(0);
            SemanticRowValueDTO rowDto = SemanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName());
            relation.setRowValue(rowDto);
        } else {
            List<SemanticRowValueDTO> list = new ArrayList<>();
            for (Row r : rows) { list.add(SemanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName())); }
            relation.setRowValueList(list);
        }
        return relation;
    }

    /**
     * 按主键批量查询主表数据
     */
    public List<Map<String, Object>> queryByIds(SemanticRecordDTO recordDTO, List<Object> ids) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        QueryWrapper qw = QueryWrapper.create().where(pkField + " in (?)", ids);
        List<Row> rows = Db.selectListByQuery(tableNameQuoter.quote(entity.getTableName()), qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Row row : rows) {
            Map<String, Object> map = new HashMap<>();
            List<SemanticFieldSchemaDTO> fieldsDto = entity.getFields();
            if (fieldsDto != null) {
                for (SemanticFieldSchemaDTO field : fieldsDto) {
                    String name = field.getFieldName();
                    if (name != null) { map.put(name, row.get(name)); }
                }
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 推断主键字段名
     * 优先：显式主键且名称为 id；其次：任意显式主键；最后：回退为 id
     */
    private String getPrimaryKeyFieldName(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) { return "id"; }
        Optional<String> idNamed = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        Optional<String> firstPk = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(SemanticFieldSchemaDTO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }

    /**
     * 判断是否存在标准软删除字段 deleted
     */
    private boolean hasDeletedField(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) { return false; }
        return fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));
    }

    private void generateAndApplyAutoNumbers(List<SemanticFieldSchemaDTO> fields, SemanticEntityValueDTO value) {
        List<Long> fieldIds = fields.stream()
                .filter(f -> Objects.equals(f.getFieldTypeEnum(), SemanticFieldTypeEnum.AUTO_CODE))
                .map(SemanticFieldSchemaDTO::getId)
                .toList();
        Map<String, String> autoNumbers = autoNumberService.generateDataNumbers(fieldIds, value.getCurrentEntityRawMap());
        value.getFieldValueMap().forEach((key, fieldVaue) -> {
            if (autoNumbers.containsKey(key)) { fieldVaue.setRawValue(autoNumbers.get(key)); }
        });
    }

    private Map<String, List<Row>> collectSubRows(SemanticRecordDTO recordDTO, List<SemanticRelationSchemaDTO> connectors, Long parentId) {
        Map<String, List<Row>> batches = new HashMap<>();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) { continue; }
            if (!RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) { continue; }
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> subDto = recordDTO.getEntityValue().getConnectorDTOObject(c.getTargetEntityTableName());
                applyConnectorAutoNumbers(c, subDto);
                if (subDto != null && !subDto.isEmpty()) { batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>()).add(SemanticValueAssembler.buildSubRow(subDto, parentId, uidGenerator)); }
            } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(c.getTargetEntityTableName());
                if (list != null) {
                    List<Row> rows = batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>());
                    for (Map<String, SemanticFieldValueDTO<Object>> subDto : list) { 
                        applyConnectorAutoNumbers(c, subDto);
                        if (subDto != null && !subDto.isEmpty()) { rows.add(SemanticValueAssembler.buildSubRow(subDto, parentId, uidGenerator)); }
                    }
                }
            }
        }
        return batches;
    }

    private Map<String, List<Row>> collectRelationRows(SemanticRecordDTO recordDTO, List<SemanticRelationSchemaDTO> connectors) {
        Map<String, List<Row>> batches = new HashMap<>();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) { continue; }
            if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) { continue; }
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> relDto = recordDTO.getEntityValue().getConnectorDTOObject(c.getTargetEntityTableName());
                applyConnectorAutoNumbers(c, relDto);
                if (relDto != null && !relDto.isEmpty()) { batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>()).add(SemanticValueAssembler.buildRelationRow(relDto, uidGenerator)); }
            } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(c.getTargetEntityTableName());
                if (list != null) {
                    List<Row> rows = batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>());
                    for (Map<String, SemanticFieldValueDTO<Object>> relDto : list) { 
                        applyConnectorAutoNumbers(c, relDto);
                        if (relDto != null && !relDto.isEmpty()) { rows.add(SemanticValueAssembler.buildRelationRow(relDto, uidGenerator)); }
                    }
                }
            }
        }
        return batches;
    }

    private void insertBatches(Map<String, List<Row>> batches) {
        for (Map.Entry<String, List<Row>> e : batches.entrySet()) {
            List<Row> rows = e.getValue();
            if (rows == null || rows.isEmpty()) { continue; }
            dynamicMetadataRepository.insertBatch(e.getKey(), rows);
        }
    }

    private void applyConnectorAutoNumbers(SemanticRelationSchemaDTO connector, Map<String, SemanticFieldValueDTO<Object>> fields) {
        if (connector == null || fields == null) { return; }
        List<SemanticFieldSchemaDTO> attrs = connector.getRelationAttributes();
        if (attrs == null || attrs.isEmpty()) { return; }
        List<Long> fieldIds = attrs.stream()
                .filter(f -> Objects.equals(f.getFieldTypeEnum(), SemanticFieldTypeEnum.AUTO_CODE))
                .map(SemanticFieldSchemaDTO::getId)
                .toList();
        if (fieldIds.isEmpty()) { return; }
        Map<String, Object> raw = new HashMap<>();
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
            SemanticFieldValueDTO<Object> v = e.getValue();
            raw.put(e.getKey(), v == null ? null : v.getRawValue());
        }
        Map<String, String> autoNumbers = autoNumberService.generateDataNumbers(fieldIds, raw);
        for (Map.Entry<String, String> e : autoNumbers.entrySet()) {
            SemanticFieldValueDTO<Object> v = fields.get(e.getKey());
            if (v != null) { v.setRawValue(e.getValue()); }
        }
    }
}
