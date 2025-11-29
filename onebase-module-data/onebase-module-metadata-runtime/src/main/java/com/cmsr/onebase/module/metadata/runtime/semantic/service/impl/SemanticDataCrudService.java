package com.cmsr.onebase.module.metadata.runtime.semantic.service.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticTableNameQuoter;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.runtime.semantic.DynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private DynamicMetadataRepository dynamicMetadataRepository;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private AutoNumberService autoNumberService;


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
        // 1. 将执行前触发放到此处
        // 2. 创建单据号，填充系统字段等相关信息
        // 2.1 增加对应的 Service 处理子表等相关内容
        // 2.2 将数据库相关内容放到 子集 service 里面的 repo 中
        // 3. 将执行后触发放到此处
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) { return; }
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        // 3. 生成自动编号
        List<Long> fieldIds = fields.stream()
                .filter(f -> Objects.equals(f.getFieldTypeEnum(), SemanticFieldTypeEnum.AUTO_CODE))
                .map(SemanticFieldSchemaDTO::getId)
                .toList();
        Map<String, String> autoNumbers = autoNumberService.generateDataNumbers(fieldIds, value.getCurrentEntityRawMap());
        value.getFieldValueMap().forEach((key, fieldVaue) -> {
            if (autoNumbers.containsKey(key)) {
                fieldVaue.setRawValue(autoNumbers.get(key)); 
            }
        });

        Map<String, Object> data = extractData(recordDTO);
        Row row = new Row();
        for (SemanticFieldSchemaDTO field : fields) {
            SemanticFieldValueDTO fieldValue = value.getFieldValueByTableAndField(entity.getTableName(), field.getFieldName());
            if (fieldValue == null) { continue; }
            String name = field.getFieldName();
            if (name == null) { continue; }
            Object storeValue = fieldValue.getStoreValue();
            if (storeValue == null && Objects.equals(field.getIsPrimaryKey(), 1)) { continue; }
            row.put(name, storeValue);
        }
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) { throw exception(DATASOURCE_NOT_EXISTS); }
        semanticTemporaryDatasourceService.createTemporaryService(datasource);
        
        dynamicMetadataRepository.insert(entity.getTableName(), row);
    }

    /**
     * 更新主表数据（按主键）
     */
    public void update(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entity.getId());
        Map<String, Object> data = extractData(recordDTO);
        String pkField = getPrimaryKeyFieldName(fields);
        Object id = data.get(pkField);
        QueryWrapper qw = QueryWrapper.create().where(pkField + " = ?", id);
        Row row = new Row();
        for (Map.Entry<String, Object> e : data.entrySet()) { row.put(e.getKey(), e.getValue()); }
        Db.updateByQuery(tableNameQuoter.quote(entity.getTableName()), row, qw);
    }

    /**
     * 删除主表数据（软删优先，物理删回退）
     */
    public void delete(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entity.getId());
        String pkField = getPrimaryKeyFieldName(fields);
        Object id = extractData(recordDTO).get(pkField);
        QueryWrapper qw = QueryWrapper.create().where(pkField + " = ?", id);
        if (hasDeletedField(fields)) { softDelete(entity.getTableName(), qw); }
        else { Db.deleteByQuery(tableNameQuoter.quote(entity.getTableName()), qw); }
    }

    /**
     * 按主键读取一条主表数据
     */
    public Map<String, Object> readById(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entity.getId());
        String pkField = getPrimaryKeyFieldName(fields);
        Object id = extractData(recordDTO).get(pkField);
        QueryWrapper qw = QueryWrapper.create().where(pkField + " = ?", id);
        Row row = Db.selectOneByQuery(tableNameQuoter.quote(entity.getTableName()), qw);
        if (row == null) { return null; }
        Map<String, Object> map = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            String name = field.getFieldName();
            if (name != null) { map.put(name, row.get(name)); }
        }
        return map;
    }

    /**
     * 按主键批量查询主表数据
     */
    public List<Map<String, Object>> queryByIds(SemanticRecordDTO recordDTO, List<Object> ids) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entity.getId());
        String pkField = getPrimaryKeyFieldName(fields);
        QueryWrapper qw = QueryWrapper.create().where(pkField + " in (?)", ids);
        List<Row> rows = Db.selectListByQuery(tableNameQuoter.quote(entity.getTableName()), qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Row row : rows) {
            Map<String, Object> map = new HashMap<>();
            for (MetadataEntityFieldDO field : fields) {
                String name = field.getFieldName();
                if (name != null) { map.put(name, row.get(name)); }
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 推断主键字段名
     * 优先：显式主键且名称为 id；其次：任意显式主键；最后：回退为 id
     */
    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        Optional<String> idNamed = fields.stream()
                .filter(f -> Objects.equals(f.getIsPrimaryKey(), 1))
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        Optional<String> firstPk = fields.stream()
                .filter(f -> Objects.equals(f.getIsPrimaryKey(), 1))
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(MetadataEntityFieldDO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }

    /**
     * 判断是否存在标准软删除字段 deleted
     */
    private boolean hasDeletedField(List<MetadataEntityFieldDO> fields) {
        return fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));
    }

    /**
     * 执行软删除：将 deleted 字段置为 1
     */
    private void softDelete(String tableName, QueryWrapper qw) {
        Row row = new Row();
        row.put("deleted", 1);
        Db.updateByQuery(tableNameQuoter.quote(tableName), row, qw);
    }

    /**
     * 从 RecordDTO 的值模型提取 name->value 映射
     */
    @Deprecated
    private Map<String, Object> extractData(SemanticRecordDTO recordDTO) {
        Map<String, Object> result = new HashMap<>();
        Map<String, SemanticFieldValueDTO<Object>> data = recordDTO.getEntityValue() != null ? recordDTO.getEntityValue().getFieldValueMap() : null;
        if (data == null) { return result; }
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> fieldValueEntry : data.entrySet()) {
            SemanticFieldValueDTO<Object> v = fieldValueEntry.getValue();
            Object store = v == null ? null : v.getStoreValue();
            result.put(fieldValueEntry.getKey(), store);
        }
        return result;
    }
}
