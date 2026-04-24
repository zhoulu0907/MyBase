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
public abstract class MetadataEntityFieldBuildServiceDdlSupport extends MetadataEntityFieldBuildServicePhysicalOperationSupport {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityFieldsByEntityId(String entityId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityId.trim());
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(queryWrapper);

        // 获取业务实体信息，用于批量删除物理表字段
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(entityId);
            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                datasource = metadataDatasourceBuildService.getDatasourceByUuid(businessEntity.getDatasourceUuid());
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (MetadataEntityFieldDO field : fields) {
            // 删除数据库记录
            fieldOptionService.deleteByFieldId(field.getFieldUuid());
            fieldConstraintService.deleteByFieldId(field.getFieldUuid());
            autoNumberConfigBuildService.deleteByFieldId(field.getFieldUuid());
            validationRequiredService.deleteByFieldId(field.getFieldUuid());
            validationUniqueService.deleteByFieldId(field.getFieldUuid());
            validationLengthService.deleteByFieldId(field.getFieldUuid());
            metadataEntityFieldRepository.removeById(field.getId());

            // 从物理表删除字段
            if (businessEntity != null && datasource != null) {
                try {
                    dropColumnFromTable(datasource, businessEntity.getTableName(), field.getFieldName());
                } catch (Exception e) {
                    log.error("从物理表删除字段 {} 失败: {}", field.getFieldName(), e.getMessage(), e);
                    // 不抛出异常，继续删除其他字段
                }
            }
        }
    }

    /**
     * 添加列到表
     * <p>
     * 使用 Anyline 原生 API 添加列，自动适配不同数据库（PostgreSQL、达梦、人大金仓等）。
     */
    protected void addColumnToTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        addColumnToTable(datasource, tableName, field, null);
    }

    protected void addColumnToTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field,
            java.util.Set<String> existingColumns) {
        try {
            log.info("开始为表 {} 添加列 {}, 数据源: {} ({})",
                    tableName, field.getFieldName(),
                    datasource.getDatasourceName(), datasource.getDatasourceType());

            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 首先检查表是否存在
                if (existingColumns == null) {
                    if (!AnylineDdlHelper.tableExists(service, tableName)) {
                        String errorMessage = "表 " + tableName + " 不存在，请先创建表。";
                        log.error("添加字段失败: {}", errorMessage);
                        throw new RuntimeException(errorMessage);
                    }
                }

                // 检查列是否已存在
                String normalizedColumnName = normalizeColumnName(field.getFieldName());
                boolean columnExists = existingColumns != null
                        ? existingColumns.contains(normalizedColumnName)
                        : AnylineDdlHelper.columnExists(service, tableName, field.getFieldName());
                if (columnExists) {
                    log.warn("列 {} 已存在于表 {} 中，跳过添加操作", field.getFieldName(), tableName);
                    return null;
                }

                // 根据数据库类型选择不同的策略
                String datasourceType = datasource.getDatasourceType();
                DatabaseType dbType = DatabaseType.valueOf(datasourceType);

                if (useCustomPgCompatibleDdl(dbType)) {
                    // PostgreSQL/OpenGauss/KingBase：使用手动 DDL（解决保留字和类型兼容性问题）
                    String ddl = generateAddColumnDDL(tableName, field);
                    AnylineDdlHelper.executeDDL(service, ddl);
                    if (existingColumns == null) {
                        AnylineDdlHelper.clearMetadataCache();
                    }
                    log.info("成功为表 {} 添加列: {} (使用自定义DDL)", tableName, field.getFieldName());
                    if (existingColumns != null) {
                        existingColumns.add(normalizedColumnName);
                    }
                    return null;
                }

                // 使用 Anyline 原生 API 构建 Column 对象
                String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
                // 所有校验在应用层进行，DDL中不设置 NOT NULL 约束，因此 nullable 始终为 true
                boolean nullable = true;
                
                Column column = AnylineDdlHelper.buildColumn(
                        tableName,
                        field.getFieldName(),
                        columnType,
                        nullable,
                        formatDefaultValueForAnyline(field.getFieldType(), field.getDefaultValue()),
                        field.getDescription()
                );

                // 使用 Anyline 原生 API 添加列
                AnylineDdlHelper.addColumn(service, column);

                log.info("成功为表 {} 添加列: {}", tableName, field.getFieldName());
                if (existingColumns != null) {
                    existingColumns.add(normalizedColumnName);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("为表 {} 添加列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("添加列失败: " + e.getMessage(), e);
        }
    }

    /**
     * 格式化默认值用于 Anyline Column 设置
     * <p>
     * 直接返回原始值，不添加引号（Anyline 会自动处理）
     */
    protected Object formatDefaultValueForAnyline(String fieldType, String defaultValue) {
        if (defaultValue == null || defaultValue.trim().isEmpty()) {
            return null;
        }
        // Anyline 会自动处理默认值的类型转换和引号，直接返回原始值
        return defaultValue;
    }

    /**
     * 检查表是否存在
     * <p>
     * 使用Anyline元数据API，自动适配不同数据库（PostgreSQL、达梦、金仓等）
     * 避免手动拼接SQL和硬编码LIMIT语法
     */
    protected boolean checkTableExists(AnylineService<?> service, String tableName) {
        // 委托给 AnylineDdlHelper 处理
        return AnylineDdlHelper.tableExists(service, tableName);
    }

    /**
     * 获取当前连接的数据库名称（用于调试）
     */
    protected String getCurrentDatabase(AnylineService<?> service) {
        try {
            DataSet resultSet = service.querys("SELECT current_database()");
            if (resultSet != null && resultSet.size() > 0) {
                DataRow row = resultSet.getRow(0);
                return row.get("current_database").toString();
            }
        } catch (Exception e) {
            log.debug("获取当前数据库名称失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 检查列是否存在于表中
     * <p>
     * 使用Anyline元数据API，自动适配不同数据库（PostgreSQL、达梦、金仓等）
     * 避免硬编码ILIKE、information_schema和LIMIT语法
     * <p>
     * 注意：PostgreSQL会将不带引号的标识符自动转为小写，因此在检查列是否存在时需要
     * 先尝试精确匹配，再尝试小写匹配，最后再进行忽略大小写的模糊匹配
     *
     * @param service    AnylineService实例
     * @param tableName  表名
     * @param columnName 列名
     * @return 如果列存在返回true，否则返回false
     */
    protected boolean checkColumnExists(AnylineService<?> service, String tableName, String columnName) {
        // 委托给 AnylineDdlHelper 处理
        return AnylineDdlHelper.columnExists(service, tableName, columnName);
    }

    /**
     * 修改表中的列
     * <p>
     * 采用混合策略：
     * - 达梦(DM)数据库：使用 Anyline 原生 API
     * - PostgreSQL/KingBase：保留手动 DDL 方式，因需要 USING 子句处理类型转换
     * <p>
     * 说明：即使 field 有 ID，也需要检查物理表中列是否真实存在
     * 因为可能存在元数据与物理表不一致的情况（如之前物理表操作失败、表被手动重建等）
     *
     * @param datasource 数据源信息
     * @param tableName  表名
     * @param field      字段信息
     */
    protected void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        alterColumnInTable(datasource, tableName, field, null);
    }

    protected void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field,
            java.util.Set<String> existingColumns) {
        try {
            TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 先校验表是否存在
                if (existingColumns == null) {
                    if (!AnylineDdlHelper.tableExists(service, tableName)) {
                        throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                    }
                }

                // 检查列是否存在，避免元数据与物理表不一致导致的问题
                String fieldName = field.getFieldName();
                // 修复 PostgreSQL 保留字 user 导致的问题
                if ("user".equalsIgnoreCase(fieldName)) {
                    fieldName = "\"" + fieldName + "\"";
                }
                boolean columnExists = existingColumns != null
                        ? existingColumns.contains(normalizeColumnName(fieldName))
                        : AnylineDdlHelper.columnExists(service, tableName, fieldName);

                if (!columnExists) {
                    // 列不存在，应该使用ADD操作而非ALTER
                    log.warn("准备修改表 {} 的列 {} 时发现列不存在（字段ID: {}），将改为新增列操作",
                            tableName, fieldName, field.getId());
                    addColumnToTable(datasource, tableName, field, existingColumns);
                } else {
                    // 列存在，正常执行ALTER操作
                    log.info("准备修改表 {} 的列: {}, 字段ID: {}", tableName, fieldName, field.getId());

                    // 根据数据库类型选择不同的策略
                    String datasourceType = datasource.getDatasourceType();
                    DatabaseType dbType = DatabaseType.valueOf(datasourceType);

                    if (dbType == DatabaseType.DM) {
                        // 达梦数据库：使用 Anyline 原生 API
                        alterColumnWithAnyline(service, tableName, field);
                    } else {
                        // PostgreSQL/KingBase：使用手动 DDL（需要 USING 子句处理类型转换）
                        String alterColumnDDL = generateAlterColumnDDL(datasourceType, tableName, field);
                        AnylineDdlHelper.alterColumnWithDDL(service, tableName, fieldName, alterColumnDDL);
                    }

                    log.info("成功修改表 {} 的列: {}", tableName, fieldName);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("修改表 {} 的列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("修改列失败", e);
        }
    }

    /**
     * 使用 Anyline 原生 API 修改列（适用于达梦数据库）
     * <p>
     * 注意：所有校验（必填、唯一、长度、范围、格式等）均在应用层进行，不在DDL中生成约束
     *
     * @param service   Anyline 服务实例
     * @param tableName 表名
     * @param field     字段信息
     */
    protected void alterColumnWithAnyline(AnylineService<?> service, String tableName, MetadataEntityFieldDO field) {
        // 将业务字段类型映射为数据库类型
        String dbTypeName = mapFieldType(field.getFieldType(), field.getDataLength());
        // 所有校验在应用层进行，DDL中不设置 NOT NULL 约束，因此 nullable 始终为 true
        boolean nullable = true;

        // 构建 Anyline Column 对象
        Column column = AnylineDdlHelper.buildColumn(
                field.getFieldName(),
                dbTypeName,
                nullable,
                formatDefaultValueForAnyline(field.getFieldType(), field.getDefaultValue()),
                field.getDescription()
        );

        // 使用 Anyline 原生 API 修改列
        AnylineDdlHelper.alterColumn(service, tableName, column);
    }

    /**
     * 重命名表中的列
     * <p>
     * 使用 Anyline 原生 API 重命名列，自动适配不同数据库。
     */
    protected void renameColumnInTable(MetadataDatasourceDO datasource, String tableName, String oldName,
            String newName) {
        renameColumnInTable(datasource, tableName, oldName, newName, null);
    }

    protected void renameColumnInTable(MetadataDatasourceDO datasource, String tableName, String oldName,
            String newName, java.util.Set<String> existingColumns) {
        try {
            TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                if (existingColumns == null) {
                    if (!AnylineDdlHelper.tableExists(service, tableName)) {
                        throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                    }

                    // 使用 Anyline 原生 API 重命名列（内部会检查列是否存在）
                    AnylineDdlHelper.renameColumn(service, tableName, oldName, newName);
                    return null;
                }

                String normalizedOld = normalizeColumnName(oldName);
                String normalizedNew = normalizeColumnName(newName);
                if (!existingColumns.contains(normalizedOld)) {
                    log.warn("列 {} 不存在于表 {} 中，跳过重命名", oldName, tableName);
                    return null;
                }
                if (existingColumns.contains(normalizedNew)) {
                    log.warn("列 {} 已存在于表 {} 中，跳过重命名", newName, tableName);
                    return null;
                }

                String datasourceType = datasource.getDatasourceType();
                DatabaseType dbType = DatabaseType.valueOf(datasourceType);
                String oldUnquoted = oldName == null ? null : oldName.replace("\"", "");
                String newUnquoted = newName == null ? null : newName.replace("\"", "");

                if (useCustomPgCompatibleDdl(dbType)) {
                    String ddl = "ALTER TABLE \"" + tableName + "\" RENAME COLUMN \"" + oldUnquoted + "\" TO \"" + newUnquoted + "\";";
                    AnylineDdlHelper.executeDDL(service, ddl);
                    if (existingColumns == null) {
                        AnylineDdlHelper.clearMetadataCache();
                    }
                } else {
                    org.anyline.metadata.Column column = new org.anyline.metadata.Column(oldUnquoted);
                    column.setTable(new org.anyline.metadata.Table<>(tableName));
                    service.ddl().rename(column, newUnquoted);
                    if (existingColumns == null) {
                        AnylineDdlHelper.clearMetadataCache();
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("重命名表 {} 列 {} 到 {} 失败: {}", tableName, oldName, newName, e.getMessage(), e);
            throw new RuntimeException("重命名列失败", e);
        }
    }

    /**
     * 从表中删除列
     * <p>
     * 使用 Anyline 原生 API 删除列，自动适配不同数据库（PostgreSQL、达梦、人大金仓等）。
     */
    protected void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName) {
        dropColumnFromTable(datasource, tableName, fieldName, null);
    }

    protected void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName,
            java.util.Set<String> existingColumns) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                if (existingColumns == null) {
                    // 使用 Anyline 原生 API 删除列（内部会检查列是否存在）
                    AnylineDdlHelper.dropColumn(service, tableName, fieldName);
                } else {
                    String normalized = normalizeColumnName(fieldName);
                    if (!existingColumns.contains(normalized)) {
                        log.info("列 {} 不存在于表 {}，跳过删除", fieldName, tableName);
                        return null;
                    }
                    String datasourceType = datasource.getDatasourceType();
                    DatabaseType dbType = DatabaseType.valueOf(datasourceType);
                    String unquoted = fieldName == null ? null : fieldName.replace("\"", "");
                    if (useCustomPgCompatibleDdl(dbType)) {
                        String ddl = "ALTER TABLE \"" + tableName + "\" DROP COLUMN \"" + unquoted + "\";";
                        AnylineDdlHelper.executeDDL(service, ddl);
                        if (existingColumns == null) {
                            AnylineDdlHelper.clearMetadataCache();
                        }
                    } else {
                        org.anyline.metadata.Column column = new org.anyline.metadata.Column(unquoted);
                        column.setTable(new org.anyline.metadata.Table<>(tableName));
                        service.ddl().drop(column);
                        if (existingColumns == null) {
                            AnylineDdlHelper.clearMetadataCache();
                        }
                    }
                }

                log.info("成功从表 {} 删除列: {}", tableName, fieldName);
                return null;
            });
        } catch (Exception e) {
            log.error("从表 {} 删除列 {} 失败: {}", tableName, fieldName, e.getMessage(), e);
            throw new RuntimeException("删除列失败", e);
        }
    }

    /**
     * 生成添加字段的DDL语句
     * <p>
     * 注意：调用此方法前必须先使用checkColumnExists()检查列是否存在
     * 不使用IF NOT EXISTS语法以兼容达梦等数据库
     * 所有校验（必填、唯一、长度、范围、格式等）均在应用层进行，不在DDL中生成约束
     */
    protected String generateAddColumnDDL(String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();
        // 不使用IF NOT EXISTS，因为已在addColumnToTable()中提前检查
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ADD COLUMN \"")
                .append(field.getFieldName()).append("\" ");

        // 字段类型映射
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append(columnType);

        // 默认值 - 根据字段类型正确格式化
        if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
            String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
            if (formattedValue != null) {
                ddl.append(" DEFAULT ").append(formattedValue);
            }
        }

        ddl.append(";");

        // 添加字段注释
        if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
            ddl.append("\nCOMMENT ON COLUMN \"").append(tableName).append("\".\"")
                    .append(field.getFieldName()).append("\" IS '").append(field.getDescription()).append("';");
        }

        return ddl.toString();
    }

    protected boolean useCustomPgCompatibleDdl(DatabaseType dbType) {
        return dbType == DatabaseType.PostgreSQL
                || dbType == DatabaseType.OpenGauss
                || dbType == DatabaseType.KingBase;
    }

    /**
     * 生成修改字段的DDL语句（跨数据库兼容）
     * <p>
     * 根据不同数据库类型生成对应的ALTER COLUMN语法：
     * - PostgreSQL/KingBase: ALTER COLUMN ... TYPE
     * - 达梦(DM): MODIFY "column" TYPE
     * <p>
     * 注意：所有校验（必填、唯一、长度、范围、格式等）均在应用层进行，不在DDL中生成约束
     *
     * @param datasourceType 数据库类型
     * @param tableName      表名
     * @param field          字段信息
     * @return DDL语句
     */
    protected String generateAlterColumnDDL(String datasourceType, String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        String fieldName = field.getFieldName();

        try {
            DatabaseType dbType = DatabaseType.valueOf(datasourceType);

            switch (dbType) {
                case PostgreSQL:
                case KingBase:
                    // PostgreSQL/金仓：修改字段类型
                    ddl.append("ALTER TABLE \"").append(tableName)
                            .append("\" ALTER COLUMN \"").append(fieldName)
                            .append("\" TYPE ").append(columnType);

                    // 为需要类型转换的字段添加 USING 子句
                    String usingClause = generateUsingClause(field.getFieldType(), fieldName);
                    if (usingClause != null) {
                        ddl.append(" USING ").append(usingClause);
                    }

                    ddl.append(";\n");

                    // 修改默认值 - 根据字段类型正确格式化
                    if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                        String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
                        if (formattedValue != null) {
                            ddl.append("ALTER TABLE \"").append(tableName)
                                    .append("\" ALTER COLUMN \"").append(fieldName)
                                    .append("\" SET DEFAULT ").append(formattedValue).append(";\n");
                        }
                    }
                    break;

                case DM:
                    // 达梦：使用MODIFY，修改类型和默认值
                    ddl.append("ALTER TABLE \"").append(tableName)
                            .append("\" MODIFY \"").append(fieldName)
                            .append("\" ").append(columnType);

                    // 添加默认值 - 根据字段类型正确格式化
                    if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                        String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
                        if (formattedValue != null) {
                            ddl.append(" DEFAULT ").append(formattedValue);
                        }
                    }

                    ddl.append(";\n");
                    break;

                default:
                    // 不支持的数据库类型，使用PostgreSQL语法作为默认
                    log.warn("不支持的数据库类型: {}，使用PostgreSQL语法", datasourceType);
                    ddl.append("ALTER TABLE \"").append(tableName)
                            .append("\" ALTER COLUMN \"").append(fieldName)
                            .append("\" TYPE ").append(columnType).append(";\n");
            }

            // 更新字段注释（所有支持的数据库都使用COMMENT ON语法）
            if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
                ddl.append("COMMENT ON COLUMN \"").append(tableName).append("\".\"")
                        .append(fieldName).append("\" IS '").append(field.getDescription()).append("';");
            }

        } catch (IllegalArgumentException e) {
            // 数据库类型无效，使用PostgreSQL语法作为默认
            log.warn("无效的数据库类型: {}，使用PostgreSQL语法", datasourceType, e);
            ddl.append("ALTER TABLE \"").append(tableName)
                    .append("\" ALTER COLUMN \"").append(fieldName)
                    .append("\" TYPE ").append(columnType).append(";");
        }

        return ddl.toString();
    }

    /**
     * 生成PostgreSQL/KingBase的USING子句，用于类型转换
     * <p>
     * 注意：根据产品需求，不再使用 USING 子句进行数据类型转换。
     * 如果数据不兼容目标类型，数据库将直接抛出异常，由调用方处理。
     * 
     * @param fieldType 目标字段类型
     * @param fieldName 字段名
     * @return 始终返回null，不使用USING子句
     */
    protected String generateUsingClause(String fieldType, String fieldName) {
        // 不再使用 USING 子句进行数据转换
        // 如果数据不兼容目标类型，数据库将直接抛出异常
        return null;
    }

}
