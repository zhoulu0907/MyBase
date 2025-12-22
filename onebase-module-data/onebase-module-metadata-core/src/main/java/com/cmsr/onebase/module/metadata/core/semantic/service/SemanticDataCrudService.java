package com.cmsr.onebase.module.metadata.core.semantic.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.constants.SystemFieldConstants;
import com.cmsr.onebase.module.metadata.core.semantic.dal.DynamicMetadataRepository;
import com.cmsr.onebase.module.metadata.core.semantic.dto.*;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticQueryConditionBuilder;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticRefResolver;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticValueAssembler;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.SemanticWorkflowExecutor;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.permission.SemanticQueryPermissionHelper;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataSystemFieldsCoreService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum.AUTO_CODE;

/**
 * 动态数据 CRUD 服务（运行态，基于 RecordDTO）
 *
 * <p>职责：
 * - 接收语义化载体 `RecordDTO`
 * - 基于实体与字段元数据执行主表的创建、更新、删除、读取与批量查询
 * - 统一处理软删除（存在 `deleted` 字段时）与物理删除
 * <p>
 * 设计约束：
 * - 仅处理主表逻辑；子表与关系后续统一在更高层处理
 * - 不直接暴露 DO；遵循分层与职责分离
 * </p>
 */
@Service
@Slf4j
public class SemanticDataCrudService {
    /**
     * 主表批量操作最大处理条数
     * 使用范围：
     * - 仅主表的批量删除与批量更新（deleteByQuery、updateByQuery）
     * - 查询统一通过 selectPageByQuery，取第 1 页，pageSize = MAX_BATCH_LIMIT
     * 不适用范围：
     * - 子表和关联表的读取与 upsert，不受该限制
     * 设计意图：
     * - 控制单次批处理的资源消耗与锁风险
     * - 保留逐条执行的工作流、权限与级联逻辑
     * 调整建议：
     * - 后续可改为可配置项（如 application 配置），支持不同接口覆盖
     */
    private static final int MAX_BATCH_LIMIT = 100;

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
        SemanticDataMethodOpEnum op = recordDTO.getRecordContext().getOperationType();
        if (op == SemanticDataMethodOpEnum.CREATE) {
            create(recordDTO);
        } else if (op == SemanticDataMethodOpEnum.UPDATE) {
            update(recordDTO);
        } else if (op == SemanticDataMethodOpEnum.DELETE) {
            delete(recordDTO);
        }
    }

    /**
     * 创建主表数据
     * <p>
     * 流程：
     * 1. 触发前置工作流钩子，执行校验、默认值填充等
     * 2. 提取实体元数据与值，生成自动编号/系统字段
     * 3. 构建主表 Row（包含主键、系统字段等）
     * 4. 切换到目标数据源并插入主表记录
     * 5. 回写主键到 RecordDTO（用于后续子表/关系处理）
     * 6. 批量插入子表与关系表数据（如果存在连接器）
     * 7. 清理数据源上下文并触发后置工作流钩子
     * <p>
     * 注意：
     * - 若 `entity` 或 `value` 为空，方法直接返回
     * - 自动编号由 AutoNumberService 根据字段策略生成并应用
     */
    public void create(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) {
            return;
        }
        // 前置工作流：用于校验、默认值填充、上下文准备等
        semanticWorkflowExecutor.preExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                value.getFieldValueMap().values().stream().toList(),
                buildConnectorFieldValueBatches(recordDTO)
        );
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        // 根据字段配置生成并回填自动编号/系统字段（如编码、创建人等）
        generateAndApplyAutoNumbers(fields, value);

        // 将 DTO 转换为可持久化的 Row；若未提供主键则由 uidGenerator 生成
        Row row = semanticValueAssembler.buildMainRow(entity, value, uidGenerator);

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
        semanticWorkflowExecutor.postExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                value.getFieldValueMap().values().stream().toList(),
                buildConnectorFieldValueBatches(recordDTO)
        );

    }

    /**
     * 更新主表数据（按主键）
     * <p>
     * 流程：
     * 1. 执行前置工作流（校验/准备）
     * 2. 通过元数据确定主键字段名，并解析待更新记录的主键
     * 3. 构建更新 Row（不包含主键字段）
     * 4. 切换目标数据源并按主键条件执行更新
     * 5. 清理数据源上下文，触发后置工作流
     * <p>
     * 注意：
     * - 若无法解析到主键 id，方法直接返回
     * - 为防止误更新主键，显式从 Row 中移除主键字段
     */
    public void update(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) {
            return;
        }
        // 前置工作流：参数校验、权限校验、上下文准备
        semanticWorkflowExecutor.preExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                value.getFieldValueMap().values().stream().toList(),
                buildConnectorFieldValueBatches(recordDTO)
        );
        // 根据元数据字段列表确定主键字段名
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        // 优先使用 DTO 中的 id；否则从原始 Map 中提取
        Object id = value.getId() != null ? value.getId() : value.getCurrentEntityRawMap().get(pkField);
        if (id == null) {
            return;
        }
        // 构建仅包含待更新字段的 Row；不含主键
        Row row = buildUpdateRow(entity, value);
        // 防止主键被更新
        row.remove(pkField);
        // 切换到目标数据源
        // 按主键条件更新主表记录
        Object v = toLongIfNotEmpty(id);
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(v != null ? v : id));
        dynamicMetadataRepository.updateByQuery(entity.getTableName(), row, qw);

        Long parentId = null;
        try {
            parentId = Long.valueOf(String.valueOf(id));
        } catch (Exception ignored) {
        }
        List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
        if (parentId != null && connectors != null && !connectors.isEmpty()) {
            upsertSubtableConnectors(recordDTO, parentId);
            upsertRelationConnectors(recordDTO, parentId);
        }
        // 后置工作流：审计、事件发布等
        semanticWorkflowExecutor.postExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                value.getFieldValueMap().values().stream().toList(),
                buildConnectorFieldValueBatches(recordDTO)
        );
    }

    /**
     * 删除主表数据（软删优先，物理删回退）
     * <p>
     * 策略：
     * - 若存在 `deleted` 字段，则执行软删除（更新标记）
     * - 否则执行物理删除（直接删除记录）
     * <p>
     * 流程：
     * 1. 前置工作流
     * 2. 解析主键并切换数据源
     * 3. 按策略执行删除
     * 4. 清理数据源并执行后置工作流
     */
    public Integer delete(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (entity == null || value == null) {
            return 0;
        }
        // 前置工作流：权限校验、审计记录准备等
        semanticWorkflowExecutor.preExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                value.getFieldValueMap() == null ? List.of() : value.getFieldValueMap().values().stream().toList(),
                List.of()
        );
        // 确定主键字段名并解析 id
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        Object id = value.getId() != null ? value.getId() : value.getCurrentEntityRawMap().get(pkField);
        if (id == null) {
            return 0;
        }

        // 构建主键条件并执行删除（软删优先）
        Object dv = toLongIfNotEmpty(id);
        QueryWrapper qw = QueryWrapper.create().where(new QueryColumn(pkField).eq(dv != null ? dv : id));
        int resultCount = 0;
        if (hasDeletedField(entity.getFields())) {
            resultCount = dynamicMetadataRepository.softDeleteByQuery(entity.getTableName(), qw, entity.getFields());
        } else {
            resultCount = dynamicMetadataRepository.deleteByQuery(entity.getTableName(), qw, entity.getFields());
        }
        List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
        if (connectors != null && !connectors.isEmpty()) {
            for (SemanticRelationSchemaDTO c : connectors) {
                if (c == null || c.getTargetEntityTableName() == null) {
                    continue;
                }
                if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                    Object pv = toLongIfNotEmpty(id);
                    QueryWrapper cq = QueryWrapper.create().where(new QueryColumn("parent_id").eq(pv != null ? pv : id));
                    boolean hasDel = hasDeletedField(c.getRelationAttributes());
                    if (hasDel) {
                        dynamicMetadataRepository.softDeleteByQuery(c.getTargetEntityTableName(), cq, entity.getFields());
                    } else {
                        dynamicMetadataRepository.deleteByQuery(c.getTargetEntityTableName(), cq, entity.getFields());
                    }
                }
            }
        }
        // 后置工作流：审计、事件发布等
        semanticWorkflowExecutor.postExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                value.getFieldValueMap() == null ? List.of() : value.getFieldValueMap().values().stream().toList(),
                List.of()
        );
        return resultCount;
    }

    /**
     * 条件删除主表数据（软删优先，物理删回退）
     */
    public Integer deleteByCondition(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        semanticWorkflowExecutor.preExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                List.of(),
                List.of()
        );
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        SemanticConditionDTO cond = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getFilters();
        boolean noCond = cond == null
                || (((cond.getFieldName() == null || cond.getFieldName().isBlank()) && cond.getFieldUuid() == null))
                || cond.getFieldValue() == null || cond.getFieldValue().isEmpty();
        if (noCond) {
            return 0;
        }
        QueryWrapper qw = QueryWrapper.create();
        semanticQueryConditionBuilder.apply(qw, fields, cond, null);
        qw = semanticQueryPermissionHelper.applyQueryPermissionFilter(qw, recordDTO.getRecordContext().getPermissionContext(), fields);
        int affected = hasDeletedField(fields)
                ? dynamicMetadataRepository.softDeleteByQuery(entity.getTableName(), qw, entity.getFields())
                : dynamicMetadataRepository.deleteByQuery(entity.getTableName(), qw, entity.getFields());
        semanticWorkflowExecutor.postExecute(
                recordDTO.getRecordContext().getOperationType(),
                recordDTO.getRecordContext().getTraceId(),
                entity.getTableName(),
                List.of(),
                List.of()
        );
        return affected;
    }

    /**
     * 按主键读取一条主表数据
     * <p>
     * 流程：
     * 1. 切换数据源并按主键读取主表 Row（考虑软删除字段）
     * 2. 回填字段值到 Map 以便 JSON 输出
     * 3. 将 Row 转换为 `SemanticEntityValueDTO`
     * 4. 读取并填充子表与关系表连接器的值
     * 5. 通过引用解析器进行富化（字典/引用字段）
     * 6. 设置到 `recordDTO` 并返回可序列化的结果
     * <p>
     * 注意：
     * - 若主表记录不存在，返回 null
     */
    public Map<String, Object> readById(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        String pkField = getPrimaryKeyFieldName(entity.getFields());
        Object id = recordDTO.getEntityValue().getId();
        // 切换到目标数据源
        try {
            // 查询主表记录；若存在软删除字段则进行条件过滤
            Row row = dynamicMetadataRepository.selectMainById(entity.getTableName(), pkField, id, hasDeletedField(entity.getFields()), entity.getFields());
            if (row == null) {
                return null;
            }

            Map<String, Object> result = new HashMap<>();
            List<SemanticFieldSchemaDTO> fieldsDto = entity.getFields();
            if (fieldsDto != null) {
                for (SemanticFieldSchemaDTO f : fieldsDto) {
                    String name = f.getFieldName();
                    // 仅回填具有字段名的列
                    if (name != null) {
                        result.put(name, row.get(name));
                    }
                }
            }

            // 将行数据转换为语义化的实体值对象
            SemanticEntityValueDTO resultVal = semanticValueAssembler.toEntityValue(entity, row);

            List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
            if (connectors != null && !connectors.isEmpty()) {
                Map<String, SemanticRelationValueDTO> connVals = new HashMap<>();
                for (SemanticRelationSchemaDTO c : connectors) {
                    if (c == null || c.getTargetEntityTableName() == null) {
                        continue;
                    }
                    if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                        // 读取子表连接器的值
                        SemanticRelationValueDTO rv = readSubtableConnector(c, id, entity.getFields());
                        if (rv != null) {
                            connVals.put(c.getTargetEntityTableName(), rv);
                        }
                    } else if (RelationshipTypeEnum.isConnectorRelationTable(c.getRelationshipType().getRelationshipType())) {
                        // 读取关系表连接器的值
                        SemanticFieldSchemaDTO targetFiledSchema = c.getRelationAttributes().stream().filter(attr ->
                                attr.getFieldUuid().equals(c.getTargetKeyFieldUuid())).findFirst().orElse(null);
                        if (targetFiledSchema == null) continue;
                        String targetFiledName = targetFiledSchema.getFieldName();
                        SemanticFieldSchemaDTO sourceFiledSchema = entity.getFields().stream().filter(filed ->
                                filed.getFieldUuid().equals(c.getSourceKeyFieldUuid())).findFirst().orElse(null);
                        if (sourceFiledSchema == null) continue;
                        Object targetFiledValue = resultVal.getFieldValueMap().get(sourceFiledSchema.getFieldName()).getStoreValue();
                        SemanticRelationValueDTO rv = readRelationConnector(c, targetFiledName, targetFiledValue);
                        if (rv != null) {
                            connVals.put(c.getTargetEntityTableName(), rv);
                        }
                    }
                }
                resultVal.setConnectors(connVals);
            }

            // 引用解析与富化（如字典翻译、外键名称回填）
            semanticRefResolver.enrich(entity, resultVal);
            // 设置读取结果到上下文，用于统一输出
            recordDTO.setResultValue(resultVal);
            // 转换为可序列化的 Map（适配前端 JSON 输出）
            Map<String, Object> resultData = recordDTO.getResultValue().getGlobalRawMapForJson();
            return resultData;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 分页查询主表数据（接收外部构建的 QueryWrapper）
     * <p>
     * - 支持执行器或外部策略构建 `QueryWrapper`
     * - 统一处理分页参数、行到语义值转换、引用富化与字段权限过滤
     *
     * @param recordDTO 语义记录上下文
     * @param qw        已构建的查询条件包装器
     * @return 分页结果（已进行引用解析与字段权限过滤）
     */
    public PageResult<Map<String, Object>> queryPage(SemanticRecordDTO recordDTO, QueryWrapper qw) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        Integer pageNo = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getPageNo();
        Integer pageSize = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getPageSize();
        int pn = pageNo == null ? 1 : pageNo;
        int ps = pageSize == null ? 10 : pageSize;
        PageResult<Row> pageRows = dynamicMetadataRepository.selectPageByQuery(entity.getTableName(), qw, pn, ps, entity.getFields());
        List<Row> rows = pageRows.getList();
        List<Map<String, Object>> result = new ArrayList<>();
        List<SemanticEntityValueDTO> values = new ArrayList<>();
        for (Row row : rows) {
            values.add(semanticValueAssembler.toEntityValue(entity, row));
        }
        semanticRefResolver.enrichBatch(entity, values);
        for (SemanticEntityValueDTO val : values) {
            result.add(val.getGlobalRawMapForJson());
        }
        result = semanticQueryPermissionHelper.filterQueryResultList(result, recordDTO.getRecordContext().getPermissionContext(), fields);
        return new PageResult<>(result, pageRows.getTotal());
    }

    /**
     * 条件批量删除主表数据（逐条执行）
     * <p>
     * 设计与约束：
     * - 仅处理主表记录；子表与关系表的级联清理在单条删除逻辑中按连接器类型处理
     * - 使用分页查询（第1页，pageSize=MAX_BATCH_LIMIT）拉取待删记录并逐条调用 {@link #delete(SemanticRecordDTO)}
     * - 保留工作流钩子与权限过滤等单条删除的行为一致性
     * - 若实体存在标准软删字段（deleted），则走软删；否则物理删除
     *
     * @param recordDTO 语义记录上下文，需包含实体元数据
     * @param qw        条件包装器；为空时创建默认条件
     * @return 实际受影响的主表记录数
     */
    public Integer deleteByQuery(SemanticRecordDTO recordDTO, QueryWrapper qw) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        if (qw == null) {
            qw = QueryWrapper.create();
        }
        int pageSize = MAX_BATCH_LIMIT;
        int affected = 0;
        PageResult<Row> pageRows = dynamicMetadataRepository.selectPageByQuery(entity.getTableName(), qw, 1, pageSize, entity.getFields());
        List<Row> rows = pageRows.getList();
        if (rows != null) {
            for (Row row : rows) {
                SemanticEntityValueDTO val = new SemanticEntityValueDTO();
                val.setId(row.get("id"));
                recordDTO.setEntityValue(val);
                Integer r = delete(recordDTO);
                affected += r == null ? 0 : r;
            }
        }
        return affected;
    }

    /**
     * 条件批量更新主表数据（逐条执行并回读）
     * <p>
     * 设计与约束：
     * - 仅处理主表字段更新；子表与关系表的 upsert 逻辑在单条更新中执行
     * - 使用分页查询（第1页，pageSize=MAX_BATCH_LIMIT）拉取目标记录，逐条构建更新字段并调用 {@link #update(SemanticRecordDTO)}
     * - 每条更新后调用 {@link #readById(SemanticRecordDTO)} 回读最新数据，最终返回聚合后的结果列表
     * - 字段权限过滤在返回前统一处理
     *
     * @param recordDTO 语义记录上下文，需包含实体元数据
     * @param updates   待更新字段的原始值映射（key 为字段名）
     * @param qw        条件包装器；为空时创建默认条件
     * @return 批量更新后的主表记录结果集（已做权限过滤）
     */
    public List<Map<String, Object>> updateByQuery(SemanticRecordDTO recordDTO, Map<String, Object> updates, QueryWrapper qw) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<SemanticFieldSchemaDTO> fields = entity.getFields();
        if (updates == null || updates.isEmpty()) {
            return List.of();
        }
        if (qw == null) {
            qw = QueryWrapper.create();
        }
        int pageSize = MAX_BATCH_LIMIT;
        List<Map<String, Object>> result = new ArrayList<>();
        PageResult<Row> pageRows = dynamicMetadataRepository.selectPageByQuery(entity.getTableName(), qw, 1, pageSize, entity.getFields());
        List<Row> rows = pageRows.getList();
        if (rows != null) {
            for (Row row : rows) {
                SemanticEntityValueDTO val = new SemanticEntityValueDTO();
                val.setId(row.get("id"));
                Map<String, SemanticFieldValueDTO<Object>> fvm = new HashMap<>();
                for (SemanticFieldSchemaDTO f : fields) {
                    String name = f.getFieldName();
                    if (name != null && updates.containsKey(name)) {
                        SemanticFieldValueDTO<Object> fv = SemanticFieldValueDTO.ofType(f.getFieldTypeEnum());
                        fv.setFieldName(name);
                        fv.setTableName(entity.getTableName());
                        fv.setRawValue(updates.get(name));
                        fvm.put(name, fv);
                    }
                }
                val.setFieldValueMap(fvm);
                recordDTO.setEntityValue(val);
                update(recordDTO);
            }
        }
        result = semanticQueryPermissionHelper.filterQueryResultList(result, recordDTO.getRecordContext().getPermissionContext(), fields);
        return result;
    }

    /**
     * 构建分页查询条件
     * <p>
     * - 过滤：仅对实体存在的字段生效；字符串使用 like，其它类型使用等值
     * - 排序：按请求排序；未指定时回退主键倒序
     * - 软删：存在 `deleted` 字段时追加 `deleted = 0`
     *
     * @param recordDTO 语义记录上下文
     * @param fields    实体字段集合
     * @return 构建完成的 `QueryWrapper`
     */
    private QueryWrapper buildPageQueryWrapper(SemanticRecordDTO recordDTO, List<SemanticFieldSchemaDTO> fields) {
        QueryWrapper qw = QueryWrapper.create();
        SemanticConditionDTO filters = recordDTO.getRecordContext().getFilters();
        List<SemanticSortRuleDTO> sortBy = recordDTO.getRecordContext() == null ? null : recordDTO.getRecordContext().getSortBy();
        semanticQueryConditionBuilder.apply(qw, fields, filters, sortBy);
        return qw;
    }

    /**
     * 构建更新 Row
     * <p>
     * - 提取待更新字段的存储值并填充到 Row
     * - 对系统字段（如 `updated_time`、`updater`）进行占位填充，由数据库处理默认值
     *
     * @param entity 实体元数据
     * @param value  当前值对象
     * @return 仅包含待更新列的 Row
     */
    private Row buildUpdateRow(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value) {
        Row row = new Row();
        List<SemanticFieldSchemaDTO> fieldsDto = entity.getFields();
        if (fieldsDto != null) {
            for (SemanticFieldSchemaDTO f : fieldsDto) {
                String name = f.getFieldName();
                if (name == null) {
                    continue;
                }
                SemanticFieldValueDTO<Object> v = value.getFieldValueByTableAndField(entity.getTableName(), name);
                if (v == null) {
                    continue;
                }
                Object sv = v.getStoreValue();
                if (sv != null) {
                    row.put(name, sv);
                }
            }
            boolean hasUpdatedTime = fieldsDto.stream().anyMatch(f -> {
                String n = f.getFieldName();
                return n != null && (SystemFieldConstants.OPTIONAL.UPDATED_TIME.equalsIgnoreCase(n));
            });
            boolean hasUpdater = fieldsDto.stream().anyMatch(f -> {
                String n = f.getFieldName();
                return n != null && (SystemFieldConstants.REQUIRE.UPDATER.equalsIgnoreCase(n));
            });
            if (hasUpdatedTime) {
                if (!row.containsKey(SystemFieldConstants.OPTIONAL.UPDATED_TIME)) {
                    row.put(SystemFieldConstants.OPTIONAL.UPDATED_TIME, null);
                }
            }
            if (hasUpdater && !row.containsKey(SystemFieldConstants.REQUIRE.UPDATER)) {
                row.put(SystemFieldConstants.REQUIRE.UPDATER, null);
            }
        }
        return row;
    }

    /**
     * 读取子表连接器数据
     * <p>
     * - 基于父主键读取目标子表数据集
     * - ONE：返回首行映射；MANY：返回列表映射
     *
     * @param c        连接器定义
     * @param parentId 父记录主键
     * @return 连接器值（可能为 null）
     */
    private SemanticRelationValueDTO readSubtableConnector(SemanticRelationSchemaDTO c, Object parentId, List<SemanticFieldSchemaDTO> fields) {
        List<Row> rows = dynamicMetadataRepository.selectSubtableRowsByParent(c.getTargetEntityTableName(), parentId, fields);
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        SemanticRelationValueDTO relation = new SemanticRelationValueDTO();
        List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
        if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
            Row r = rows.get(0);
            SemanticRowValueDTO rowDto = semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName());
            relation.setRowValue(rowDto);
        } else {
            List<SemanticRowValueDTO> list = new ArrayList<>();
            for (Row r : rows) {
                list.add(semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName()));
            }
            relation.setRowValueList(list);
        }
        return relation;
    }

    /**
     * 读取关系表连接器数据
     * <p>
     * - 使用关系表父键 `parent_id` 进行查询
     * - ONE：返回首行映射；MANY：返回列表映射
     *
     * @param c   连接器定义
     * @param key value 关联字段名称 值
     * @return 连接器值（可能为 null）
     */
    private SemanticRelationValueDTO readRelationConnector(SemanticRelationSchemaDTO c, String key, Object value) {
        List<Row> rows = dynamicMetadataRepository.selectRelationRowsByCondition(c.getTargetEntityTableName(), key, value, true);
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        SemanticRelationValueDTO relation = new SemanticRelationValueDTO();
        List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
        if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
            Row r = rows.get(0);
            SemanticRowValueDTO rowDto = semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName());
            relation.setRowValue(rowDto);
        } else {
            List<SemanticRowValueDTO> list = new ArrayList<>();
            for (Row r : rows) {
                list.add(semanticValueAssembler.toRowValue(r, attrs, c.getTargetEntityTableName()));
            }
            relation.setRowValueList(list);
        }
        return relation;
    }

    /**
     * 推断主键字段名
     * 优先：显式主键且名称为 id；其次：任意显式主键；最后：回退为 id
     */
    private String getPrimaryKeyFieldName(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) {
            return "id";
        }
        Optional<String> idNamed = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) {
            return idNamed.get();
        }
        Optional<String> firstPk = fields.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsPrimaryKey()))
                .map(SemanticFieldSchemaDTO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) {
            return firstPk.get();
        }
        boolean hasId = fields.stream().map(SemanticFieldSchemaDTO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) {
            return "id";
        }
        return "id";
    }

    /**
     * 判断是否存在标准软删除字段 deleted
     */
    private boolean hasDeletedField(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null) {
            return false;
        }
        return fields.stream().anyMatch(f -> SystemFieldConstants.OPTIONAL.DELETED.equalsIgnoreCase(f.getFieldName()));
    }

    /**
     * 生成并应用主表自动编号字段
     * <p>
     * - 收集类型为 `AUTO_CODE` 的字段 ID
     * - 调用编号服务生成编码并写回原始值（rawValue）
     *
     * @param fields 字段集合
     * @param value  当前值对象
     */
    private void generateAndApplyAutoNumbers(List<SemanticFieldSchemaDTO> fields, SemanticEntityValueDTO value) {
        List<String> fieldIds = fields.stream()
                .filter(f -> Objects.equals(f.getFieldTypeEnum(), AUTO_CODE))
                .map(SemanticFieldSchemaDTO::getFieldUuid)
                .toList();
        Map<String, String> autoNumbers = autoNumberService.generateDataNumbers(fieldIds, value.getCurrentEntityRawMap());
        for (String key : autoNumbers.keySet()) {
            String filedName = fields.stream().filter(semanticFieldSchemaDTO ->
                    semanticFieldSchemaDTO.getFieldUuid().equals(key)
            ).map(SemanticFieldSchemaDTO::getFieldName).findFirst().orElse("");

//            Long filedId = fields.stream().filter(semanticFieldSchemaDTO ->
//                    semanticFieldSchemaDTO.getFieldUuid().equals(key)
//            ).map(SemanticFieldSchemaDTO::getId).findFirst().orElse(0l);

            SemanticFieldValueDTO<java.lang.Object> semanticFieldValueDTO = new SemanticFieldValueDTO<java.lang.Object>(AUTO_CODE);
            semanticFieldValueDTO.setFieldId(null);
            semanticFieldValueDTO.setFieldUuid(key);
            semanticFieldValueDTO.setFieldName(filedName);
            semanticFieldValueDTO.setRawValue(autoNumbers.get(key).toString());
            value.getFieldValueMap().put(filedName, semanticFieldValueDTO);
        }

    }

    /**
     * 汇总子表批次数据
     * <p>
     * - 针对 ONE/MANY 两类子表连接器分别构建 Row
     * - 在构建前应用连接器字段的自动编号策略
     *
     * @param recordDTO  语义记录上下文
     * @param connectors 连接器集合
     * @param parentId   父记录主键
     * @return 表名到 Row 列表的映射
     */
    private Map<String, List<Row>> collectSubRows(SemanticRecordDTO recordDTO, List<SemanticRelationSchemaDTO> connectors, Long parentId) {
        Map<String, List<Row>> batches = new HashMap<>();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) {
                continue;
            }
            if (!RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                continue;
            }
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> subDto = recordDTO.getEntityValue().getConnectorDTOObject(c.getTargetEntityTableName());
                applyConnectorAutoNumbers(c, subDto);
                if (subDto != null && !subDto.isEmpty()) {
                    batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>()).add(semanticValueAssembler.buildSubRow(subDto, parentId, uidGenerator));
                }
            } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(c.getTargetEntityTableName());
                if (list != null) {
                    List<Row> rows = batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>());
                    for (Map<String, SemanticFieldValueDTO<Object>> subDto : list) {
                        applyConnectorAutoNumbers(c, subDto);
                        if (subDto != null && !subDto.isEmpty()) {
                            rows.add(semanticValueAssembler.buildSubRow(subDto, parentId, uidGenerator));
                        }
                    }
                }
            }
        }
        return batches;
    }

    /**
     * 汇总关系表批次数据
     * <p>
     * - 针对 ONE/MANY 两类关系连接器分别构建 Row
     * - 在构建前应用连接器字段的自动编号策略
     *
     * @param recordDTO  语义记录上下文
     * @param connectors 连接器集合
     * @return 关系表名到 Row 列表的映射
     */
    private Map<String, List<Row>> collectRelationRows(SemanticRecordDTO recordDTO, List<SemanticRelationSchemaDTO> connectors) {
        Map<String, List<Row>> batches = new HashMap<>();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) {
                continue;
            }
            if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                continue;
            }
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> relDto = recordDTO.getEntityValue().getConnectorDTOObject(c.getTargetEntityTableName());
                applyConnectorAutoNumbers(c, relDto);
                if (relDto != null && !relDto.isEmpty()) {
                    batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>()).add(semanticValueAssembler.buildRelationRow(recordDTO, c, relDto, uidGenerator));
                }
            } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(c.getTargetEntityTableName());
                if (list != null) {
                    List<Row> rows = batches.computeIfAbsent(c.getTargetEntityTableName(), k -> new ArrayList<>());
                    for (Map<String, SemanticFieldValueDTO<Object>> relDto : list) {
                        applyConnectorAutoNumbers(c, relDto);
                        if (relDto != null && !relDto.isEmpty()) {
                            rows.add(semanticValueAssembler.buildRelationRow(recordDTO, c, relDto, uidGenerator));
                        }
                    }
                }
            }
        }
        return batches;
    }

    /**
     * 批量插入数据行
     * <p>
     * - 按表名分组批量插入，避免循环内单条插入的性能问题
     *
     * @param batches 表名到 Row 列表的映射
     */
    private void insertBatches(Map<String, List<Row>> batches) {
        for (Map.Entry<String, List<Row>> e : batches.entrySet()) {
            List<Row> rows = e.getValue();
            if (rows == null || rows.isEmpty()) {
                continue;
            }
            dynamicMetadataRepository.insertBatch(e.getKey(), rows);
        }
    }

    /**
     * 应用连接器字段的自动编号策略
     * <p>
     * - 收集属性中类型为 `AUTO_CODE` 的字段
     * - 生成编码并写回原始值（rawValue）
     *
     * @param connector 连接器定义
     * @param fields    字段值映射
     */
    private void applyConnectorAutoNumbers(SemanticRelationSchemaDTO connector, Map<String, SemanticFieldValueDTO<Object>> fields) {
        if (connector == null || fields == null) {
            return;
        }
        List<SemanticFieldSchemaDTO> attrs = connector.getRelationAttributes();
        if (attrs == null || attrs.isEmpty()) {
            return;
        }
        List<String> fieldIds = attrs.stream()
                .filter(f -> Objects.equals(f.getFieldTypeEnum(), AUTO_CODE))
                .map(SemanticFieldSchemaDTO::getFieldUuid)
                .toList();
        if (fieldIds.isEmpty()) {
            return;
        }
        Map<String, Object> raw = new HashMap<>();
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
            SemanticFieldValueDTO<Object> v = e.getValue();
            raw.put(e.getKey(), v == null ? null : v.getRawValue());
        }
        Map<String, String> autoNumbers = autoNumberService.generateDataNumbers(fieldIds, raw);
        for (Map.Entry<String, String> e : autoNumbers.entrySet()) {
            SemanticFieldValueDTO<Object> v = fields.get(e.getKey());
            if (v != null) {
                v.setRawValue(e.getValue());
            }
        }
    }

    private List<List<SemanticFieldValueDTO<Object>>> buildConnectorFieldValueBatches(SemanticRecordDTO recordDTO) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<SemanticRelationSchemaDTO> connectors = entity == null ? null : entity.getConnectors();
        List<List<SemanticFieldValueDTO<Object>>> batches = new ArrayList<>();
        if (connectors == null) {
            return batches;
        }
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) {
                continue;
            }
            String name = c.getTargetEntityTableName();
            Map<String, SemanticFieldValueDTO<Object>> obj = recordDTO.getEntityValue() == null ? null : recordDTO.getEntityValue().getConnectorDTOObject(name);
            if (obj != null && !obj.isEmpty()) {
                batches.add(obj.values().stream().toList());
            }
            List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue() == null ? null : recordDTO.getEntityValue().getConnectorDTOList(name);
            if (list != null) {
                for (Map<String, SemanticFieldValueDTO<Object>> m : list) {
                    if (m != null && !m.isEmpty()) {
                        batches.add(m.values().stream().toList());
                    }
                }
            }
        }
        return batches;
    }

    private void upsertSubtableConnectors(SemanticRecordDTO recordDTO, Long parentId) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) {
                continue;
            }
            if (!RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                continue;
            }
            String table = c.getTargetEntityTableName();
            List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
            String childPk = getPrimaryKeyFieldName(attrs);

            List<Row> existing = dynamicMetadataRepository.selectSubtableRonanwsByParent(table, parentId);
            Map<String, Row> existingById = new HashMap<>();
            for (Row r : existing) {
                Object rid = r.get(childPk);
                Object rv = toLongIfNotEmpty(rid);
                if (rv != null) {
                    existingById.put(String.valueOf(rv), r);
                }
            }

            List<Map<String, SemanticFieldValueDTO<Object>>> incoming = new ArrayList<>();
            List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(table);
            if (list != null) {
                incoming.addAll(list);
            }

            List<String> incomingIds = new ArrayList<>();
            for (Map<String, SemanticFieldValueDTO<Object>> dto : incoming) {
                Object cid = extractIdFromConnectorDto(dto);
                Object cv = toLongIfNotEmpty(cid);
                String keyStr = cv == null ? null : String.valueOf(cv);
                if (keyStr != null) {
                    incomingIds.add(keyStr);
                }
                if (keyStr != null && existingById.containsKey(keyStr)) {
                    Row updateRow = buildConnectorUpdateRow(attrs, dto, childPk);
                    QueryWrapper uq = QueryWrapper.create().where(new QueryColumn(childPk).eq(cv != null ? cv : cid));
                    dynamicMetadataRepository.updateByQuery(table, updateRow, uq);
                } else {
                    Row insertRow = semanticValueAssembler.buildSubRow(dto, parentId, uidGenerator);
                    dynamicMetadataRepository.insert(table, insertRow);
                }
            }

            List<String> existingIds = new ArrayList<>(existingById.keySet());
            List<String> toDelete = existingIds.stream().filter(x -> !incomingIds.contains(x)).toList();
            if (!toDelete.isEmpty()) {
                QueryWrapper dq = QueryWrapper.create()
                        .where(new QueryColumn("parent_id").eq(parentId))
                        .and(new QueryColumn(childPk).in(toLongListForIn(toDelete)));
                if (hasDeletedField(attrs)) {
                    dynamicMetadataRepository.softDeleteByQuery(table, dq, entity.getFields());
                } else {
                    dynamicMetadataRepository.deleteByQuery(table, dq, entity.getFields());
                }
            }
        }
    }

    private void upsertRelationConnectors(SemanticRecordDTO recordDTO, Long parentId) {
        SemanticEntitySchemaDTO entity = recordDTO.getEntitySchema();
        List<SemanticRelationSchemaDTO> connectors = entity.getConnectors();
        for (SemanticRelationSchemaDTO c : connectors) {
            if (c == null || c.getTargetEntityTableName() == null) {
                continue;
            }
            if (RelationshipTypeEnum.isSubtableRelationship(c.getRelationshipType().getRelationshipType())) {
                continue;
            }
            String table = c.getTargetEntityTableName();
            List<SemanticFieldSchemaDTO> attrs = c.getRelationAttributes();
            String pk = getPrimaryKeyFieldName(attrs);

            List<Row> existing = dynamicMetadataRepository.selectRelationRowsByParent(table, "parent_id", parentId, entity.getFields());
            Map<String, Row> existingById = new HashMap<>();
            for (Row r : existing) {
                Object rid = r.get(pk);
                Object rv = toLongIfNotEmpty(rid);
                if (rv != null) {
                    existingById.put(String.valueOf(rv), r);
                }
            }

            List<Map<String, SemanticFieldValueDTO<Object>>> incoming = new ArrayList<>();
            if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, SemanticFieldValueDTO<Object>> dto = recordDTO.getEntityValue().getConnectorDTOObject(table);
                if (dto != null && !dto.isEmpty()) {
                    incoming.add(dto);
                }
            } else {
                List<Map<String, SemanticFieldValueDTO<Object>>> list = recordDTO.getEntityValue().getConnectorDTOList(table);
                if (list != null) {
                    incoming.addAll(list);
                }
            }

            List<String> incomingIds = new ArrayList<>();
            for (Map<String, SemanticFieldValueDTO<Object>> dto : incoming) {
                Object rid = extractIdFromConnectorDto(dto);
                Object rv = toLongIfNotEmpty(rid);
                String keyStr = rv == null ? null : String.valueOf(rv);
                if (keyStr != null) {
                    incomingIds.add(keyStr);
                }
                if (keyStr != null && existingById.containsKey(keyStr)) {
                    Row updateRow = buildConnectorUpdateRow(attrs, dto, pk);
                    QueryWrapper uq = QueryWrapper.create().where(new QueryColumn(pk).eq(rv != null ? rv : rid));
                    dynamicMetadataRepository.updateByQuery(table, updateRow, uq);
                } else {
                    Row insertRow = semanticValueAssembler.buildRelationRow(recordDTO, c, dto, uidGenerator);
                    if (!insertRow.containsKey("parent_id")) {
                        insertRow.put("parent_id", parentId);
                    }
                    dynamicMetadataRepository.insert(table, insertRow);
                }
            }

            List<String> existingIds = new ArrayList<>(existingById.keySet());
            List<String> toDelete = existingIds.stream().filter(x -> !incomingIds.contains(x)).toList();
            if (!toDelete.isEmpty()) {
                QueryWrapper dq = QueryWrapper.create()
                        .where(new QueryColumn("parent_id").eq(parentId))
                        .and(new QueryColumn(pk).in(toLongListForIn(toDelete)));
                if (hasDeletedField(attrs)) {
                    dynamicMetadataRepository.softDeleteByQuery(table, dq, entity.getFields());
                } else {
                    dynamicMetadataRepository.deleteByQuery(table, dq, entity.getFields());
                }
            }
        }
    }

    private Row buildConnectorUpdateRow(List<SemanticFieldSchemaDTO> attrs, Map<String, SemanticFieldValueDTO<Object>> dto, String pkField) {
        Row row = new Row();
        if (attrs == null || dto == null || dto.isEmpty()) {
            return row;
        }
        for (SemanticFieldSchemaDTO a : attrs) {
            String name = a.getFieldName();
            if (name == null) {
                continue;
            }
            if (pkField != null && name.equalsIgnoreCase(pkField)) {
                continue;
            }
            SemanticFieldValueDTO<Object> v = dto.get(name);
            if (v == null) {
                continue;
            }
            Object sv = v.getStoreValue();
            if (sv != null) {
                row.put(name, sv);
            }
        }
        boolean hasUpdatedTime = attrs.stream().anyMatch(f -> {
            String n = f.getFieldName();
            return n != null && SystemFieldConstants.OPTIONAL.UPDATED_TIME.equalsIgnoreCase(n);
        });
        boolean hasUpdater = attrs.stream().anyMatch(f -> {
            String n = f.getFieldName();
            return n != null && (SystemFieldConstants.REQUIRE.UPDATER.equalsIgnoreCase(n));
        });
        if (hasUpdatedTime && !row.containsKey(SystemFieldConstants.OPTIONAL.UPDATED_TIME)) {
            row.put(SystemFieldConstants.OPTIONAL.UPDATED_TIME, null);
        }
        if (hasUpdater && !row.containsKey(SystemFieldConstants.REQUIRE.UPDATER)) {
            row.put(SystemFieldConstants.REQUIRE.UPDATER, null);
        }
        return row;
    }

    private Object extractIdFromConnectorDto(Map<String, SemanticFieldValueDTO<Object>> dto) {
        if (dto == null || dto.isEmpty()) {
            return null;
        }
        SemanticFieldValueDTO<Object> idVal = dto.get("id");
        if (idVal != null) {
            Object sv = idVal.getStoreValue();
            if (sv != null) {
                return sv;
            }
            Object rv = idVal.getRawValue();
            if (rv != null) {
                return rv;
            }
        }
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : dto.entrySet()) {
            if ("id".equalsIgnoreCase(e.getKey())) {
                SemanticFieldValueDTO<Object> v = e.getValue();
                if (v == null) {
                    continue;
                }
                Object sv = v.getStoreValue();
                if (sv != null) {
                    return sv;
                }
                Object rv = v.getRawValue();
                if (rv != null) {
                    return rv;
                }
            }
        }
        return null;
    }

    private List<Object> toLongListForIn(List<?> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.List.of();
        }
        java.util.List<Object> out = new java.util.ArrayList<>(ids.size());
        for (Object id : ids) {
            Object v = toLongIfNotEmpty(id);
            if (v != null) {
                out.add(v);
            }
        }
        return out;
    }

    private Object toLongIfNotEmpty(Object value) {
        if (value == null) {
            return null;
        }
        String s = String.valueOf(value).trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (Exception ignored) {
            return value;
        }
    }
}
