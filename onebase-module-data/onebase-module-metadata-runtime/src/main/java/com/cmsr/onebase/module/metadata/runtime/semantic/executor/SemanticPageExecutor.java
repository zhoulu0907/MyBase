package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.domain.query.LoginUserCtx;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.permission.PermissionQueryHelper;
import com.cmsr.onebase.module.metadata.core.service.permission.builder.PermissionContextBuilder;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticEntityValidator;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticFieldLoader;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticOperatorConditionApplier;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticResponseBuilder;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticRowMapper;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticTableNameQuoter;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.SemanticPageRecordAssembler;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticPageBodyVO;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
public class SemanticPageExecutor {
    @Resource
    private MetadataDatasourceCoreService semanticMetadataDatasourceCoreService;
    private MetadataDatasourceCoreService metadataDatasourceCoreService;
    private TemporaryDatasourceService semanticTemporaryDatasourceService;
    @Resource
    private PermissionQueryHelper semanticPermissionQueryHelper;
    @Resource
    private PermissionContextBuilder semanticPermissionContextBuilder;
    @Resource
    private SemanticOperatorConditionApplier semanticOperatorConditionApplier;
    @Resource
    private SemanticRowMapper semanticRowMapper;
    @Resource
    private SemanticResponseBuilder semanticResponseBuilder;
    @Resource
    private SemanticTableNameQuoter semanticTableNameQuoter;

    @Resource
    private SemanticEntityValidator semanticEntityValidator;
    @Resource
    private SemanticFieldLoader semanticFieldLoader;
    @Resource
    private SemanticPageRecordAssembler semanticPageRecordAssembler;
    @Resource
    private MetadataBusinessEntityCoreService semanticBusinessEntityCoreService;

    public PageResult<Map<String, Object>> execute(MetadataBusinessEntityDO entity,
                                                   List<MetadataEntityFieldDO> fields,
                                                   Long menuId,
                                                   SemanticRecordDTO record) {
        Integer pageNo = record.getContext() == null ? null : record.getContext().getPageNo();
        Integer pageSize = record.getContext() == null ? null : record.getContext().getPageSize();
        String sortField = null;
        String sortDirection = null;
        if (record.getContext() != null && record.getContext().getSortBy() != null && !record.getContext().getSortBy().isEmpty()) {
            var first = record.getContext().getSortBy().get(0);
            sortField = first.getField();
            sortDirection = first.getDirection() == null ? null : first.getDirection().name();
        }
        Map<String, Object> filters = record.getContext() == null ? null : record.getContext().getFilters();

        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) { throw exception(DATASOURCE_NOT_EXISTS); }
        AnylineService<?> temporaryService = semanticTemporaryDatasourceService.createTemporaryService(datasource);

