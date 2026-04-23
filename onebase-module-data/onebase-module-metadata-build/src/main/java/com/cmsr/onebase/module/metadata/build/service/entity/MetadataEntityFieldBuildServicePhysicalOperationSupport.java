package com.cmsr.onebase.module.metadata.build.service.entity;


import com.cmsr.onebase.framework.aynline.AnylineDdlHelper;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.service.component.MetadataComponentFieldTypeBuildService;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.build.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldConstraintBuildService;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldOptionBuildService;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberConfigBuildService;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberRuleBuildService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.*;
import com.cmsr.onebase.module.metadata.core.dal.database.*;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberResetLogDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberStateDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import com.cmsr.onebase.module.metadata.core.enums.*;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberRuleEngine;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.metadata.Column;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.service.AnylineService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;
/**
 * Split segment of metadata build service implementation.
 */
@Slf4j
public abstract class MetadataEntityFieldBuildServicePhysicalOperationSupport extends MetadataEntityFieldBuildServiceRelatedSupport {

    protected abstract void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName,
            Set<String> existingColumns);

    protected abstract void renameColumnInTable(MetadataDatasourceDO datasource, String tableName, String oldName,
            String newName, Set<String> existingColumns);

    protected abstract void addColumnToTable(MetadataDatasourceDO datasource, String tableName,
            MetadataEntityFieldDO field, Set<String> existingColumns);

    protected abstract void alterColumnInTable(MetadataDatasourceDO datasource, String tableName,
            MetadataEntityFieldDO field, Set<String> existingColumns);

    protected abstract boolean useCustomPgCompatibleDdl(DatabaseType dbType);

    protected abstract void validateFieldNameNotDatabaseKeyword(String fieldName);

    /**
     * 物理表操作封装类
     */
    @lombok.Data
    protected static class PhysicalTableOperation {
        /**
         * 操作类型：ADD、ALTER、DROP、RENAME
         */
        protected MetadataPhysicalTableOperationTypeEnum operationType;

        /**
         * 字段名（用于ADD、ALTER、DROP操作）
         */
        protected String fieldName;

        /**
         * 旧字段名（用于RENAME操作）
         */
        protected String oldFieldName;

        /**
         * 字段信息（用于ADD、ALTER操作）
         */
        protected MetadataEntityFieldDO fieldInfo;
    }

    /**
     * 统一执行物理表操作
     * <p>
     * 优化策略：
     * - DROP/RENAME/ADD 操作仍然逐个执行
     * - ALTER 操作合并为一条 DDL 语句执行（仅针对 PostgreSQL/KingBase）
     *
     * @param datasource 数据源信息
     * @param tableName  表名
     * @param operations 待执行的操作列表
     */
    protected void executePhysicalTableOperations(MetadataDatasourceDO datasource, String tableName,
            List<PhysicalTableOperation> operations) {
        if (operations == null || operations.isEmpty()) {
            return;
        }

        long totalStartNs = System.nanoTime();
        log.info("开始批量执行物理表操作，表名: {}, 操作数量: {}", tableName, operations.size());

        java.util.Set<String> existingColumns = fetchExistingColumns(datasource, tableName);

        // 分类收集操作
        List<PhysicalTableOperation> dropOps = new java.util.ArrayList<>();
        List<PhysicalTableOperation> renameOps = new java.util.ArrayList<>();
        List<PhysicalTableOperation> alterOps = new java.util.ArrayList<>();
        List<PhysicalTableOperation> addOps = new java.util.ArrayList<>();

        for (PhysicalTableOperation op : operations) {
            switch (op.getOperationType()) {
                case DROP:
                    dropOps.add(op);
                    break;
                case RENAME:
                    renameOps.add(op);
                    break;
                case ALTER:
                    alterOps.add(op);
                    break;
                case ADD:
                    addOps.add(op);
                    break;
                default:
                    log.warn("未知的物理表操作类型: {}", op.getOperationType());
            }
        }

        long dropStartNs = System.nanoTime();
        // 1. 先执行 DROP 操作
        for (PhysicalTableOperation op : dropOps) {
            try {
                dropColumnFromTable(datasource, tableName, op.getFieldName(), existingColumns);
                if (existingColumns != null) {
                    existingColumns.remove(normalizeColumnName(op.getFieldName()));
                }
            } catch (Exception e) {
                log.error("执行 DROP 操作失败，字段名: {}, 错误: {}", op.getFieldName(), e.getMessage(), e);
                throw new RuntimeException("物理表操作失败: " + e.getMessage(), e);
            }
        }
        long dropMs = (System.nanoTime() - dropStartNs) / 1_000_000;

        long renameStartNs = System.nanoTime();
        // 2. 执行 RENAME 操作
        for (PhysicalTableOperation op : renameOps) {
            try {
                renameColumnInTable(datasource, tableName, op.getOldFieldName(), op.getFieldName(), existingColumns);
                if (existingColumns != null) {
                    existingColumns.remove(normalizeColumnName(op.getOldFieldName()));
                    existingColumns.add(normalizeColumnName(op.getFieldName()));
                }
            } catch (Exception e) {
                log.error("执行 RENAME 操作失败，旧字段名: {}, 新字段名: {}, 错误: {}",
                        op.getOldFieldName(), op.getFieldName(), e.getMessage(), e);
                throw new RuntimeException("物理表操作失败: " + e.getMessage(), e);
            }
        }
        long renameMs = (System.nanoTime() - renameStartNs) / 1_000_000;

        long alterStartNs = System.nanoTime();
        // 3. 合并执行 ALTER 操作（优化性能）
        if (!alterOps.isEmpty()) {
            try {
                executeBatchAlterOperations(datasource, tableName, alterOps, existingColumns);
            } catch (Exception e) {
                log.error("执行批量 ALTER 操作失败，错误: {}", e.getMessage(), e);
                throw new RuntimeException("物理表操作失败: " + e.getMessage(), e);
            }
        }
        long alterMs = (System.nanoTime() - alterStartNs) / 1_000_000;

        long addStartNs = System.nanoTime();
        // 4. 最后执行 ADD 操作
        for (PhysicalTableOperation op : addOps) {
            try {
                addColumnToTable(datasource, tableName, op.getFieldInfo(), existingColumns);
            } catch (Exception e) {
                log.error("执行 ADD 操作失败，字段名: {}, 错误: {}",
                        op.getFieldInfo().getFieldName(), e.getMessage(), e);
                throw new RuntimeException("物理表操作失败: " + e.getMessage(), e);
            }
        }
        long addMs = (System.nanoTime() - addStartNs) / 1_000_000;

        long totalMs = (System.nanoTime() - totalStartNs) / 1_000_000;
        log.info("批量执行物理表操作完成，表名: {}, totalMs={}, dropMs={}, renameMs={}, alterMs={}, addMs={}, dropOps={}, renameOps={}, alterOps={}, addOps={}",
                tableName, totalMs, dropMs, renameMs, alterMs, addMs,
                dropOps.size(), renameOps.size(), alterOps.size(), addOps.size());
    }

    /**
     * 批量执行 ALTER 操作
     * <p>
     * 将多个 ALTER 操作合并为一条 DDL 语句执行，减少数据库交互次数。
     * 对于达梦数据库，仍然使用 Anyline 原生 API 逐个执行。
     *
     * @param datasource 数据源信息
     * @param tableName  表名
     * @param alterOps   ALTER 操作列表
     */
    protected void executeBatchAlterOperations(MetadataDatasourceDO datasource, String tableName,
            List<PhysicalTableOperation> alterOps) {
        executeBatchAlterOperations(datasource, tableName, alterOps, null);
    }

    protected void executeBatchAlterOperations(MetadataDatasourceDO datasource, String tableName,
            List<PhysicalTableOperation> alterOps, java.util.Set<String> existingColumns) {
        if (alterOps == null || alterOps.isEmpty()) {
            return;
        }

        String datasourceType = datasource.getDatasourceType();
        DatabaseType dbType;
        try {
            dbType = DatabaseType.valueOf(datasourceType);
        } catch (IllegalArgumentException e) {
            log.warn("未知的数据库类型: {}，使用逐个执行方式", datasourceType);
            // 未知数据库类型，逐个执行
            for (PhysicalTableOperation op : alterOps) {
                alterColumnInTable(datasource, tableName, op.getFieldInfo(), existingColumns);
            }
            return;
        }

        if (dbType == DatabaseType.DM) {
            // 达梦数据库：使用 Anyline API 逐个执行（因为 MODIFY 语法不支持合并）
            log.info("达梦数据库逐个执行 ALTER 操作，操作数量: {}", alterOps.size());
            for (PhysicalTableOperation op : alterOps) {
                alterColumnInTable(datasource, tableName, op.getFieldInfo(), existingColumns);
            }
        } else {
            // PostgreSQL/KingBase：合并为一条 DDL 语句执行
            log.info("合并执行 ALTER 操作，操作数量: {}", alterOps.size());
            executeMergedAlterDDL(datasource, tableName, alterOps, datasourceType, existingColumns);
        }
    }

    /**
     * 执行合并的 ALTER DDL 语句（适用于 PostgreSQL/KingBase）
     * <p>
     * PostgreSQL 支持在一条 ALTER TABLE 语句中包含多个 ALTER COLUMN 子句。
     * 例如：
     * <pre>
     * ALTER TABLE "table_name"
     *     ALTER COLUMN "col1" TYPE VARCHAR(100),
     *     ALTER COLUMN "col2" SET NOT NULL;
     * </pre>
     *
     * @param datasource     数据源信息
     * @param tableName      表名
     * @param alterOps       ALTER 操作列表
     * @param datasourceType 数据库类型
     */
    protected void executeMergedAlterDDL(MetadataDatasourceDO datasource, String tableName,
            List<PhysicalTableOperation> alterOps, String datasourceType) {
        executeMergedAlterDDL(datasource, tableName, alterOps, datasourceType, null);
    }

    protected void executeMergedAlterDDL(MetadataDatasourceDO datasource, String tableName,
            List<PhysicalTableOperation> alterOps, String datasourceType, java.util.Set<String> existingColumns) {
        try {
            TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 先校验表是否存在
                if (existingColumns == null) {
                    if (!AnylineDdlHelper.tableExists(service, tableName)) {
                        throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                    }
                }

                java.util.Set<String> columns = existingColumns != null ? existingColumns : new java.util.HashSet<>();
                if (existingColumns == null) {
                    AnylineDdlHelper.clearMetadataCache();
                    org.anyline.metadata.Table<?> table = service.metadata().table(tableName);
                    if (table != null && table.getColumns() != null) {
                        for (org.anyline.metadata.Column col : table.getColumns().values()) {
                            if (col != null && col.getName() != null) {
                                columns.add(normalizeColumnName(col.getName()));
                            }
                        }
                    }
                }

                // 生成合并的 DDL 语句
                StringBuilder mergedDdl = new StringBuilder();
                StringBuilder commentDdl = new StringBuilder();

                for (int i = 0; i < alterOps.size(); i++) {
                    PhysicalTableOperation op = alterOps.get(i);
                    MetadataEntityFieldDO field = op.getFieldInfo();
                    String fieldName = field.getFieldName();

                    // 检查列是否存在
                    if (!columns.contains(normalizeColumnName(fieldName))) {
                        log.warn("列 {} 不存在于表 {} 中，跳过 ALTER 操作", fieldName, tableName);
                        continue;
                    }

                    String columnType = mapFieldType(field.getFieldType(), field.getDataLength());

                    // 生成该字段的 ALTER COLUMN 子句
                    // 1. 修改字段类型
                    if (mergedDdl.length() > 0) {
                        mergedDdl.append(",\n    ");
                    } else {
                        mergedDdl.append("ALTER TABLE \"").append(tableName).append("\"\n    ");
                    }
                    mergedDdl.append("ALTER COLUMN \"").append(fieldName).append("\" TYPE ").append(columnType);

                    // 2. 修改是否允许为空（需要单独的 ALTER COLUMN 语句）
                    if (field.getIsRequired() != null) {
                        mergedDdl.append(",\n    ALTER COLUMN \"").append(fieldName).append("\"");
                        if (BooleanStatusEnum.isYes(field.getIsRequired())) {
                            mergedDdl.append(" SET NOT NULL");
                        } else {
                            mergedDdl.append(" DROP NOT NULL");
                        }
                    }

                    // 3. 修改默认值（需要单独的 ALTER COLUMN 语句）
                    if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                        String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
                        if (formattedValue != null) {
                            mergedDdl.append(",\n    ALTER COLUMN \"").append(fieldName)
                                    .append("\" SET DEFAULT ").append(formattedValue);
                        }
                    }

                    // 4. 字段注释（需要单独执行）
                    if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
                        commentDdl.append("COMMENT ON COLUMN \"").append(tableName).append("\".\"");
                        commentDdl.append(fieldName).append("\" IS '").append(field.getDescription()).append("';\n");
                    }
                }

                // 执行合并的 ALTER TABLE 语句
                if (mergedDdl.length() > 0) {
                    mergedDdl.append(";");
                    String alterSql = mergedDdl.toString();
                    log.info("执行合并的 ALTER DDL: {}", alterSql);
                    AnylineDdlHelper.executeDDL(service, alterSql);
                }

                // 执行注释语句
                if (commentDdl.length() > 0) {
                    log.debug("执行字段注释 DDL: {}", commentDdl);
                    AnylineDdlHelper.executeDDL(service, commentDdl.toString());
                }

                return null;
            });
        } catch (Exception e) {
            log.error("执行合并 ALTER DDL 失败: {}", e.getMessage(), e);
            throw new RuntimeException("修改列失败", e);
        }
    }

    protected java.util.Set<String> fetchExistingColumns(MetadataDatasourceDO datasource, String tableName) {
        java.util.Set<String> fastPathColumns = fetchExistingColumnsByInformationSchema(datasource, tableName);
        if (fastPathColumns != null) {
            return fastPathColumns;
        }

        try {
            return TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);
                AnylineDdlHelper.clearMetadataCache();
                org.anyline.metadata.Table<?> table = service.metadata().table(tableName);
                if (table == null) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }
                java.util.Set<String> result = new java.util.HashSet<>();
                if (table != null && table.getColumns() != null) {
                    for (org.anyline.metadata.Column col : table.getColumns().values()) {
                        if (col != null && col.getName() != null) {
                            result.add(normalizeColumnName(col.getName()));
                        }
                    }
                }
                return result;
            });
        } catch (Exception e) {
            log.warn("获取表 {} 列元数据失败，将退化为按列查询: {}", tableName, e.getMessage());
            return null;
        }
    }

    /**
     * 使用 information_schema 精确查询列信息，避免 Anyline metadata 在大表数量场景下的全量元数据开销。
     * 仅在 PostgreSQL/OpenGauss/KingBase 数据源启用。
     *
     * @return 成功时返回列名集合；不适用或失败时返回 null 以便走旧逻辑兜底
     */
    protected java.util.Set<String> fetchExistingColumnsByInformationSchema(MetadataDatasourceDO datasource, String tableName) {
        if (datasource == null || !StringUtils.hasText(tableName) || !isPgCompatibleDatasource(datasource.getDatasourceType())) {
            return null;
        }
        String lookupTableName = sanitizeSqlLiteral(unquoteIdentifier(tableName));
        if (!StringUtils.hasText(lookupTableName)) {
            return null;
        }
        long startNs = System.nanoTime();
        try {
            return TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);
                String tableExistsSql = "SELECT table_name FROM information_schema.tables "
                        + "WHERE table_schema = current_schema() AND table_name = '" + lookupTableName + "' LIMIT 1";
                DataSet tableRows = service.querys(tableExistsSql);
                if (tableRows == null || tableRows.size() == 0) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                String columnsSql = "SELECT column_name FROM information_schema.columns "
                        + "WHERE table_schema = current_schema() AND table_name = '" + lookupTableName + "'";
                DataSet columnRows = service.querys(columnsSql);
                java.util.Set<String> result = new java.util.HashSet<>();
                if (columnRows != null) {
                    for (int i = 0; i < columnRows.size(); i++) {
                        DataRow row = columnRows.getRow(i);
                        if (row == null) {
                            continue;
                        }
                        String columnName = row.getString("column_name");
                        if (StringUtils.hasText(columnName)) {
                            result.add(normalizeColumnName(columnName));
                        }
                    }
                }
                return result;
            });
        } catch (Exception ex) {
            log.debug("information_schema 列元数据查询失败，回退 Anyline metadata。tableName={}, error={}",
                    tableName, ex.getMessage());
            return null;
        } finally {
            long costMs = (System.nanoTime() - startNs) / 1_000_000;
            log.info("fetchExistingColumnsByInformationSchema done tableName={}, costMs={}", tableName, costMs);
        }
    }

    protected boolean isPgCompatibleDatasource(String datasourceType) {
        if (!StringUtils.hasText(datasourceType)) {
            return false;
        }
        try {
            return useCustomPgCompatibleDdl(DatabaseType.valueOf(datasourceType));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    protected String unquoteIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        return identifier.replace("\"", "").trim();
    }

    protected String sanitizeSqlLiteral(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("'", "''");
    }

    protected void validateBatchFieldUniqueness(String entityUuid, String applicationId, List<EntityFieldUpsertItemVO> items,
            List<MetadataEntityFieldDO> existingFields, Map<Long, MetadataEntityFieldDO> fieldsById) {
        if (items == null || items.isEmpty()) {
            return;
        }
        Set<String> deletedFieldUuids = collectDeletedFieldUuids(items, fieldsById);
        Set<String> updatedFieldUuids = collectUpdatedFieldUuids(items, fieldsById);
        Map<String, String> fieldNameOwner = new HashMap<>();
        Map<String, String> displayNameOwner = new HashMap<>();
        seedExistingFieldOwners(applicationId, existingFields, deletedFieldUuids, updatedFieldUuids,
                fieldNameOwner, displayNameOwner);
        validateIncomingFieldOwners(items, fieldsById, fieldNameOwner, displayNameOwner);
    }

    protected Set<String> collectDeletedFieldUuids(List<EntityFieldUpsertItemVO> items,
            Map<Long, MetadataEntityFieldDO> fieldsById) {
        Set<String> deletedFieldUuids = new HashSet<>();
        for (EntityFieldUpsertItemVO item : items) {
            if (Boolean.TRUE.equals(item.getIsDeleted())) {
                addFieldUuidByItemId(deletedFieldUuids, item, fieldsById);
            }
        }
        return deletedFieldUuids;
    }

    protected Set<String> collectUpdatedFieldUuids(List<EntityFieldUpsertItemVO> items,
            Map<Long, MetadataEntityFieldDO> fieldsById) {
        Set<String> updatedFieldUuids = new HashSet<>();
        for (EntityFieldUpsertItemVO item : items) {
            if (!Boolean.TRUE.equals(item.getIsDeleted())) {
                addFieldUuidByItemId(updatedFieldUuids, item, fieldsById);
            }
        }
        return updatedFieldUuids;
    }

    protected void addFieldUuidByItemId(Set<String> fieldUuids, EntityFieldUpsertItemVO item,
            Map<Long, MetadataEntityFieldDO> fieldsById) {
        Long id = parseIdOrNull(item.getId());
        MetadataEntityFieldDO field = id != null ? fieldsById.get(id) : null;
        if (field != null && field.getFieldUuid() != null) {
            fieldUuids.add(field.getFieldUuid());
        }
    }

    protected void seedExistingFieldOwners(String applicationId, List<MetadataEntityFieldDO> existingFields,
            Set<String> deletedFieldUuids, Set<String> updatedFieldUuids,
            Map<String, String> fieldNameOwner, Map<String, String> displayNameOwner) {
        String appIdStr = applicationId != null ? applicationId.trim() : null;
        if (existingFields == null) {
            return;
        }
        for (MetadataEntityFieldDO field : existingFields) {
            if (shouldSkipExistingFieldOwner(field, appIdStr, deletedFieldUuids, updatedFieldUuids)) {
                continue;
            }
            putOwnerIfPresent(fieldNameOwner, trimToNull(field.getFieldName()), field.getFieldUuid());
            putOwnerIfPresent(displayNameOwner, trimToNull(field.getDisplayName()), field.getFieldUuid());
        }
    }

    protected boolean shouldSkipExistingFieldOwner(MetadataEntityFieldDO field, String appIdStr,
            Set<String> deletedFieldUuids, Set<String> updatedFieldUuids) {
        if (field == null || field.getFieldUuid() == null) {
            return true;
        }
        if (deletedFieldUuids.contains(field.getFieldUuid()) || updatedFieldUuids.contains(field.getFieldUuid())) {
            return true;
        }
        return appIdStr != null && field.getApplicationId() != null
                && !appIdStr.equals(String.valueOf(field.getApplicationId()));
    }

    protected void validateIncomingFieldOwners(List<EntityFieldUpsertItemVO> items,
            Map<Long, MetadataEntityFieldDO> fieldsById,
            Map<String, String> fieldNameOwner,
            Map<String, String> displayNameOwner) {
        for (EntityFieldUpsertItemVO item : items) {
            if (Boolean.TRUE.equals(item.getIsDeleted())) {
                continue;
            }
            FieldOwnerCandidate candidate = buildFieldOwnerCandidate(item, fieldsById);
            assertUniqueOwner(fieldNameOwner, candidate.fieldName, candidate.fieldUuid, item, true);
            assertUniqueOwner(displayNameOwner, candidate.displayName, candidate.fieldUuid, item, false);
        }
    }

    protected FieldOwnerCandidate buildFieldOwnerCandidate(EntityFieldUpsertItemVO item,
            Map<Long, MetadataEntityFieldDO> fieldsById) {
        FieldOwnerCandidate candidate = new FieldOwnerCandidate();
        MetadataEntityFieldDO origin = resolveOriginField(item, fieldsById);
        candidate.fieldUuid = origin != null ? origin.getFieldUuid() : null;
        candidate.fieldName = resolveFinalFieldName(item, origin);
        candidate.displayName = resolveFinalDisplayName(item, origin);
        return candidate;
    }

    protected MetadataEntityFieldDO resolveOriginField(EntityFieldUpsertItemVO item,
            Map<Long, MetadataEntityFieldDO> fieldsById) {
        Long id = parseIdOrNull(item.getId());
        if (id == null) {
            return null;
        }
        MetadataEntityFieldDO origin = fieldsById.get(id);
        if (origin == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
        return origin;
    }

    protected String resolveFinalFieldName(EntityFieldUpsertItemVO item, MetadataEntityFieldDO origin) {
        String fieldName = trimToNull(item.getFieldName());
        if (fieldName != null) {
            validateFieldNameNotDatabaseKeyword(fieldName);
            return fieldName;
        }
        return origin != null ? trimToNull(origin.getFieldName()) : null;
    }

    protected String resolveFinalDisplayName(EntityFieldUpsertItemVO item, MetadataEntityFieldDO origin) {
        String displayName = trimToNull(item.getDisplayName());
        return displayName != null || origin == null ? displayName : trimToNull(origin.getDisplayName());
    }

    protected void assertUniqueOwner(Map<String, String> owners, String value, String fieldUuid,
            EntityFieldUpsertItemVO item, boolean fieldName) {
        if (value == null) {
            return;
        }
        String owner = owners.get(value);
        if (owner != null && (fieldUuid == null || !owner.equals(fieldUuid))) {
            throw exception(fieldName ? ENTITY_FIELD_NAME_DUPLICATE : ENTITY_FIELD_DISPLAY_NAME_DUPLICATE, value);
        }
        owners.put(value, fieldUuid != null ? fieldUuid : "NEW#" + System.identityHashCode(item));
    }

    protected void putOwnerIfPresent(Map<String, String> owners, String value, String fieldUuid) {
        if (value != null) {
            owners.put(value, fieldUuid);
        }
    }

    protected static class FieldOwnerCandidate {
        protected String fieldUuid;
        protected String fieldName;
        protected String displayName;
    }

    protected static Long parseIdOrNull(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(id.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    protected static String normalizeColumnName(String name) {
        String n = name != null ? name.trim() : "";
        if (n.isEmpty()) {
            return "";
        }
        String unquoted = n.replace("\"", "").replace("'", "");
        return unquoted.toLowerCase();
    }

    protected Map<Long, MetadataEntityFieldDO> loadFieldsByIds(List<EntityFieldUpsertItemVO> items) {
        if (items == null || items.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        java.util.Set<Long> ids = new java.util.HashSet<>();
        for (EntityFieldUpsertItemVO item : items) {
            Long id = parseIdOrNull(item.getId());
            if (id != null) {
                ids.add(id);
            }
        }
        if (ids.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        QueryWrapper queryWrapper = QueryWrapper.create().in(MetadataEntityFieldDO::getId, ids);
        List<MetadataEntityFieldDO> list = metadataEntityFieldRepository.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        Map<Long, MetadataEntityFieldDO> map = new HashMap<>();
        for (MetadataEntityFieldDO f : list) {
            if (f != null && f.getId() != null) {
                map.put(f.getId(), f);
            }
        }
        return map;
    }

    protected MetadataEntityFieldDO buildUpdatedSnapshot(MetadataEntityFieldDO origin, EntityFieldUpsertItemVO item, Integer maxLength) {
        MetadataEntityFieldDO snapshot = new MetadataEntityFieldDO();
        snapshot.setId(origin.getId());
        snapshot.setFieldUuid(origin.getFieldUuid());
        snapshot.setEntityUuid(origin.getEntityUuid());
        snapshot.setApplicationId(origin.getApplicationId());
        snapshot.setFieldName(item.getFieldName() != null ? item.getFieldName() : origin.getFieldName());
        snapshot.setDisplayName(item.getDisplayName() != null ? item.getDisplayName() : origin.getDisplayName());
        snapshot.setFieldType(item.getFieldType() != null ? item.getFieldType() : origin.getFieldType());
        snapshot.setDataLength(maxLength != null ? maxLength : origin.getDataLength());
        snapshot.setDecimalPlaces(item.getDecimalPlaces() != null ? item.getDecimalPlaces() : origin.getDecimalPlaces());
        snapshot.setDefaultValue(item.getDefaultValue() != null ? item.getDefaultValue() : origin.getDefaultValue());
        snapshot.setDescription(item.getDescription() != null ? item.getDescription() : origin.getDescription());
        snapshot.setIsRequired(item.getIsRequired() != null ? item.getIsRequired() : origin.getIsRequired());
        snapshot.setIsUnique(item.getIsUnique() != null ? item.getIsUnique() : origin.getIsUnique());
        snapshot.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : origin.getSortOrder());
        snapshot.setIsSystemField(item.getIsSystemField() != null ? item.getIsSystemField() : origin.getIsSystemField());
        snapshot.setIsPrimaryKey(origin.getIsPrimaryKey());
        snapshot.setVersionTag(origin.getVersionTag());
        snapshot.setDictTypeId(item.getDictTypeId() != null ? item.getDictTypeId() : origin.getDictTypeId());
        snapshot.setStatus(origin.getStatus());
        snapshot.setFieldCode(origin.getFieldCode());
        return snapshot;
    }

    /**
     * 生成删除字段的DDL语句
     * <p>
     * 注意：调用此方法前必须先使用checkColumnExists()检查列是否存在
     * 不使用IF EXISTS语法以兼容达梦等数据库
     */
    protected String generateDropColumnDDL(String tableName, String fieldName) {
        // 不使用IF EXISTS，因为已在dropColumnFromTable()中提前检查
        return "ALTER TABLE \"" + tableName + "\" DROP COLUMN \"" + fieldName + "\";";
    }

    /**
     * 字段类型映射
     */
    protected String mapFieldType(String fieldType, Integer dataLength) {
        // 使用新的字段类型服务从MetadataComponentFieldTypeDO中读取映射关系
        return componentFieldTypeService.mapFieldTypeToDatabaseType(fieldType, dataLength);
    }

    /**
     * 格式化默认值用于SQL语句
     * 根据字段类型判断是否需要用单引号包裹默认值
     *
     * @param fieldType    字段类型
     * @param defaultValue 默认值
     * @return 格式化后的默认值
     */
    protected String formatDefaultValue(String fieldType, String defaultValue) {
        if (defaultValue == null || defaultValue.trim().isEmpty()) {
            return null;
        }

        // 数值类型：不需要单引号
        // NUMBER, INTEGER, DECIMAL, FLOAT, DOUBLE, BIGINT, SMALLINT, TINYINT 等
        if (fieldType.contains("NUMBER") || fieldType.contains("INTEGER") ||
                fieldType.contains("DECIMAL") || fieldType.contains("FLOAT") ||
                fieldType.contains("DOUBLE") || fieldType.contains("BIGINT") ||
                fieldType.contains("SMALLINT") || fieldType.contains("TINYINT") ||
                fieldType.contains("BOOLEAN") || fieldType.contains("BOOL")) {
            return defaultValue;
        }

        // 特殊函数或表达式（如CURRENT_TIMESTAMP、NOW()等）：不需要单引号
        String upperValue = defaultValue.toUpperCase();
        if (upperValue.contains("CURRENT_") || upperValue.contains("NOW(") ||
                upperValue.contains("UUID") || upperValue.contains("NULL")) {
            return defaultValue;
        }

        // 如果已经包含单引号，直接返回
        if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
            return defaultValue;
        }

        // 其他类型（TEXT, VARCHAR, CHAR, DATE, DATETIME, TIME, USER等）：需要单引号
        // 对单引号进行转义处理（PostgreSQL使用两个单引号表示一个单引号）
        String escapedValue = defaultValue.replace("'", "''");
        return "'" + escapedValue + "'";
    }

    /**
     * 生成字段编码
     * 将字段名转换为大写，下划线保持不变
     *
     * @param fieldName 字段名
     * @return 字段编码
     */
    protected String generateFieldCode(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }
        return fieldName.toUpperCase();
    }

    /**
     * 校验实体是否允许修改表结构
     *
     * @param entityUuid 实体UUID
     */
}
