package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
// MetadataDataSystemMethodDO 已由查询功能迁移至 build 模块，核心仅保留运行时 CRUD
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.engine.MultiTableQueryEngine;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.PageNavi;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

/**
 * 数据方法 Service 核心实现类 - 只处理基础数据操作，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
@Slf4j
public class MetadataDataMethodCoreServiceImpl implements MetadataDataMethodCoreService {

    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Resource
    private MetadataDataSystemMethodCoreService metadataDataSystemMethodService; // 仍用于多表计划获取

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Resource
    private MultiTableQueryEngine multiTableQueryEngine;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService autoNumberService;
    // ========== 动态数据操作方法实现 ==========
    // ========== 动态数据操作方法实现 ==========

    @Override
    public Map<String, Object> createData(Long entityId, Map<String, Object> data, String methodCode) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);

        // 2. 获取实体字段信息
    List<MetadataEntityFieldDO> fields = getEntityFields(entityId);

        // 3. 校验数据完整性
        validateDataForCreate(data, fields);

        // 4. 处理数据并设置默认值
        Map<String, Object> processedData = processDataForCreate(data, fields);

        // 5. 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 6. 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
        return TenantUtils.executeIgnore(() -> {

        // 7. 执行插入
        if (log.isDebugEnabled()) {
            log.debug("createData -> processedData before insert: {}", processedData);
        }
        DataRow dataRow = new DataRow(processedData);
        Object insertResult = temporaryService.insert(quoteTableName(entity.getTableName()), dataRow);
        log.info("创建数据成功，实体ID: {}, 表名: {}, 插入结果: {}", entityId, entity.getTableName(), insertResult);

        // 8. 查询插入后的完整数据
        Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
        log.info("从处理数据中获取主键值: {}, 插入结果: {}", primaryKeyValue, insertResult);

        // 确保主键值不为null
        if (primaryKeyValue == null) {
            log.warn("无法获取主键值，跳过查询插入后的数据，实体ID: {}, 表名: {}", entityId, entity.getTableName());
            // 返回插入的数据
            return buildDataResponse(entity, processedData, fields);
        }

        Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), primaryKeyValue, fields);

        // 9. 构建响应（移除多表写入逻辑，直接返回结果）
        return buildDataResponse(entity, resultData, fields);

        }); // TenantUtils.executeIgnore 闭合
    }

    @Override
    public Map<String, Object> updateData(Long entityId, Object id, Map<String, Object> data, String methodCode) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);

        // 2. 获取实体字段信息
    List<MetadataEntityFieldDO> fields = getEntityFields(entityId);

        // 3. 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 4. 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
        return TenantUtils.executeIgnore(() -> {

        // 5. 校验数据存在
        validateDataExistsWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);

        // 6. 校验更新数据
        validateDataForUpdate(data, fields);

        // 7. 处理更新数据
        Map<String, Object> processedData = processDataForUpdate(data, fields);

        // 8. 获取主键字段名
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        // 9. 构建更新条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, id);

        // 10. 执行更新
        DataRow dataRow = new DataRow(processedData);
        long updateCount = temporaryService.update(quoteTableName(entity.getTableName()), dataRow, configStore);
        log.info("更新数据成功，实体ID: {}, 表名: {}, 更新记录数: {}", entityId, entity.getTableName(), updateCount);

        // 11. 查询更新后的完整数据
        Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);

        // 12. 构建响应（移除多表写入逻辑，直接返回结果）
        return buildDataResponse(entity, resultData, fields);

        }); // TenantUtils.executeIgnore 闭合
    }

    @Override
    public Boolean deleteData(Long entityId, Object id, String methodCode) {
        // 1. 校验实体存在
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);

        // 2. 获取实体字段信息
    List<MetadataEntityFieldDO> fields = getEntityFields(entityId);

        // 3. 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 4. 检查表中是否有软删除字段
        boolean hasDeletedField = fields.stream()
                .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));

    // 5. 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
    return TenantUtils.executeIgnore(() -> {

            // 6. 校验数据存在
            validateDataExistsWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);

            // 7. 获取主键字段名
            String primaryKeyField = getPrimaryKeyFieldName(fields);

            // 8. 构建删除条件
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and(primaryKeyField, id);

            long deleteCount;
            if (hasDeletedField) {
                // 软删除：更新deleted字段为删除时间戳
                DataRow updateData = new DataRow();
                updateData.put("deleted", String.valueOf(System.currentTimeMillis()));
                deleteCount = temporaryService.update(quoteTableName(entity.getTableName()), updateData, configStore);
                log.info("软删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
            } else {
                // 物理删除：直接删除记录
                deleteCount = temporaryService.delete(quoteTableName(entity.getTableName()), configStore);
                log.info("物理删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
            }

            boolean ok = deleteCount > 0;
            // 移除多表写入逻辑，直接返回删除结果
            return ok;
        }); // TenantUtils.executeIgnore 闭合
    }

    @Override
    public Map<String, Object> getData(Long entityId, Object id, String methodCode) {
        // 移除多表查询逻辑，直接使用单表查询
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        return TenantUtils.executeIgnore(() -> {
            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);
            if (resultData == null || resultData.isEmpty()) {
                throw exception(BUSINESS_ENTITY_NOT_EXISTS);
            }
            return buildDataResponse(entity, resultData, fields);
        });
    }

    @Override
    public PageResult<Map<String, Object>> getDataPage(Long entityId, Integer pageNo, Integer pageSize,
                                                       String sortField, String sortDirection,
                                                       Map<String, Object> filters, String methodCode) {
        // 添加调试日志
        log.info("核心服务分页查询参数 - entityId: {}, pageNo: {}, pageSize: {}, pageSize类型: {}", 
                 entityId, pageNo, pageSize, pageSize != null ? pageSize.getClass().getSimpleName() : "null");
                 
        // 移除多表查询逻辑，直接使用单表分页
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        boolean hasDeletedField = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));

        return TenantUtils.executeIgnore(() -> {
            ConfigStore configs = new DefaultConfigStore();
            boolean deletedConditionAdded = false;
            if (filters != null && !filters.isEmpty()) {
                Set<String> names = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String rawKey = entry.getKey();
                    Object rawVal = entry.getValue();
                    if (rawVal == null) {
                        continue;
                    }
                    if ("deleted".equalsIgnoreCase(rawKey) || "tenant_id".equalsIgnoreCase(rawKey)) {
                        continue;
                    }
                    // 兼容上层传入的结构：可能是直接 fieldName -> value，也可能是 conditionKey -> {fieldName, operator, value}
                    if (rawVal instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> cond = (Map<String,Object>) rawVal;
                        String fieldName = (String) cond.getOrDefault("fieldName", rawKey);
                        Object value = cond.get("value");
                        String operator = cond.get("operator") != null ? String.valueOf(cond.get("operator")) : "CONTAINS";
                        if (value == null || !names.contains(fieldName)) {
                            continue;
                        }
                        applyOperatorCondition(configs, fieldName, operator, value);
                    } else {
                        // 退化：直接LIKE
                        if (names.contains(rawKey)) {
                            configs.and(Compare.LIKE, rawKey, rawVal);
                        }
                    }
                }
            }
            if (hasDeletedField && !deletedConditionAdded) {
                configs.and(Compare.EQUAL, "deleted", 0);
                deletedConditionAdded = true;
            }
            Set<String> fieldNames = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
            if (StringUtils.hasText(sortField) && fieldNames.contains(sortField)) {
                String orderClause = sortField;
                orderClause += "desc".equalsIgnoreCase(sortDirection) ? " DESC" : " ASC";
                configs.order(orderClause);
            } else {
                String primaryKeyField = getPrimaryKeyFieldName(fields);
                configs.order(primaryKeyField + " DESC");
            }
            if (pageNo != null && pageSize != null) {
                PageNavi page = new DefaultPageNavi(pageNo, pageSize);
                configs.setPageNavi(page);
                log.info("设置分页参数 - pageNo: {}, pageSize: {}", pageNo, pageSize);
            }
            ConfigStore countConfigs = new DefaultConfigStore();
            if (filters != null && !filters.isEmpty()) {
                Set<String> existingFieldNames = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String rawKey = entry.getKey();
                    Object rawVal = entry.getValue();
                    if (rawVal == null) { continue; }
                    if ("deleted".equalsIgnoreCase(rawKey) || "tenant_id".equalsIgnoreCase(rawKey)) { continue; }
                    if (rawVal instanceof Map) {
                        @SuppressWarnings("unchecked") Map<String,Object> cond = (Map<String,Object>) rawVal;
                        String fieldName = (String) cond.getOrDefault("fieldName", rawKey);
                        Object value = cond.get("value");
                        String operator = cond.get("operator") != null ? String.valueOf(cond.get("operator")) : "CONTAINS";
                        if (value == null || !existingFieldNames.contains(fieldName)) { continue; }
                        // count 语句保守处理：范围/比较用相同 Compare，模糊仍用 LIKE
                        applyOperatorCondition(countConfigs, fieldName, operator, value);
                    } else {
                        if (existingFieldNames.contains(rawKey)) {
                            countConfigs.and(Compare.LIKE, rawKey, rawVal);
                        }
                    }
                }
            }
            if (hasDeletedField) {
                countConfigs.and(Compare.EQUAL, "deleted", 0);
            }
            long total = temporaryService.count(quoteTableName(entity.getTableName()), countConfigs);
            DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), configs);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = convertDataRowToMap(row, fields);
                list.add(buildDataResponse(entity, data, fields));
            }
            return new PageResult<>(list, total);
        });
    }

    @Override
    public PageResult<Map<String, Object>> getDataPageOr(Long entityId, Integer pageNo, Integer pageSize,
                                                         String sortField, String sortDirection,
                                                         List<Map<String,Object>> orConditionGroups,
                                                         String methodCode) {
        log.info("OR复合查询(单SQL) 参数 - entityId:{}, orGroups:{}, pageNo:{}, pageSize:{}", entityId,
                orConditionGroups == null ? 0 : orConditionGroups.size(), pageNo, pageSize);

        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) { throw exception(DATASOURCE_NOT_EXISTS); }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        boolean hasDeletedField = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));

        return TenantUtils.executeIgnore(() -> {
            DefaultConfigStore root = new DefaultConfigStore();
            // 处理 OR 组
            if (orConditionGroups != null && !orConditionGroups.isEmpty()) {
                if (orConditionGroups.size() == 1) {
                    // 只有一组，直接当 AND 处理
                    appendAndGroup(root, orConditionGroups.get(0), fields);
                } else {
                    // 多组 OR
                    List<ConfigStore> subStores = new ArrayList<>();
                    for (int i = 0; i < orConditionGroups.size(); i++) {
                        DefaultConfigStore sub = new DefaultConfigStore();
                        appendAndGroup(sub, orConditionGroups.get(i), fields);
                        subStores.add(sub);
                    }
                    // Anyline OR 组合：将子 store 添加为 OR 逻辑
                    for (ConfigStore sub : subStores) {
                        root.or(sub);
                    }
                }
            }

            if (hasDeletedField) {
                root.and(Compare.EQUAL, "deleted", 0);
            }

            // 排序
            Set<String> fieldNames = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
            if (StringUtils.hasText(sortField) && fieldNames.contains(sortField)) {
                root.order(sortField + ("desc".equalsIgnoreCase(sortDirection) ? " DESC" : " ASC"));
            } else {
                root.order(getPrimaryKeyFieldName(fields) + " DESC");
            }

            if (pageNo != null && pageSize != null) {
                PageNavi page = new DefaultPageNavi(pageNo, pageSize);
                root.setPageNavi(page);
            }

            // 统计总数：复制 root 条件
            // 重建一次统计条件（不含分页）
            DefaultConfigStore countStore = new DefaultConfigStore();
            if (orConditionGroups != null && !orConditionGroups.isEmpty()) {
                if (orConditionGroups.size() == 1) {
                    appendAndGroup(countStore, orConditionGroups.get(0), fields);
                } else {
                    List<ConfigStore> subStores = new ArrayList<>();
                    for (Map<String,Object> grp : orConditionGroups) {
                        DefaultConfigStore sub = new DefaultConfigStore();
                        appendAndGroup(sub, grp, fields);
                        subStores.add(sub);
                    }
                    for (ConfigStore sub : subStores) {
                        countStore.or(sub);
                    }
                }
            }
            if (hasDeletedField) {
                countStore.and(Compare.EQUAL, "deleted", 0);
            }
            long total = temporaryService.count(quoteTableName(entity.getTableName()), countStore);
            DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), root);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = convertDataRowToMap(row, fields);
                list.add(buildDataResponse(entity, data, fields));
            }
            return new PageResult<>(list, total);
        });
    }

    /**
     * 将 OR 单组内的 AND 条件追加到 ConfigStore
     */
    private void appendAndGroup(DefaultConfigStore store, Map<String,Object> andGroup, List<MetadataEntityFieldDO> fields) {
        if (andGroup == null || andGroup.isEmpty()) return;
        Set<String> names = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
        for (Object val : andGroup.values()) {
            if (!(val instanceof Map)) continue;
            @SuppressWarnings("unchecked") Map<String,Object> cond = (Map<String,Object>) val;
            String fieldName = (String) cond.get("fieldName");
            Object value = cond.get("value");
            String operator = cond.get("operator") != null ? String.valueOf(cond.get("operator")) : "CONTAINS";
            if (!names.contains(fieldName)) continue;
            // 根据 operator 添加条件
            addCompare(store, fieldName, operator, value);
        }
    }

    private void addCompare(DefaultConfigStore store, String fieldName, String operator, Object value) {
        if (value == null && !("IS_EMPTY".equalsIgnoreCase(operator) || "IS_NOT_EMPTY".equalsIgnoreCase(operator))) {
            return;
        }
        String op = operator.toUpperCase();
        switch (op) {
            case "EQUALS": store.and(Compare.EQUAL, fieldName, value); break;
            case "NOT_EQUALS": store.and(Compare.NOT_EQUAL, fieldName, value); break;
            case "GREATER_THAN": store.and(Compare.GREAT, fieldName, value); break;
            case "GREATER_EQUALS": store.and(Compare.GREAT_EQUAL, fieldName, value); break;
            case "LESS_THAN": store.and(Compare.LESS, fieldName, value); break;
            case "LESS_EQUALS": store.and(Compare.LESS_EQUAL, fieldName, value); break;
            case "CONTAINS": store.and(Compare.LIKE, fieldName, value); break;
            case "NOT_CONTAINS": store.and(Compare.NOT_LIKE, fieldName, value); break;
            case "EARLIER_THAN": store.and(Compare.LESS, fieldName, value); break;
            case "LATER_THAN": store.and(Compare.GREAT, fieldName, value); break;
            case "EXISTS_IN": store.and(Compare.IN, fieldName, value); break;
            case "NOT_EXISTS_IN": store.and(Compare.NOT_IN, fieldName, value); break;
            case "RANGE":
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked") Map<String,Object> range = (Map<String,Object>) value;
                    Object start = range.get("start");
                    Object end = range.get("end");
                    if (start != null) store.and(Compare.GREAT_EQUAL, fieldName, start);
                    if (end != null) store.and(Compare.LESS_EQUAL, fieldName, end);
                }
                break;
            case "IS_EMPTY": store.and(Compare.EQUAL, fieldName, ""); break;
            case "IS_NOT_EMPTY": store.and(Compare.NOT_EQUAL, fieldName, ""); break;
            default: store.and(Compare.LIKE, fieldName, value); break;
        }
    }

    // ========== 私有辅助方法 ==========

    /**
     * 校验实体存在
     */
    private MetadataBusinessEntityDO validateEntityExists(Long entityId) {
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        if (entity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
        return entity;
    }

    /**
     * 获取实体字段
     */
    private List<MetadataEntityFieldDO> getEntityFields(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.and("deleted", 0);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        if (fields == null || fields.isEmpty()) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
        return fields;
    }

    /**
     * 根据自定义 operator 映射到 Anyline Compare 条件
     * 支持：EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_EQUALS, LESS_THAN, LESS_EQUALS,
     *       CONTAINS, NOT_CONTAINS, EARLIER_THAN, LATER_THAN, RANGE(起始值), EXISTS_IN, NOT_EXISTS_IN
     * 其他或未识别操作符默认 LIKE
     */
    private void applyOperatorCondition(ConfigStore configs, String fieldName, String operator, Object value) {
        if (operator == null) {
            log.debug("[FILTER] field={} op=LIKE(default) value={}", fieldName, value);
            configs.and(Compare.LIKE, fieldName, value);
            return;
        }
        String op = operator.trim().toUpperCase();
        switch (op) {
            case "EQUALS":
                log.debug("[FILTER] field={} op=EQUAL value={}", fieldName, value);
                configs.and(Compare.EQUAL, fieldName, value); break;
            case "NOT_EQUALS":
                log.debug("[FILTER] field={} op=NOT_EQUAL value={}", fieldName, value);
                configs.and(Compare.NOT_EQUAL, fieldName, value); break;
            case "GREATER_THAN":
                log.debug("[FILTER] field={} op> value={}", fieldName, value);
                configs.and(Compare.GREAT, fieldName, value); break;
            case "GREATER_EQUALS":
                log.debug("[FILTER] field={} op>= value={}", fieldName, value);
                configs.and(Compare.GREAT_EQUAL, fieldName, value); break;
            case "LESS_THAN":
                log.debug("[FILTER] field={} op< value={}", fieldName, value);
                configs.and(Compare.LESS, fieldName, value); break;
            case "LESS_EQUALS":
                log.debug("[FILTER] field={} op<= value={}", fieldName, value);
                configs.and(Compare.LESS_EQUAL, fieldName, value); break;
            case "CONTAINS":
                log.debug("[FILTER] field={} op LIKE value=%{}%", fieldName, value);
                configs.and(Compare.LIKE, fieldName, value); break;
            case "NOT_CONTAINS":
                log.debug("[FILTER] field={} op NOT LIKE value=%{}%", fieldName, value);
                configs.and(Compare.NOT_LIKE, fieldName, value); break;
            case "EARLIER_THAN":
                log.debug("[FILTER] field={} op EARLIER(<) value={}", fieldName, value);
                configs.and(Compare.LESS, fieldName, value); break;
            case "LATER_THAN":
                log.debug("[FILTER] field={} op LATER(>) value={}", fieldName, value);
                configs.and(Compare.GREAT, fieldName, value); break;
            case "EXISTS_IN":
                log.debug("[FILTER] field={} op IN value={}", fieldName, value);
                configs.and(Compare.IN, fieldName, value); break;
            case "NOT_EXISTS_IN":
                log.debug("[FILTER] field={} op NOT IN value={}", fieldName, value);
                configs.and(Compare.NOT_IN, fieldName, value); break;
            case "RANGE":
                // 如果 value 是 Map {start,end} 结构，分别添加 >= 与 <=
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked") Map<String,Object> range = (Map<String,Object>) value;
                    Object start = range.get("start");
                    Object end = range.get("end");
                    if (start != null) { configs.and(Compare.GREAT_EQUAL, fieldName, start); }
                    if (end != null) { configs.and(Compare.LESS_EQUAL, fieldName, end); }
                    log.debug("[FILTER] field={} op RANGE start={} end={}", fieldName, start, end);
                } else {
                    configs.and(Compare.GREAT_EQUAL, fieldName, value);
                    log.debug("[FILTER] field={} op RANGE(start only) >= {}", fieldName, value);
                }
                break;
            case "IS_EMPTY":
                log.debug("[FILTER] field={} op =''", fieldName);
                configs.and(Compare.EQUAL, fieldName, ""); break;
            case "IS_NOT_EMPTY":
                log.debug("[FILTER] field={} op !=''", fieldName);
                configs.and(Compare.NOT_EQUAL, fieldName, ""); break;
            default:
                log.debug("[FILTER] field={} op LIKE(default) value={}", fieldName, value);
                configs.and(Compare.LIKE, fieldName, value); break;
        }
    }

    /**
     * 校验创建数据的完整性
     */
    private void validateDataForCreate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        for (MetadataEntityFieldDO field : fields) {
            // 跳过系统字段和主键字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsSystemField()) ||
                BooleanStatusEnum.isYes(field.getIsPrimaryKey())) {
                continue;
            }

            // 校验必填字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsRequired()) &&
                (data.get(field.getFieldName()) == null || String.valueOf(data.get(field.getFieldName())).trim().isEmpty())) {
                throw invalidParamException("字段[{}]为必填字段", field.getDisplayName());
            }
        }
    }

    /**
     * 校验更新数据
     */
    private void validateDataForUpdate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 更新时不校验必填，只校验数据类型等
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            if (field == null) {
                throw exception(ENTITY_FIELD_NOT_EXISTS, fieldName);
            }

            // 不允许更新主键字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsPrimaryKey())) {
                throw invalidParamException("不允许更新主键字段");
            }
        }
    }

    /**
     * 处理创建数据
     */
    private Map<String, Object> processDataForCreate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> processedData = new HashMap<>(data);

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 仅确定一个实际主键字段名，避免系统字段被误配置为主键导致被赋予雪花ID
        String realPrimaryKey = getPrimaryKeyFieldName(fields);

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            if (fieldName == null) {
                continue;
            }

            // 仅对真实主键字段生成雪花ID，避免误把deleted/lock_version等系统字段当作主键
            if (fieldName != null && fieldName.equalsIgnoreCase(realPrimaryKey)) {
                if (!processedData.containsKey(fieldName)) {
                    // 生成雪花ID作为主键
                    processedData.put(fieldName, uidGenerator.getUID());
                }
                continue;
            }

            // 处理系统字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsSystemField())) {
                switch (fieldName.toLowerCase()) {
                    case "created_time":
                    case "createtime":
                        processedData.put(fieldName, now);
                        break;
                    case "updated_time":
                    case "updatetime":
                        processedData.put(fieldName, now);
                        break;
                    case "deleted":
                        // deleted字段使用数字类型0，对应数据库中的int8类型
                        processedData.put(fieldName, 0);
                        break;
                    case "lock_version":
                    case "lockversion":
                        processedData.put(fieldName, 0);
                        break;
                    case "tenant_id":
                    case "tenantid":
                        // 这里可以从当前上下文获取租户ID，暂时设置为1
                        processedData.put(fieldName, 1L);
                        break;
                    case "owner_id":
                    case "ownerid":
                        // 设置为当前登录用户ID
                        Long currentUserId = WebFrameworkUtils.getLoginUserId();
                        if (currentUserId != null) {
                            processedData.put(fieldName, currentUserId);
                        } else {
                            log.warn("无法获取当前用户ID，owner_id字段将使用默认值");
                            if (StringUtils.hasText(field.getDefaultValue())) {
                                processedData.put(fieldName, field.getDefaultValue());
                            }
                        }
                        break;
                    case "owner_dept":
                    case "ownerdept":
                        // 设置为当前登录用户的部门ID
                        Long currentUserDeptId = SecurityFrameworkUtils.getLoginUserDeptId();
                        if (currentUserDeptId != null) {
                            processedData.put(fieldName, currentUserDeptId);
                        } else {
                            log.warn("无法获取当前用户部门ID，owner_dept字段将使用默认值1");
                            // 如果获取不到就先写死为1
                            processedData.put(fieldName, 1L);
                        }
                        break;
                    case "creator":
                        // 设置为当前登录用户ID
                        Long creatorUserId = WebFrameworkUtils.getLoginUserId();
                        if (creatorUserId != null) {
                            processedData.put(fieldName, creatorUserId);
                        } else {
                            log.warn("无法获取当前用户ID，creator字段将使用默认值1");
                            processedData.put(fieldName, 1L);
                        }
                        break;
                    case "updater":
                        // 设置为当前登录用户ID
                        Long updaterUserId = WebFrameworkUtils.getLoginUserId();
                        if (updaterUserId != null) {
                            processedData.put(fieldName, updaterUserId);
                        } else {
                            log.warn("无法获取当前用户ID，updater字段将使用默认值1");
                            processedData.put(fieldName, 1L);
                        }
                        break;
                    default:
                        // 其他系统字段按默认值处理
                        if (StringUtils.hasText(field.getDefaultValue())) {
                            processedData.put(fieldName, field.getDefaultValue());
                        }
                        break;
                }
                continue;
            }

            // 设置业务字段默认值
            if (!processedData.containsKey(fieldName) && StringUtils.hasText(field.getDefaultValue())) {
                processedData.put(fieldName, field.getDefaultValue());
            }
        }

        // 处理自动编号字段
        processAutoNumberFields(fields, processedData);

        return processedData;
    }

    /**
     * 处理更新数据
     */
    private Map<String, Object> processDataForUpdate(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> processedData = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            // 只处理非主键和非系统字段 - 使用新的枚举值：1-是，0-否
            if (field != null &&
                !BooleanStatusEnum.isYes(field.getIsPrimaryKey()) &&
                !BooleanStatusEnum.isYes(field.getIsSystemField())) {
                processedData.put(fieldName, entry.getValue());
            }
        }

        return processedData;
    }

    /**
     * 获取主键字段名
     */
    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
    // 1) 只考虑非系统字段中的主键，避免把 deleted/lock_version 等系统字段当作主键
    List<MetadataEntityFieldDO> pkCandidates = fields.stream()
        .filter(field -> BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
        .filter(field -> !BooleanStatusEnum.isYes(field.getIsSystemField()))
        .collect(Collectors.toList());

    // 2) 在候选中优先选择名字为 id 的字段
    Optional<String> idNamed = pkCandidates.stream()
        .map(MetadataEntityFieldDO::getFieldName)
        .filter(Objects::nonNull)
        .filter(name -> "id".equalsIgnoreCase(name))
        .findFirst();
    if (idNamed.isPresent()) {
        String pk = idNamed.get();
        log.debug("检测到非系统主键字段优先为: {}", pk);
        return pk;
    }

    // 3) 否则取第一个非系统主键候选
    Optional<String> firstPk = pkCandidates.stream()
        .map(MetadataEntityFieldDO::getFieldName)
        .filter(Objects::nonNull)
        .findFirst();
    if (firstPk.isPresent()) {
        String pk = firstPk.get();
        log.debug("检测到非系统主键字段: {}", pk);
        return pk;
    }

    // 4) 如果未配置主键，则回退到列名为 id（即使它被标记为系统字段，也作为兜底使用）
    boolean hasId = fields.stream()
        .map(MetadataEntityFieldDO::getFieldName)
        .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
    if (hasId) {
        log.debug("未配置主键，回退使用列名 id 作为主键");
        return "id";
    }

    // 5) 最终兜底
    log.warn("未找到主键字段且不存在列名为 id 的字段，使用默认 id 作为主键名称");
    return "id";
    }

    /**
     * 获取主键值
     */
    private Object getPrimaryKeyValue(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);
        Object value = data.get(primaryKeyField);

        log.debug("获取主键值 - 主键字段名: {}, 数据中的值: {}, 数据内容: {}",
                primaryKeyField, value, data);

        return value;
    }

    /**
     * 转换DataRow为Map
     */
    private Map<String, Object> convertDataRowToMap(DataRow dataRow, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> resultMap = new HashMap<>();

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            Object value = dataRow.get(fieldName);
            if (value != null) {
                resultMap.put(fieldName, value);
            }
        }

        return resultMap;
    }

    /**
     * 构建数据响应Map（包含字段类型信息）
     */
    private Map<String, Object> buildDataResponse(MetadataBusinessEntityDO entity, Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> response = new HashMap<>();
        response.put("entityId", String.valueOf(entity.getId()));
        response.put("entityName", entity.getDisplayName());
        response.put("data", data);

        // 构建字段类型映射
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            fieldTypeMap.put(field.getFieldName(), field.getFieldType());
        }
        response.put("fieldType", fieldTypeMap);

        return response;
    }

    /**
     * 使用指定的AnylineService根据ID查询数据
     */
    private Map<String, Object> queryDataByIdWithService(AnylineService<?> service, String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, id);

        // 检查表中是否有软删除字段
        boolean hasDeletedField = fields.stream()
                .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));

        if (hasDeletedField) {
            configStore.and("deleted", 0);
        }

        DataSet dataSet = service.querys(tableName, configStore);
        if (dataSet == null || dataSet.size() == 0) {
            return null;
        }

        DataRow dataRow = dataSet.getRow(0);
        return convertDataRowToMap(dataRow, fields);
    }

    /**
     * 使用指定的AnylineService校验数据是否存在
     */
    private void validateDataExistsWithService(AnylineService<?> service, String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> existingData = queryDataByIdWithService(service, tableName, id, fields);
        if (existingData == null || existingData.isEmpty()) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
    }

    /**
     * 为表名添加引号以支持PostgreSQL中大小写混合的表名
     *
     * @param tableName 原始表名
     * @return 添加引号后的表名
     */
    private void processAutoNumberFields(List<MetadataEntityFieldDO> fields, Map<String, Object> processedData) {
        for (MetadataEntityFieldDO field : fields) {
            try {
                // 检查字段是否配置了自动编号
                if (autoNumberService.hasAutoNumber(field.getId())) {
                    String fieldName = field.getFieldName();
                    
                    // 如果用户没有提供值，则生成自动编号
                    if (!processedData.containsKey(fieldName) || processedData.get(fieldName) == null) {
                        // 准备上下文数据，将当前的processedData作为上下文传递
                        Map<String, Object> contextData = new HashMap<>(processedData);
                        
                        // 为字段引用规则准备数据，使用字段ID作为key
                        for (MetadataEntityFieldDO f : fields) {
                            if (f.getFieldName() != null && processedData.containsKey(f.getFieldName())) {
                                contextData.put("field_" + f.getId(), processedData.get(f.getFieldName()));
                            }
                        }
                        
                        // 生成自动编号
                        String autoNumber = autoNumberService.generateNumber(field.getId(), contextData);
                        processedData.put(fieldName, autoNumber);
                        
                        log.info("为字段 {} 生成自动编号: {}", fieldName, autoNumber);
                    }
                }
            } catch (Exception e) {
                // 自动编号生成失败不应该阻塞整个数据创建过程，记录警告日志
                log.warn("为字段 {} 生成自动编号失败: {}", field.getFieldName(), e.getMessage(), e);
            }
        }
    }

    /**
     * 为表名添加双引号以处理PostgreSQL的大小写敏感性
     */
    private String quoteTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return tableName;
        }
        // 如果表名已经有引号，直接返回
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
            return tableName;
        }
        // 为表名添加双引号
        return "\"" + tableName + "\"";
    }
}