        boolean hasDeletedField = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));
        final String sortFieldFinal = sortField;
        final String sortDirectionFinal = sortDirection;
        return TenantUtils.executeIgnore(() -> {
            DefaultConfigStore configs = new DefaultConfigStore();
            boolean deletedConditionAdded = false;
            if (filters != null && !filters.isEmpty()) {
                Set<String> names = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
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
                        if (value == null || !names.contains(fieldName)) { continue; }
                        semanticOperatorConditionApplier.apply(configs, fieldName, operator, value);
                    } else {
                        if (names.contains(rawKey)) {
                            String fieldType = fields.stream().filter(field -> rawKey.equals(field.getFieldName())).map(MetadataEntityFieldDO::getFieldType).findFirst().orElse("");
                            if ("DATE".equals(fieldType)) {
                                try {
                                    java.time.LocalDate date = java.time.LocalDate.parse(String.valueOf(rawVal));
                                    java.time.LocalDateTime startOfDay = date.atStartOfDay();
                                    java.time.LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                                    configs.and(Compare.GREAT_EQUAL, rawKey, startOfDay);
                                    configs.and(Compare.LESS, rawKey, endOfDay);
                                } catch (Exception e) {
                                    configs.and(Compare.LIKE, rawKey, rawVal);
                                }
                            } else {
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
            MetadataPermissionContext permissionContext = null;
            LoginUserCtx loginUserCtx = null;
            if (menuId != null) {
                RuntimeLoginUser loginUser = RTSecurityContext.getLoginUser();
                if (loginUser != null) {
                    loginUserCtx = new LoginUserCtx();
                    loginUserCtx.setUserId(loginUser.getId());
                    loginUserCtx.setApplicationId(loginUser.getApplicationId());
                }
                permissionContext = semanticPermissionContextBuilder.buildPermissionContext(loginUserCtx, menuId, entity.getId());
            }
            if (permissionContext != null) {
                semanticPermissionQueryHelper.applyQueryPermissionFilter(configs, permissionContext, loginUserCtx, fields);
            }
            Set<String> fieldNames = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
            if (fieldNames.contains(sortFieldFinal)) {
                String orderClause = sortFieldFinal + ("desc".equalsIgnoreCase(sortDirectionFinal) ? " DESC" : " ASC");
                configs.order(orderClause);
            } else {
                String primaryKeyField = getPrimaryKeyFieldName(fields);
                configs.order(primaryKeyField + " DESC");
            }
            if (pageNo != null && pageSize != null) {
                PageNavi page = new DefaultPageNavi(pageNo, pageSize);
                configs.setPageNavi(page);
            }
            DefaultConfigStore countConfigs = new DefaultConfigStore();
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
                        semanticOperatorConditionApplier.apply(countConfigs, fieldName, operator, value);
                    } else {
                        if (existingFieldNames.contains(rawKey)) {
                            String fieldType = fields.stream().filter(field -> rawKey.equals(field.getFieldName())).map(MetadataEntityFieldDO::getFieldType).findFirst().orElse("");
                            if ("DATE".equals(fieldType)) {
                                try {
                                    java.time.LocalDate date = java.time.LocalDate.parse(String.valueOf(rawVal));
                                    java.time.LocalDateTime startOfDay = date.atStartOfDay();
                                    java.time.LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                                    countConfigs.and(Compare.GREAT_EQUAL, rawKey, startOfDay);
                                    countConfigs.and(Compare.LESS, rawKey, endOfDay);
                                } catch (Exception e) {
                                    countConfigs.and(Compare.LIKE, rawKey, rawVal);
                                }
                            } else {
                                countConfigs.and(Compare.LIKE, rawKey, rawVal);
                            }
                        }
                    }
                }
            }
            if (hasDeletedField) { countConfigs.and(Compare.EQUAL, "deleted", 0); }
            long total = temporaryService.count(semanticTableNameQuoter.quote(entity.getTableName()), countConfigs);
            DataSet dataSet = temporaryService.querys(semanticTableNameQuoter.quote(entity.getTableName()), configs);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = semanticRowMapper.toMap(row, fields);
                Map<String, Object> filtered = semanticPermissionQueryHelper.filterQueryResult(data, permissionContext, fields);
                list.add(semanticResponseBuilder.build(entity, filtered, fields));
            }
            return new PageResult<>(list, total);
        });
    }

    public PageResult<Map<String, Object>> execute(Long entityId,
                                                   Long menuId,
                                                   String traceId,
                                                   SemanticRecordDTO record) {
        MetadataBusinessEntityDO entity = semanticEntityValidator.validateExists(entityId);
        List<MetadataEntityFieldDO> fields = semanticFieldLoader.load(entityId);
        return execute(entity, fields, menuId, record);
    }

    public PageResult<Map<String, Object>> execute(Long entityId,
                                                   Long menuId,
                                                   String traceId,
                                                   SemanticPageBodyVO body) {
        SemanticRecordDTO record = semanticPageRecordAssembler.assemble(semanticBusinessEntityCoreService.getBusinessEntity(entityId).getCode(), body, menuId, traceId);
        return execute(entityId, menuId, traceId, record);
    }

    public PageResult<Map<String, Object>> execute(String entityCode,
                                                   Long menuId,
                                                   String traceId,
                                                   SemanticPageBodyVO body) {
        SemanticRecordDTO record = semanticPageRecordAssembler.assemble(entityCode, body, menuId, traceId);
        MetadataBusinessEntityDO entity = semanticBusinessEntityCoreService.getBusinessEntityByCode(entityCode);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        List<MetadataEntityFieldDO> fields = semanticFieldLoader.load(entity.getId());
        return execute(entity, fields, menuId, record);
    }

    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        List<MetadataEntityFieldDO> pkCandidates = fields.stream()
                .filter(field -> com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
                .filter(field -> !com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsSystemField()))
                .toList();
        Optional<String> idNamed = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        Optional<String> firstPk = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(MetadataEntityFieldDO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }
}
