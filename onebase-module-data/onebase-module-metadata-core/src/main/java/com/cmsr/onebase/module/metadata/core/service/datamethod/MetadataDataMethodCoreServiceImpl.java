package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
// MetadataDataSystemMethodDO 已由查询功能迁移至 build 模块，核心仅保留运行时 CRUD
import com.cmsr.onebase.module.metadata.core.service.datamethod.datamethodImpl.MetadataDataMethodCreateImpl;
import com.cmsr.onebase.module.metadata.core.service.datamethod.datamethodImpl.MetadataDataMethodUpdateImpl;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.engine.MultiTableQueryEngine;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

/**
 * 数据方法 Service 核心实现类 - 只处理基础数据操作，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
@Slf4j
public class MetadataDataMethodCoreServiceImpl extends AbstractMetadataDataMethodCoreService  implements MetadataDataMethodCoreService {


    @Autowired
    private MetadataDataMethodCreateImpl metadataDataMethodCreate;
    
    @Autowired
    private MetadataDataMethodUpdateImpl metadataDataMethodUpdate;
    
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

        Map<String, Object> result = metadataDataMethodCreate.executeProcess(OperationType.CREATE, entityId, data, methodCode);


//        // 1. 校验实体存在
//        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
//
//        // 2. 获取实体字段信息
//        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
//
//        // 3. 校验数据完整性
//        validateDataIntegrity(data, fields);
//
//        // 4. 处理数据并设置默认值
//        Map<String, Object> processedData = processDataAndSetDefaults(data, fields);
//
//        // 5. 获取临时数据源服务
//        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
//        if (datasource == null) {
//            throw exception(DATASOURCE_NOT_EXISTS);
//        }
//
//        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
//        log.info("成功切换到数据源：{}", datasource.getCode());
//
//        // 6. 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
//        return TenantUtils.executeIgnore(() -> {
//
//        // 7. 执行插入
//        if (log.isDebugEnabled()) {
//            log.debug("createData -> processedData before insert: {}", processedData);
//        }
//        DataRow dataRow = new DataRow(processedData);
//        Object insertResult = temporaryService.insert(quoteTableName(entity.getTableName()), dataRow);
//        log.info("创建数据成功，实体ID: {}, 表名: {}, 插入结果: {}", entityId, entity.getTableName(), insertResult);
//
//        // 8. 查询插入后的完整数据
//        Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
//        log.info("从处理数据中获取主键值: {}, 插入结果: {}", primaryKeyValue, insertResult);
//
//        // 确保主键值不为null
//        if (primaryKeyValue == null) {
//            log.warn("无法获取主键值，跳过查询插入后的数据，实体ID: {}, 表名: {}", entityId, entity.getTableName());
//            // 返回插入的数据
//            return buildDataResponse(entity, processedData, fields);
//        }
//
//        Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), primaryKeyValue, fields);
//
//        // 9. 构建响应（移除多表写入逻辑，直接返回结果）
//        return buildDataResponse(entity, resultData, fields);
//
//        }); // TenantUtils.executeIgnore 闭合

        return result;
    }

    @Override
    public Map<String, Object> updateData(Long entityId, Object id, Map<String, Object> data, String methodCode) {
        // 使用新的统一流程处理更新操作
        return metadataDataMethodUpdate.executeProcess(OperationType.UPDATE, entityId, id, data, methodCode);
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

    // 公共方法已移动到 AbstractMetadataDataMethodCoreService

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

    // 校验方法已移动到 AbstractMetadataDataMethodCoreService

    // 数据处理方法已移动到 AbstractMetadataDataMethodCoreService

    // 处理更新数据方法已移动到 AbstractMetadataDataMethodCoreService

    // 主键相关方法已移动到 AbstractMetadataDataMethodCoreService

    // 查询和校验方法已移动到 AbstractMetadataDataMethodCoreService

    // 自动编号和表名处理方法已移动到 AbstractMetadataDataMethodCoreService
}
