package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.security.runtime.RTLoginUser;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.enums.ClientTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataSystemMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.engine.MultiTableQueryEngine;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl.MetadataDataMethodCreateImpl;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl.MetadataDataMethodDeleteImpl;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl.MetadataDataMethodQueryImpl;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl.MetadataDataMethodUpdateImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.METADATA_DATA_METHOD_RUNTIME_MENU_ID_REQUIRED;

/**
 * 数据方法 Service 核心实现类 - 只处理基础数据操作，不依赖VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Service
@Slf4j
public class MetadataDataMethodCoreServiceImpl extends AbstractMetadataDataMethodCoreService implements MetadataDataMethodCoreService {


    @Autowired
    private MetadataDataMethodCreateImpl metadataDataMethodCreate;

    @Autowired
    private MetadataDataMethodUpdateImpl metadataDataMethodUpdate;

    @Autowired
    private MetadataDataMethodDeleteImpl metadataDataMethodDelete;

    @Autowired
    private MetadataDataMethodQueryImpl metadataDataMethodQuery;

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

    @Value("${metadata.runtime.enable-auth-check:false}")
    private boolean enableAuthCheck;

    @Resource
    private com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService autoNumberService;
    // ========== 动态数据操作方法实现 ==========
    // ========== 动态数据操作方法实现 ==========

    @Override
    public Map<String, Object> createData(MetadataDataMethodRequestContext metadataDataMethodRequestContext) {

        // 获取当前登录用户的运行时权限
        this.fetchRuntimePermission(metadataDataMethodRequestContext);

        // 使用统一流程处理新增操作
        return metadataDataMethodCreate.executeProcess(metadataDataMethodRequestContext);
    }



    @Override
    public Map<String, Object> updateData(MetadataDataMethodRequestContext metadataDataMethodRequestContext) {

        // 获取当前登录用户的运行时权限
        this.fetchRuntimePermission(metadataDataMethodRequestContext);

        // 使用新的统一流程处理更新操作

        return metadataDataMethodUpdate.executeProcess(metadataDataMethodRequestContext);
    }

    @Override
    public Boolean deleteData(MetadataDataMethodRequestContext methodCoreContext) {

        // 获取当前登录用户的运行时权限
        this.fetchRuntimePermission(methodCoreContext);

        // 使用统一流程处理删除操作
        metadataDataMethodDelete.executeProcess(methodCoreContext);
        return true;
    }

    @Override
    public Map<String, Object> getData(Long entityId, Object id, String methodCode) {

        MetadataDataMethodRequestContext requestContext = new MetadataDataMethodRequestContext();
        requestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.GET);
        requestContext.setEntityId(entityId);
        requestContext.setId(id);

        Map<String, Object> result = metadataDataMethodQuery.doExecuteProcess(requestContext);
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> getDataPage(Long entityId, Integer pageNo, Integer pageSize,
                                                       String sortField, String sortDirection,
                                                       Map<String, Object> filters, String methodCode, Long menuId) {
        // 添加调试日志
        log.info("核心服务分页查询参数 - entityId: {}, pageNo: {}, pageSize: {}, pageSize类型: {}",
                entityId, pageNo, pageSize, pageSize != null ? pageSize.getClass().getSimpleName() : "null");
//合并代码
//        // 移除多表查询逻辑，直接使用单表分页
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        boolean hasDeletedField = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));

        // 获取权限上下文
        final MetadataPermissionContext permissionContext;
        final LoginUserCtx loginUserCtx;
        if (enableAuthCheck && menuId != null) {
            MetadataDataMethodRequestContext requestContext = new MetadataDataMethodRequestContext();
            requestContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.GET_PAGE);
            requestContext.setEntityId(entityId);
            requestContext.setClientTypeEnum(ClientTypeEnum.RUNTIME);
            requestContext.setMenuId(menuId);
            requestContext.setMethodCode(methodCode);
            this.fetchRuntimePermission(requestContext);
            permissionContext = requestContext.getPermissionContext();
            loginUserCtx = requestContext.getLoginUserCtx();
        } else {
            loginUserCtx = null;
            permissionContext = null;
        }

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
//                    // 兼容上层传入的结构：可能是直接 fieldName -> value，也可能是 conditionKey -> {fieldName, operator, value}
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
                        // 退化：直接LIKE（日期类型除外）
                        if (names.contains(rawKey)) {
                            String fieldType = fields.stream().filter(field ->
                                    rawKey.equals(field.getFieldName())).map(MetadataEntityFieldDO::getFieldType).findFirst().orElse("");
                            if("DATE".equals(fieldType)){
                                // 日期类型不使用LIKE，而是尝试解析为日期范围查询
                                // 这样Anyline可以根据不同数据库自动生成正确的SQL
                                try {
                                    java.time.LocalDate date = java.time.LocalDate.parse(String.valueOf(rawVal));
                                    java.time.LocalDateTime startOfDay = date.atStartOfDay();
                                    java.time.LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                                    configs.and(Compare.GREAT_EQUAL, rawKey, startOfDay);
                                    configs.and(Compare.LESS, rawKey, endOfDay);
                                } catch (Exception e) {
                                    // 如果无法解析为日期，直接使用原字段进行LIKE（让Anyline处理类型转换）
                                    log.debug("日期字段[{}]的值[{}]无法解析为日期，使用LIKE查询", rawKey, rawVal);
                                    configs.and(Compare.LIKE, rawKey, rawVal);
                                }
                            }else{
                                configs.and(Compare.LIKE, rawKey, rawVal);
                            }
                        }
                    }
                }
            }
            if (hasDeletedField && !deletedConditionAdded) {
                configs.and(Compare.EQUAL, "deleted", 0);
                deletedConditionAdded = true;
            }

            // 应用数据权限过滤
            if (permissionContext != null && loginUserCtx != null) {
                permissionQueryHelper.applyQueryPermissionFilter(configs, permissionContext, loginUserCtx, fields);
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
                            String fieldType = fields.stream().filter(field ->
                                    rawKey.equals(field.getFieldName())).map(MetadataEntityFieldDO::getFieldType).findFirst().orElse("");
                            if("DATE".equals(fieldType)){
                                // 日期类型不使用LIKE，而是尝试解析为日期范围查询
                                // 这样Anyline可以根据不同数据库自动生成正确的SQL
                                try {
                                    java.time.LocalDate date = java.time.LocalDate.parse(String.valueOf(rawVal));
                                    java.time.LocalDateTime startOfDay = date.atStartOfDay();
                                    java.time.LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                                    countConfigs.and(Compare.GREAT_EQUAL, rawKey, startOfDay);
                                    countConfigs.and(Compare.LESS, rawKey, endOfDay);
                                } catch (Exception e) {
                                    // 如果无法解析为日期，直接使用原字段进行LIKE（让Anyline处理类型转换）
                                    log.debug("日期字段[{}]的值[{}]无法解析为日期，使用LIKE查询", rawKey, rawVal);
                                    countConfigs.and(Compare.LIKE, rawKey, rawVal);
                                }
                            }else {
                                countConfigs.and(Compare.LIKE, rawKey, rawVal);
                            }
                        }
                    }
                }
            }
            if (hasDeletedField) {
                countConfigs.and(Compare.EQUAL, "deleted", 0);
            }

            // 应用数据权限过滤到 count 查询
            if (permissionContext != null && loginUserCtx != null) {
                permissionQueryHelper.applyQueryPermissionFilter(countConfigs, permissionContext, loginUserCtx, fields);
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

    private void fetchRuntimePermission(MetadataDataMethodRequestContext metadataDataMethodRequestContext) {

        metadataDataMethodRequestContext.setEnableAuthCheck(enableAuthCheck);

        if (!enableAuthCheck) {
            return;
        }
        // 仅 runtime 客户端需要校验权限
        if (metadataDataMethodRequestContext.getClientTypeEnum() != ClientTypeEnum.RUNTIME) {
            return;
        }

        Long menuId = metadataDataMethodRequestContext.getMenuId();
        if (menuId == null) {
            throw exception(METADATA_DATA_METHOD_RUNTIME_MENU_ID_REQUIRED);
        }

        RTLoginUser loginUser = RTSecurityContext.getLoginUser();
        if (loginUser == null) {
            throw exception(UNAUTHORIZED);
        }

        DataPermission menuDataPermission = RTSecurityContext.getMenuDataPermission(menuId);
        FieldPermission menuFieldPermission = RTSecurityContext.getMenuFieldPermission(menuId);
        OperationPermission menuOperation = RTSecurityContext.getMenuOperation(menuId);

        MetadataPermissionContext metadataPermissionContext = new MetadataPermissionContext();
        metadataPermissionContext.setDataPermission(menuDataPermission);
        metadataPermissionContext.setFieldPermission(menuFieldPermission);
        metadataPermissionContext.setOperationPermission(menuOperation);
        metadataDataMethodRequestContext.setPermissionContext(metadataPermissionContext);
        LoginUserCtx loginUserCtx = convertLoginUserCtx(loginUser);
        metadataDataMethodRequestContext.setLoginUserCtx(loginUserCtx);

    }

    private LoginUserCtx convertLoginUserCtx(RTLoginUser loginUser) {
        LoginUserCtx loginUserCtx = new LoginUserCtx();
        loginUserCtx.setUserId(loginUser.getId());
        loginUserCtx.setApplicationId(loginUser.getApplicationId());
        return loginUserCtx;
    }

}
