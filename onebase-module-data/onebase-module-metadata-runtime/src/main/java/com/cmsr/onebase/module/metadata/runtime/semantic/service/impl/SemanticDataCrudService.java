package com.cmsr.onebase.module.metadata.runtime.semantic.service.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
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
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticQueryConditionBuilder;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticRefResolver;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticValueAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticWorkflowExecutor;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.permission.SemanticQueryPermissionHelper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
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
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;

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
    @Resource
    private SemanticValueAssembler semanticValueAssembler;
    @Resource
    private SemanticQueryPermissionHelper semanticQueryPermissionHelper;
    @Resource
    private SemanticQueryConditionBuilder semanticQueryConditionBuilder;


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
     * 
     * 流程：
     * 1. 触发前置工作流钩子，执行校验、默认值填充等
     * 2. 提取实体元数据与值，生成自动编号/系统字段
     * 3. 构建主表 Row（包含主键、系统字段等）
     * 4. 切换到目标数据源并插入主表记录
     * 5. 回写主键到 RecordDTO（用于后续子表/关系处理）
     * 6. 批量插入子表与关系表数据（如果存在连接器）
     * 7. 清理数据源上下文并触发后置工作流钩子
     * 
     * 注意：
     * - 若 `entity` 或 `value` 为空，方法直接返回
     * - 自动编号由 AutoNumberService 根据字段策略生成并应用
     */
    public void create(SemanticRecordDTO recordDTO) {
        
        // 前置工作流：用于校验、默认值填充、上下文准备等
        semanticWorkflowExecutor.preExecute(recordDTO);

        // 提取实体与值
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) { return; }
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        // 根据字段配置生成并回填自动编号/系统字段（如编码、创建人等）
        generateAndApplyAutoNumbers(fields, value);

        // 将 DTO 转换为可持久化的 Row；若未提供主键则由 uidGenerator 生成
        Row row = semanticValueAssembler.buildMainRow(entity, value, uidGenerator);

        log.info("create record: {}", row);
        // 插入主表记录
        dynamicMetadataRepository.insert(entity.getTableName(), row);

        // 回写主键到 value 与 entity，后续子表/关系写入需要使用
        recordDTO.getEntityValue().setId(row.get("id"));
        recordDTO.getEntitySchema().setId(Long.valueOf(row.get("id").toString()));

        Long parentId = (Long) row.get("id");
        
        // 计算并收集子表与关系表的批量插入数据
        List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
        if (connectors != null && recordDTO.getEntityValue() != null) {
            Map<String, List<Row>> subBatches = collectSubRows(recordDTO, connectors, parentId);
            Map<String, List<Row>> relBatches = collectRelationRows(recordDTO, connectors);
            // 分别批量写入子表与关系表，避免循环内逐条插入导致性能问题
            insertBatches(subBatches);
            insertBatches(relBatches);
        }

        // 后置工作流：可做审计、事件发布、缓存刷新等
        semanticWorkflowExecutor.postExecute(recordDTO);

    }

    /**
     * 更新主表数据（按主键）
     * 
     * 流程：
     * 1. 执行前置工作流（校验/准备）
     * 2. 通过元数据确定主键字段名，并解析待更新记录的主键
     * 3. 构建更新 Row（不包含主键字段）
     * 4. 切换目标数据源并按主键条件执行更新
     * 5. 清理数据源上下文，触发后置工作流
     * 
     * 注意：
     * - 若无法解析到主键 id，方法直接返回
     * - 为防止误更新主键，显式从 Row 中移除主键字段
     */
    public void update(SemanticRecordDTO recordDTO) {
        // 前置工作流：参数校验、权限校验、上下文准备
        semanticWorkflowExecutor.preExecute(recordDTO);
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) { return; }
        // 根据元数据字段列表确定主键字段名
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        // 优先使用 DTO 中的 id；否则从原始 Map 中提取
        Object id = value.getId() != null ? value.getId() : value.getCurrentEntityRawMap().get(pkField);
        if (id == null) { return; }
        // 构建仅包含待更新字段的 Row；不含主键
        Row row = buildUpdateRow(entity, value);
        // 防止主键被更新
        row.remove(pkField);
        // 切换到目标数据源
        // 按主键条件更新主表记录
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(String.valueOf(id)));
        dynamicMetadataRepository.updateByQuery(entity.getTableName(), row, qw);
        // 后置工作流：审计、事件发布等
        semanticWorkflowExecutor.postExecute(recordDTO);
    }

    /**
     * 删除主表数据（软删优先，物理删回退）
     * 
     * 策略：
     * - 若存在 `deleted` 字段，则执行软删除（更新标记）
     * - 否则执行物理删除（直接删除记录）
     * 
     * 流程：
     * 1. 前置工作流
     * 2. 解析主键并切换数据源
     * 3. 按策略执行删除
     * 4. 清理数据源并执行后置工作流
     */
    public void delete(SemanticRecordDTO recordDTO) {
        // 前置工作流：权限校验、审计记录准备等
        semanticWorkflowExecutor.preExecute(recordDTO);
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) { return; }
        // 确定主键字段名并解析 id
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        Object id = value.getId() != null ? value.getId() : value.getCurrentEntityRawMap().get(pkField);
        if (id == null) { return; }
        
        // 构建主键条件并执行删除（软删优先）
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(String.valueOf(id)));
        if (hasDeletedField(entity.getFields())) {
            dynamicMetadataRepository.softDeleteByQuery(entity.getTableName(), qw);
        } else {
            // dynamicMetadataRepository.deleteByQuery(entity.getTableName(), qw);
        }
        // 后置工作流：审计、事件发布等
        semanticWorkflowExecutor.postExecute(recordDTO);
    }

    /**
     * 按主键读取一条主表数据
     * 
     * 流程：
     * 1. 切换数据源并按主键读取主表 Row（考虑软删除字段）
     * 2. 回填字段值到 Map 以便 JSON 输出
     * 3. 将 Row 转换为 `SemanticEntityValueDTO`
     * 4. 读取并填充子表与关系表连接器的值
     * 5. 通过引用解析器进行富化（字典/引用字段）
     * 6. 设置到 `recordDTO` 并返回可序列化的结果
     * 
     * 注意：
     * - 若主表记录不存在，返回 null
     */
    public Map<String, Object> readById(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        Object id = recordDTO.getEntityValue().getId();
        // 切换到目标数据源
        try {
            log.info("read record: table={}, {}={}", entity.getTableName(), pkField, id);
            // 查询主表记录；若存在软删除字段则进行条件过滤
            Row row = dynamicMetadataRepository.selectMainById(entity.getTableName(), pkField, id, hasDeletedField(entity.getFields()));
            if (row == null) { return null; }

            Map<String, Object> result = new HashMap<>();
            List<SemanticFieldSchemaDTO> fieldsDto = entity.getFields();
            if (fieldsDto != null) {
                for (SemanticFieldSchemaDTO f : fieldsDto) {
                    String name = f.getFieldName();
                    // 仅回填具有字段名的列
                    if (name != null) { result.put(name, row.get(name)); }
                }
            }

            // 将行数据转换为语义化的实体值对象
            SemanticEntityValueDTO resultVal = semanticValueAssembler.toEntityValue(entity, row);

            List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
            if (connectors != null && !connectors.isEmpty()) {
                Map<String, SemanticRelationValueDTO> connVals = new HashMap<>();
                for (SemanticRelationSchemaDTO c : connectors) {
                    if (c == null || c.getTargetEntityTableName() == null) { continue; }
                    if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                        // 读取子表连接器的值
                        SemanticRelationValueDTO rv = readSubtableConnector(c, id);
                        if (rv != null) { connVals.put(c.getTargetEntityTableName(), rv); }
                    } else if (RelationshipTypeEnum.isConnectorRelationTable(c.getRelationshipType().getRelationshipType())) {
                        // 读取关系表连接器的值
                        SemanticRelationValueDTO rv = readRelationConnector(c, id);
                        if (rv != null) { connVals.put(c.getTargetEntityTableName(), rv); }
                    }
                }
                resultVal.setConnectors(connVals);
            }

            // 引用解析与富化（如字典翻译、外键名称回填）
            semanticRefResolver.enrich(entity, resultVal);
            log.info("read record result resultVal: {}", resultVal);
            // 设置读取结果到上下文，用于统一输出
            recordDTO.setResultValue(resultVal);
            // 转换为可序列化的 Map（适配前端 JSON 输出）
            Map<String, Object> resultData = recordDTO.getResultValue().getGlobalRawMapForJson();
            log.info("read record result: {}", resultData);
            return resultData;
        } catch (Exception e) {
            log.error("read record error: table={}, {}={}", entity.getTableName(), pkField, id, e);
            throw e;
        }
    }

    /**
     * 分页查询主表数据（接收外部构建的 QueryWrapper）
     *
     * - 支持执行器或外部策略构建 `QueryWrapper`
     * - 统一处理分页参数、行到语义值转换、引用富化与字段权限过滤
     *
     * @param recordDTO 语义记录上下文
     * @param qw 已构建的查询条件包装器
     * @return 分页结果（已进行引用解析与字段权限过滤）
     */
    public PageResult<Map<String, Object>> queryPage(SemanticRecordDTO recordDTO, QueryWrapper qw) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        Integer pageNo = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getPageNo();
        Integer pageSize = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getPageSize();
        int pn = pageNo == null ? 1 : pageNo;
        int ps = pageSize == null ? 10 : pageSize;
        PageResult<Row> pageRows = dynamicMetadataRepository.selectPageByQuery(entity.getTableName(), qw, pn, ps);
        List<Row> rows = pageRows.getList();
        List<Map<String, Object>> result = new ArrayList<>();
        List<SemanticEntityValueDTO> values = new ArrayList<>();
        for (Row row : rows) { values.add(semanticValueAssembler.toEntityValue(entity, row)); }
        semanticRefResolver.enrichBatch(entity, values);
        for (SemanticEntityValueDTO val : values) { result.add(val.getGlobalRawMapForJson()); }
        result = semanticQueryPermissionHelper.filterQueryResultList(result, recordDTO.getRecordContext().getPermissionContext(), fields);
        return new PageResult<>(result, pageRows.getTotal());
    }

    /**
     * 构建分页查询条件
     *
     * - 过滤：仅对实体存在的字段生效；字符串使用 like，其它类型使用等值
     * - 排序：按请求排序；未指定时回退主键倒序
     * - 软删：存在 `deleted` 字段时追加 `deleted = 0`
     *
     * @param recordDTO 语义记录上下文
     * @param fields 实体字段集合
     * @return 构建完成的 `QueryWrapper`
     */
    private QueryWrapper buildPageQueryWrapper(SemanticRecordDTO recordDTO, List<SemanticFieldSchemaDTO> fields) {
        QueryWrapper qw = QueryWrapper.create();
        Map<String, Object> filters = recordDTO.getRecordContext().getFilters();
        List<SemanticSortRuleDTO> sortBy = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getSortBy();
        semanticQueryConditionBuilder.apply(qw, fields, filters, sortBy);
        return qw;
    }

    /**
     * 构建更新 Row
     *
     * - 提取待更新字段的存储值并填充到 Row
     * - 对系统字段（如 `updated_time`、`updater`）进行占位填充，由数据库处理默认值
     *
     * @param entity 实体元数据
     * @param value 当前值对象
     * @return 仅包含待更新列的 Row
     */
    private Row buildUpdateRow(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value) {
        Row row = new Row();
        List<SemanticFieldSchemaDTO> fieldsDto = entity.getFields();
        if (fieldsDto != null) {
            for (SemanticFieldSchemaDTO f : fieldsDto) {
                String name = f.getFieldName();
                if (name == null) { continue; }
                SemanticFieldValueDTO<Object> v = value.getFieldValueByTableAndField(entity.getTableName(), name);
                if (v == null) { continue; }
                Object sv = v.getStoreValue();
                if (sv != null) { row.put(name, sv); }
            }
            boolean hasUpdatedTime = fieldsDto.stream().anyMatch(f -> {
                String n = f.getFieldName();
                return n != null && ("updated_time".equalsIgnoreCase(n) || "updatetime".equalsIgnoreCase(n));
            });
            boolean hasUpdater = fieldsDto.stream().anyMatch(f -> {
                String n = f.getFieldName();
                return n != null && ("updater".equalsIgnoreCase(n));
            });
            if (hasUpdatedTime) {
                if (!row.containsKey("updated_time")) { row.put("updated_time", null); }
            }
            if (hasUpdater && !row.containsKey("updater")) { row.put("updater", null); }
        }
        return row;
    }

    /**
     * 读取子表连接器数据
     *
     * - 基于父主键读取目标子表数据集
     * - ONE：返回首行映射；MANY：返回列表映射
     *
     * @param c 连接器定义
     * @param parentId 父记录主键
     * @return 连接器值（可能为 null）
     */
    private SemanticRelationValueDTO readSubtableConnector(SemanticRelationSchemaDTO c, Object parentId) {
        List<Row> rows = dynamicMetadataRepository.selectSubtableRowsByParent(c.getTargetEntityTableName(), parentId);
        if (rows == null || rows.isEmpty()) { return null; }
        SemanticRelationValueDTO relation = new SemanticRelationValueDTO();
        List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
        if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
            Row r = rows.get(0);
            SemanticRowValueDTO rowDto = semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName());
            relation.setRowValue(rowDto);
        } else {
            List<SemanticRowValueDTO> list = new ArrayList<>();
            for (Row r : rows) { list.add(semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName())); }
            relation.setRowValueList(list);
        }
        return relation;
    }

    /**
     * 读取关系表连接器数据
     *
     * - 使用关系表父键 `parent_id` 进行查询
     * - ONE：返回首行映射；MANY：返回列表映射
     *
     * @param c 连接器定义
     * @param parentId 父记录主键
     * @return 连接器值（可能为 null）
     */
    private SemanticRelationValueDTO readRelationConnector(SemanticRelationSchemaDTO c, Object parentId) {
        String srcKey = "parent_id";
        List<Row> rows = dynamicMetadataRepository.selectRelationRowsByParent(c.getTargetEntityTableName(), srcKey, parentId);
        if (rows == null || rows.isEmpty()) { return null; }
        SemanticRelationValueDTO relation = new SemanticRelationValueDTO();
        List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
        if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
            Row r = rows.get(0);
            SemanticRowValueDTO rowDto = semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName());
            relation.setRowValue(rowDto);
        } else {
            List<SemanticRowValueDTO> list = new ArrayList<>();
            for (Row r : rows) { list.add(semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName())); }
            relation.setRowValueList(list);
        }
        return relation;
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

    /**
     * 生成并应用主表自动编号字段
     *
     * - 收集类型为 `AUTO_CODE` 的字段 ID
     * - 调用编号服务生成编码并写回原始值（rawValue）
     *
     * @param fields 字段集合
     * @param value 当前值对象
     */
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

    /**
     * 汇总子表批次数据
     *
     * - 针对 ONE/MANY 两类子表连接器分别构建 Row
     * - 在构建前应用连接器字段的自动编号策略
     *
     * @param recordDTO 语义记录上下文
     * @param connectors 连接器集合
     * @param parentId 父记录主键
     * @return 表名到 Row 列表的映射
     */
    private Map<String, List<Row>> collectSubRows(SemanticRecordDTO recordDTO, List<SemanticRelationSchemaDTO> connectors, Long parentId) {
        Map<String, List<Row>> batches = new HashMap<>();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) { continue; }
            if (!RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) { continue; }
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> subDto = recordDTO.getEntityValue().getConnectorDTOObject(c.getTargetEntityTableName());
                applyConnectorAutoNumbers(c, subDto);
                if (subDto != null && !subDto.isEmpty()) { batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>()).add(semanticValueAssembler.buildSubRow(subDto, parentId, uidGenerator)); }
            } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(c.getTargetEntityTableName());
                if (list != null) {
                    List<Row> rows = batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>());
                    for (Map<String, SemanticFieldValueDTO<Object>> subDto : list) { 
                        applyConnectorAutoNumbers(c, subDto);
                        if (subDto != null && !subDto.isEmpty()) { rows.add(semanticValueAssembler.buildSubRow(subDto, parentId, uidGenerator)); }
                    }
                }
            }
        }
        return batches;
    }

    /**
     * 汇总关系表批次数据
     *
     * - 针对 ONE/MANY 两类关系连接器分别构建 Row
     * - 在构建前应用连接器字段的自动编号策略
     *
     * @param recordDTO 语义记录上下文
     * @param connectors 连接器集合
     * @return 关系表名到 Row 列表的映射
     */
    private Map<String, List<Row>> collectRelationRows(SemanticRecordDTO recordDTO, List<SemanticRelationSchemaDTO> connectors) {
        Map<String, List<Row>> batches = new HashMap<>();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) { continue; }
            if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) { continue; }
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> relDto = recordDTO.getEntityValue().getConnectorDTOObject(c.getTargetEntityTableName());
                applyConnectorAutoNumbers(c, relDto);
                if (relDto != null && !relDto.isEmpty()) { batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>()).add(semanticValueAssembler.buildRelationRow(relDto, uidGenerator)); }
            } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(c.getTargetEntityTableName());
                if (list != null) {
                    List<Row> rows = batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>());
                    for (Map<String, SemanticFieldValueDTO<Object>> relDto : list) { 
                        applyConnectorAutoNumbers(c, relDto);
                        if (relDto != null && !relDto.isEmpty()) { rows.add(semanticValueAssembler.buildRelationRow(relDto, uidGenerator)); }
                    }
                }
            }
        }
        return batches;
    }

    /**
     * 批量插入数据行
     *
     * - 按表名分组批量插入，避免循环内单条插入的性能问题
     *
     * @param batches 表名到 Row 列表的映射
     */
    private void insertBatches(Map<String, List<Row>> batches) {
        for (Map.Entry<String, List<Row>> e : batches.entrySet()) {
            List<Row> rows = e.getValue();
            if (rows == null || rows.isEmpty()) { continue; }
            dynamicMetadataRepository.insertBatch(e.getKey(), rows);
        }
    }

    /**
     * 应用连接器字段的自动编号策略
     *
     * - 收集属性中类型为 `AUTO_CODE` 的字段
     * - 生成编码并写回原始值（rawValue）
     *
     * @param connector 连接器定义
     * @param fields 字段值映射
     */
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
