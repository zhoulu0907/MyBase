package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ChildEntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityWithFieldsBatchQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERDiagramRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EREntityVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERFieldVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERRelationshipVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldOptionBuildService;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataAppAndDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataSystemFieldsCoreService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.enums.BusinessEntityTypeEnum;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.dal.database.FieldTypeMappingRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.FieldTypeMappingDO;
import com.cmsr.onebase.framework.aynline.AnylineDdlHelper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.service.AnylineService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_CODE_DUPLICATE;
/**
 * Split segment of metadata build service implementation.
 */
@Slf4j
public abstract class MetadataBusinessEntityBuildServiceCore extends MetadataBusinessEntityBuildServiceQuerySupport {
    protected Long safeParseLong(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            log.warn("无法将字符串转换为Long: {}", str);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)

    public Long createBusinessEntity(@Valid BusinessEntitySaveReqVO createReqVO) {
        // ID转UUID兼容处理：支持前端传入datasourceId或datasourceUuid
        String resolvedDatasourceUuid = idUuidConverter.resolveDatasourceUuidOptional(
                createReqVO.getDatasourceUuid(), createReqVO.getDatasourceId());
        createReqVO.setDatasourceUuid(resolvedDatasourceUuid);

        // 如果entityType为空，默认设置为自建表类型(1)
        if (createReqVO.getEntityType() == null) {
            createReqVO.setEntityType(BusinessEntityTypeEnum.SELF_BUILT.getCode());
            log.info("实体类型未指定，默认设置为自建表类型(1)");
        }

        // 预先获取系统字段信息，避免在事务中查询
        List<MetadataSystemFieldsDO> systemFields = null;
        if (BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType())) {
            systemFields = getSystemFieldsWithCache();
        }

        // 校验编码唯一性（只有当code不为空时才校验）
        Long appId = safeParseLong(createReqVO.getApplicationId());
        if (CharSequenceUtil.isNotEmpty(createReqVO.getCode()) && appId != null) {
            validateBusinessEntityCodeUniqueWithLock(null, createReqVO.getCode(), appId);
        }

        // 校验实体类型
        validateEntityType(createReqVO.getEntityType());

        // 插入业务实体
        MetadataBusinessEntityDO businessEntity = BeanUtils.toBean(createReqVO, MetadataBusinessEntityDO.class);
        businessEntity.setApplicationId(appId);
        
        // 设置datasourceUuid
        String datasourceUuid = createReqVO.getDatasourceUuid();
        if (datasourceUuid != null && !datasourceUuid.isEmpty()) {
            businessEntity.setDatasourceUuid(datasourceUuid);
        }

        // 处理code字段：如果为空或空字符串，则生成UUID
        if (CharSequenceUtil.isEmpty(createReqVO.getCode())) {
            businessEntity.setCode(IdUtil.simpleUUID());
        }

        // 根据实体类型处理表名，并加上 appUid 前缀（如有）
        handleTableNameByEntityType(businessEntity, createReqVO);
        String appUid = null;
        if (appId != null && businessEntity.getDatasourceUuid() != null) {
            appUid = metadataAppAndDatasourceCoreService.getAppUidByAppIdAndDatasourceUuid(
                    appId, businessEntity.getDatasourceUuid()
            );
        }
        if (appUid != null && !appUid.isBlank()) {
            String rawName = businessEntity.getTableName();
            // 避免重复前缀：如果已经以 appUid_ 开头则不再重复
            String prefix = appUid + "_";
            if (rawName == null || rawName.isBlank()) {
                rawName = CharSequenceUtil.isNotEmpty(createReqVO.getCode()) ? createReqVO.getCode().toLowerCase() : IdUtil.simpleUUID();
            }
            if (!rawName.startsWith(prefix)) {
                businessEntity.setTableName(prefix + rawName);
            }
        }

        // 根据实体类型决定是否需要创建物理表
        FieldTypeMappingContext fieldTypeMappingContext = null;
        if (BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType())) {
            // 同步创建物理表，确保事务原子性
            fieldTypeMappingContext = createPhysicalTableForEntitySync(businessEntity, createReqVO, systemFields);
        }

        // 生成实体UUID（如果为空）
        if (businessEntity.getEntityUuid() == null || businessEntity.getEntityUuid().isEmpty()) {
            businessEntity.setEntityUuid(UuidUtils.getUuid());
        }

        // 插入业务实体到数据库，如果前面创建物理表失败，这里不会执行
        metadataBusinessEntityRepository.save(businessEntity);

        // 如果需要创建物理表，保存系统字段信息到 metadata_entity_field 表
        if (BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType()) && systemFields != null && appId != null) {
            saveEntityFields(businessEntity.getEntityUuid(), systemFields, appId,
                    fieldTypeMappingContext != null ? fieldTypeMappingContext.defaultMappings : null);
        }

        if (!BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType())) {
            BusinessEntityTypeEnum entityTypeEnum = BusinessEntityTypeEnum.getByCode(createReqVO.getEntityType());
            String typeName = entityTypeEnum != null ? entityTypeEnum.getName() : "未知类型";
            log.info("实体类型为 {} ({}), 跳过物理表创建", createReqVO.getEntityType(), typeName);
        } else {
            log.info("成功创建业务实体 {} 及其物理表: {}", businessEntity.getDisplayName(), businessEntity.getTableName());
        }

        return businessEntity.getId();
    }

    /**
     * 校验实体类型有效性
     *
     * @param entityType 实体类型
     */
    protected void validateEntityType(Integer entityType) {
        if (entityType != null && !BusinessEntityTypeEnum.isValidCode(entityType)) {
            throw new IllegalArgumentException("无效的实体类型: " + entityType);
        }
    }

    /**
     * 带锁的编码唯一性校验，避免并发冲突
     *
     * @param id 实体ID（更新时传入）
     * @param code 实体编码
     * @param appId 应用ID
     */
    protected void validateBusinessEntityCodeUniqueWithLock(Long id, String code, Long appId) {
        // 使用更安全的并发校验方式
        try {
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(MetadataBusinessEntityDO::getCode, code)
                    .eq(MetadataBusinessEntityDO::getApplicationId, appId)
                    .ne(MetadataBusinessEntityDO::getId, id, id != null);

            // 先进行普通查询，如果存在则抛出异常
            MetadataBusinessEntityDO existEntity = metadataBusinessEntityRepository.getOne(queryWrapper);
            if (existEntity != null) {
                throw exception(BUSINESS_ENTITY_CODE_DUPLICATE);
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("BUSINESS_ENTITY_CODE_DUPLICATE")) {
                throw e; // 重新抛出业务异常
            }
            log.warn("编码唯一性校验异常，可能由于并发导致: {}", e.getMessage());
            // 对于数据库异常，可能是并发导致的，让调用方重试
            throw new RuntimeException("编码校验失败，请重试", e);
        }
    }

    /**
     * 获取系统字段信息（带缓存优化）
     */
    protected List<MetadataSystemFieldsDO> getSystemFieldsWithCache() {
        long currentTime = System.currentTimeMillis();

        // 检查缓存是否有效
        if (systemFieldsCache != null && (currentTime - lastCacheTime) < CACHE_EXPIRE_TIME) {
            log.debug("使用系统字段缓存，缓存大小: {}", systemFieldsCache.size());
            return new ArrayList<>(systemFieldsCache); // 返回副本避免并发修改
        }

        // 重新查询并更新缓存
        synchronized (this) {
            // 双重检查
            if (systemFieldsCache != null && (currentTime - lastCacheTime) < CACHE_EXPIRE_TIME) {
                return new ArrayList<>(systemFieldsCache);
            }

            List<MetadataSystemFieldsDO> fields = getSystemFields();
            systemFieldsCache = new ArrayList<>(fields);
            lastCacheTime = currentTime;

            log.info("刷新系统字段缓存，字段数量: {}", fields.size());
            return new ArrayList<>(fields);
        }
    }

    /**
     * 同步创建物理表，确保事务原子性
     *
     * @param businessEntity 业务实体
     * @param createReqVO 创建请求VO
     * @param systemFields 系统字段列表
     */
    protected FieldTypeMappingContext createPhysicalTableForEntitySync(MetadataBusinessEntityDO businessEntity,
                                                 BusinessEntitySaveReqVO createReqVO,
                                                 List<MetadataSystemFieldsDO> systemFields) {
        try {
            // 1. 通过数据源UUID获取对应的数据源信息
            String datasourceUuid = createReqVO.getDatasourceUuid();
            if (datasourceUuid == null || datasourceUuid.isEmpty()) {
                throw new RuntimeException("数据源UUID为空，无法创建物理表");
            }
            MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasourceByUuid(datasourceUuid);
            if (datasource == null) {
                throw new RuntimeException("未找到数据源UUID为 " + datasourceUuid + " 的数据源配置");
            }

            // 2. 生成 DDL 并在数据源内建物理表
            java.util.Map<String, FieldTypeMappingDO> defaultMappings =
                    fieldTypeMappingRepository.getDefaultMappingsByDatabaseType(datasource.getDatasourceType());
            createPhysicalTable(datasource, businessEntity.getTableName(), systemFields, defaultMappings);

            // 3. 预先保存实体字段信息到 metadata_entity_field 表（待实体创建成功后会一起提交）
            // 注意：这里不能直接调用saveEntityFields，因为它需要实体ID，而此时实体还未保存
            // 我们将在主方法中处理字段保存

            log.info("成功为业务实体 {} 创建物理表: {}", businessEntity.getDisplayName(), businessEntity.getTableName());
            return new FieldTypeMappingContext(datasource.getDatasourceType(), defaultMappings);
        } catch (Exception e) {
            log.error("创建业务实体物理表失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建物理表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据实体类型处理表名
     *
     * @param businessEntity 业务实体DO
     * @param createReqVO 创建请求VO
     */
    protected void handleTableNameByEntityType(MetadataBusinessEntityDO businessEntity, BusinessEntitySaveReqVO createReqVO) {
        if (BusinessEntityTypeEnum.REUSE_EXISTING.getCode().equals(createReqVO.getEntityType())) {
            // 复用已有表，必须指定表名
            if (businessEntity.getTableName() == null || businessEntity.getTableName().trim().isEmpty()) {
                throw new IllegalArgumentException("复用已有表时必须指定表名");
            }
        } else {
            // 自建表或中间表，如果没有指定表名则使用编码作为表名
            if (businessEntity.getTableName() == null || businessEntity.getTableName().trim().isEmpty()) {
                businessEntity.setTableName(createReqVO.getCode().toLowerCase());
            }
        }
    }



    /**
     * 获取系统字段信息
     */
    protected List<MetadataSystemFieldsDO> getSystemFields() {
        // 构建查询启用的系统字段的条件
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataSystemFieldsDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus())
                .orderBy(MetadataSystemFieldsDO::getId, true);
        List<MetadataSystemFieldsDO> systemFields = metadataSystemFieldsCoreService.findAllByConfig(queryWrapper);

        log.info("获取系统字段结果: 总数={}, is_enabled条件={}",
                systemFields.size(), CommonStatusEnum.ENABLE.getStatus());

        // 如果启用的系统字段为空，尝试获取所有系统字段（忽略启用状态）
        if (systemFields.isEmpty()) {
            log.warn("未找到启用的系统字段，尝试获取所有系统字段");
            QueryWrapper allQueryWrapper = QueryWrapper.create()
                    .orderBy(MetadataSystemFieldsDO::getId, true);
            systemFields = metadataSystemFieldsCoreService.findAllByConfig(allQueryWrapper);
            log.info("获取所有系统字段结果: 总数={}", systemFields.size());

            // 打印前几个字段的详细信息用于调试
            for (int i = 0; i < Math.min(3, systemFields.size()); i++) {
                MetadataSystemFieldsDO field = systemFields.get(i);
                log.info("系统字段[{}]: 名称={}, 类型={}, 启用状态={}, 雪花ID={}",
                        i, field.getFieldName(), field.getFieldType(),
                        field.getIsEnabled(), field.getIsSnowflakeId());
            }
        }

        return systemFields;
    }

    /**
     * 保存实体字段信息到 metadata_entity_field 表
     *
     * @param entityUuid 业务实体UUID
     * @param systemFields 系统字段列表
     * @param appId 应用ID
     */
    protected void saveEntityFields(String entityUuid, List<MetadataSystemFieldsDO> systemFields, Long appId,
                                  java.util.Map<String, FieldTypeMappingDO> defaultMappings) {
        int sortOrder = 1;
        java.util.Map<String, FieldTypeMappingDO> mappings = defaultMappings != null
                ? defaultMappings
                : fieldTypeMappingRepository.getDefaultMappingsByDatabaseType("PostgreSQL");
        for (MetadataSystemFieldsDO systemField : systemFields) {
            // 特殊处理 parent_id：不是主键、不是必填、不是唯一
            boolean isParentId = "parent_id".equalsIgnoreCase(systemField.getFieldName());
            int isPrimaryKey = isParentId ? StatusEnumUtil.NO : BooleanStatusEnum.toStatusValue(systemField.getIsSnowflakeId());
            int isRequired = isParentId ? StatusEnumUtil.NO : BooleanStatusEnum.toStatusValue(systemField.getIsRequired());
            int isUnique = isParentId ? StatusEnumUtil.NO : BooleanStatusEnum.toStatusValue(systemField.getIsSnowflakeId());

            FieldTypeMappingDO mapping = null;
            if (systemField.getFieldType() != null && !systemField.getFieldType().isBlank()) {
                mapping = mappings.get(systemField.getFieldType().trim().toUpperCase());
            }
            
            MetadataEntityFieldDO entityField = new MetadataEntityFieldDO();
            entityField.setEntityUuid(entityUuid);
            entityField.setFieldName(systemField.getFieldName());
            // 优先使用系统字段的显示名称，为空则回退为字段名
            entityField.setDisplayName(CharSequenceUtil.isNotEmpty(systemField.getDisplayName())
                ? systemField.getDisplayName()
                : systemField.getFieldName());
            entityField.setFieldType(systemField.getFieldType());
            entityField.setDataLength(mapping != null ? mapping.getDefaultLength() : null);
            entityField.setDecimalPlaces(mapping != null && mapping.getDefaultDecimalPlaces() != null && mapping.getDefaultDecimalPlaces() > 0
                    ? mapping.getDefaultDecimalPlaces()
                    : null);
            entityField.setDefaultValue(systemField.getDefaultValue());
            entityField.setDescription(systemField.getDescription());
            // 使用新的枚举值：1-是，0-否
            entityField.setIsSystemField(StatusEnumUtil.YES); // 标记为系统字段：1-是
            entityField.setIsPrimaryKey(isPrimaryKey); // parent_id 强制不是主键
            entityField.setIsRequired(isRequired); // parent_id 强制不是必填  
            entityField.setIsUnique(isUnique); // parent_id 强制不是唯一
            entityField.setSortOrder(sortOrder++);
            entityField.setValidationRules(null); // 系统字段暂不设置校验规则
            entityField.setVersionTag(0L); // 默认编辑态
            entityField.setApplicationId(appId);
            entityField.setStatus(1); // 默认启用：1-启用，0-禁用
            entityField.setFieldCode(generateFieldCode(systemField.getFieldName())); // 生成字段编码

            metadataEntityFieldBuildService.createEntityFieldInternal(entityField);
        }

        log.info("成功保存 {} 个系统字段到实体字段表", systemFields.size());
    }

    /**
     * 根据字段类型获取默认数据长度
     * <p>
     * 从 metadata_field_type_mapping 表查询业务类型对应的默认长度
     */
    protected Integer getDefaultDataLength(String fieldType) {
        if (fieldType == null || fieldType.trim().isEmpty()) {
            return null;
        }
        
        FieldTypeMappingDO mapping = fieldTypeMappingRepository.getDefaultMappingByBusinessType(fieldType);
        if (mapping != null && mapping.getDefaultLength() != null) {
            return mapping.getDefaultLength();
        }
        return null;
    }

    /**
     * 根据字段类型获取默认小数位数
     * <p>
     * 从 metadata_field_type_mapping 表查询业务类型对应的默认小数位数
     */
    protected Integer getDefaultDecimalPlaces(String fieldType) {
        if (fieldType == null || fieldType.trim().isEmpty()) {
            return null;
        }
        
        FieldTypeMappingDO mapping = fieldTypeMappingRepository.getDefaultMappingByBusinessType(fieldType);
        if (mapping != null && mapping.getDefaultDecimalPlaces() != null && mapping.getDefaultDecimalPlaces() > 0) {
            return mapping.getDefaultDecimalPlaces();
        }
        return null;
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
     * 创建物理表
     * <p>
     * 使用 Anyline 原生 API 创建表，自动适配不同数据库（PostgreSQL、达梦、人大金仓等）。
     */
    protected void createPhysicalTable(MetadataDatasourceDO datasource, String tableName, List<MetadataSystemFieldsDO> systemFields,
                                     java.util.Map<String, FieldTypeMappingDO> defaultMappings) {
        int maxRetries = 3;
        Exception lastException = null;
        java.util.Map<String, FieldTypeMappingDO> mappings = defaultMappings != null
                ? defaultMappings
                : fieldTypeMappingRepository.getDefaultMappingsByDatabaseType(datasource.getDatasourceType());
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("=== 开始创建物理表 (尝试 {}/{}) ===", attempt, maxRetries);
                log.info("目标表名: {}", tableName);
                log.info("数据源类型: {}", datasource.getDatasourceType());

                // 如果不是第一次尝试，先清理失效的连接池缓存
                if (attempt > 1) {
                    log.info("第{}次重试，清理失效的数据源缓存", attempt);
                    temporaryDatasourceService.cleanupInactiveDataSources();
                }

                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 使用 Anyline 原生 API 构建 Table 对象
                Table<?> table = buildTableFromSystemFields(tableName, systemFields, mappings);

                // 记录连接的数据库信息
                try {
                    String databaseName = getDatabaseNameFromConfig(datasource);
                    log.info("当前连接的数据库: {} (类型: {})", databaseName, datasource.getDatasourceType());
                } catch (Exception e) {
                    log.debug("无法从配置获取数据库名称: {}", e.getMessage());
                }

                // 使用 Anyline 原生 API 创建表
                AnylineDdlHelper.createTable(service, table);

                log.info("=== 物理表创建完成 ===");
                log.info("成功创建物理表: {}", tableName);
                return; // 成功创建，直接返回
                
            } catch (Exception e) {
                lastException = e;
                String errorMsg = e.getMessage();
                log.error("创建物理表失败 (尝试 {}/{}): {}", attempt, maxRetries, errorMsg, e);
                
                // 检查是否是连接池关闭错误
                boolean isConnectionPoolError = errorMsg != null && 
                    (errorMsg.contains("HikariDataSource") && errorMsg.contains("has been closed") ||
                     errorMsg.contains("connection pool") ||
                     errorMsg.contains("Connection is not available") ||
                     errorMsg.contains("Pool not open"));
                
                if (isConnectionPoolError && attempt < maxRetries) {
                    log.warn("检测到连接池相关错误，将进行第{}次重试", attempt + 1);
                    try {
                        Thread.sleep(1000 * attempt); // 递增等待时间
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("创建物理表过程被中断", ie);
                    }
                    continue; // 继续重试
                } else if (attempt >= maxRetries) {
                    break; // 达到最大重试次数，退出循环
                } else {
                    // 非连接池错误，直接抛出
                    throw new RuntimeException("创建物理表失败", e);
                }
            }
        }
        
        // 所有重试都失败了
        log.error("创建物理表在{}次尝试后仍然失败", maxRetries);
        throw new RuntimeException("创建物理表失败，已重试" + maxRetries + "次", lastException);
    }

    /**
     * 根据系统字段列表构建 Anyline Table 对象
     * <p>
     * 建表规则：
     * 1. id 字段: 强制 BIGINT NOT NULL 主键
     * 2. parent_id 字段: 强制允许为空，不作为主键
     * 3. 其它字段: 按元数据 isRequired 设置 NOT NULL
     * 4. 主键优先级: id > 第一个 isSnowflakeId=1 的非 parent_id 字段 > 兜底 id
     */
    protected Table<?> buildTableFromSystemFields(String tableName, List<MetadataSystemFieldsDO> systemFields) {
        return buildTableFromSystemFields(tableName, systemFields,
                fieldTypeMappingRepository.getDefaultMappingsByDatabaseType("PostgreSQL"));
    }

    protected Table<?> buildTableFromSystemFields(String tableName, List<MetadataSystemFieldsDO> systemFields,
                                                java.util.Map<String, FieldTypeMappingDO> defaultMappings) {
        Table<?> table = new Table<>(tableName);
        table.setComment("业务实体表");

        String detectedIdField = null;
        String candidatePk = null;
        java.util.Map<String, FieldTypeMappingDO> mappings = defaultMappings != null ? defaultMappings : java.util.Map.of();

        // 预扫描是否已有 id 列
        for (MetadataSystemFieldsDO f : systemFields) {
            if ("id".equalsIgnoreCase(f.getFieldName())) {
                detectedIdField = f.getFieldName();
                break;
            }
        }

        // 构建列定义
        for (MetadataSystemFieldsDO field : systemFields) {
            String fieldName = field.getFieldName();
            if (fieldName == null || fieldName.trim().isEmpty()) {
                continue;
            }

            boolean isParentId = "parent_id".equalsIgnoreCase(fieldName);
            boolean isId = "id".equalsIgnoreCase(fieldName);

            Column column = new Column(fieldName);
            column.setTable(table);
            FieldTypeMappingDO mapping = null;
            if (field.getFieldType() != null && !field.getFieldType().isBlank()) {
                mapping = mappings.get(field.getFieldType().trim().toUpperCase());
            }
            column.setTypeName(mapFieldType(mapping, field.getFieldType()));

            // id 强制 NOT NULL；parent_id 永不加 NOT NULL；其它按 isRequired
            if (isId) {
                column.setNullable(false);
            } else if (isParentId) {
                column.setNullable(true);
            } else {
                column.setNullable(!BooleanStatusEnum.isYes(field.getIsRequired()));
            }

            // 默认值
            if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                column.setDefaultValue(field.getDefaultValue());
            }

            // 字段注释
            if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
                column.setComment(field.getDescription());
            }

            // 主键候选: 优先使用显式 id；否则记录第一个非 parent_id 且 isSnowflakeId=1 的字段
            if (isId) {
                candidatePk = fieldName;
                column.setPrimaryKey(true);
            } else if (candidatePk == null && !isParentId && BooleanStatusEnum.isYes(field.getIsSnowflakeId())) {
                candidatePk = fieldName;
            }

            table.addColumn(column);
        }

        // 如果没有 id 列且没有其它候选主键 -> 追加一个 id 列
        if (candidatePk == null) {
            if (detectedIdField == null) {
                Column idColumn = new Column("id");
                idColumn.setTable(table);
                idColumn.setTypeName("BIGINT");
                idColumn.setNullable(false);
                idColumn.setPrimaryKey(true);
                table.addColumn(idColumn);
            }
            candidatePk = "id";
        }

        // 如果主键不是 id，设置主键
        if (candidatePk != null && !"id".equalsIgnoreCase(candidatePk)) {
            Column pkColumn = table.getColumn(candidatePk);
            if (pkColumn != null) {
                pkColumn.setPrimaryKey(true);
            }
        }

        return table;
    }

    protected static final class FieldTypeMappingContext {
        protected final String databaseType;
        protected final java.util.Map<String, FieldTypeMappingDO> defaultMappings;

        protected FieldTypeMappingContext(String databaseType, java.util.Map<String, FieldTypeMappingDO> defaultMappings) {
            this.databaseType = databaseType;
            this.defaultMappings = defaultMappings != null ? defaultMappings : java.util.Map.of();
        }
    }

    /**
     * 生成创建表的DDL语句（保留用于兼容和调试）
     */
    @Deprecated
    protected String generateCreateTableDDL(String tableName, List<MetadataSystemFieldsDO> systemFields) {
        // 保留原有实现，供兼容和调试使用
        java.util.StringJoiner ddl = new java.util.StringJoiner("\n");
        ddl.add("CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (");

        java.util.StringJoiner columns = new java.util.StringJoiner(",\n  ");
        String detectedIdField = null;
        String candidatePk = null;

        for (MetadataSystemFieldsDO f : systemFields) {
            if ("id".equalsIgnoreCase(f.getFieldName())) {
                detectedIdField = f.getFieldName();
                break;
            }
        }

        for (MetadataSystemFieldsDO field : systemFields) {
            String fieldName = field.getFieldName();
            if (fieldName == null || fieldName.trim().isEmpty()) {
                continue;
            }
            boolean isParentId = "parent_id".equalsIgnoreCase(fieldName);
            boolean isId = "id".equalsIgnoreCase(fieldName);

            StringBuilder columnDef = new StringBuilder();
            columnDef.append("\"").append(fieldName).append("\" ");
            columnDef.append(mapFieldType(field.getFieldType()));

            if (isId) {
                columnDef.append(" NOT NULL");
            } else if (!isParentId && BooleanStatusEnum.isYes(field.getIsRequired())) {
                columnDef.append(" NOT NULL");
            }

            if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                columnDef.append(" DEFAULT ").append(field.getDefaultValue());
            }

            if (isId) {
                candidatePk = fieldName;
            } else if (candidatePk == null && !isParentId && BooleanStatusEnum.isYes(field.getIsSnowflakeId())) {
                candidatePk = fieldName;
            }

            columns.add(columnDef.toString());
        }

        if (candidatePk == null) {
            if (detectedIdField == null) {
                columns.add("\"id\" BIGINT NOT NULL");
            }
            candidatePk = "id";
        }

        ddl.add("  " + columns.toString());
        ddl.add(",  PRIMARY KEY (\"" + candidatePk + "\")");
        ddl.add(");");
        ddl.add("COMMENT ON TABLE \"" + tableName + "\" IS '业务实体表';");
        return ddl.toString();
    }

    /**
     * 字段类型映射
     * <p>
     * 从 metadata_field_type_mapping 表查询业务类型对应的数据库类型
     * 支持业务类型（如 ID, USER, DATETIME, NUMBER）到数据库类型（如 BIGINT, TIMESTAMP）的转换
     */
    protected String mapFieldType(String fieldType) {
        if (fieldType == null || fieldType.trim().isEmpty()) {
            log.warn("字段类型为空，使用默认 VARCHAR(255)");
            return "VARCHAR(255)";
        }
        
        // 从映射表查询业务类型对应的数据库类型
        FieldTypeMappingDO mapping = fieldTypeMappingRepository.getDefaultMappingByBusinessType(fieldType);
        return mapFieldType(mapping, fieldType);
    }

    protected String mapFieldType(FieldTypeMappingDO mapping, String fieldType) {
        if (mapping != null && mapping.getDatabaseField() != null) {
            return buildDatabaseTypeString(mapping);
        }
        
        // 如果映射表中没有找到，记录警告并返回默认值
        log.warn("未找到业务类型 {} 的映射配置，使用默认 VARCHAR(255)", fieldType);
        return "VARCHAR(255)";
    }

    /**
     * 根据映射配置构建数据库类型字符串
     * <p>
     * 根据 database_field 的类型决定是否需要加长度或精度参数
     */
    protected String buildDatabaseTypeString(FieldTypeMappingDO mapping) {
        String dbField = mapping.getDatabaseField().toUpperCase();
        Integer defaultLength = mapping.getDefaultLength();
        Integer decimalPlaces = mapping.getDefaultDecimalPlaces();
        
        // 需要加长度参数的类型（如 VARCHAR、CHAR）
        if (LENGTH_REQUIRED_TYPES.contains(dbField)) {
            int length = (defaultLength != null && defaultLength > 0) ? defaultLength : 255;
            return dbField + "(" + length + ")";
        }
        
        // 需要加精度参数的类型（如 NUMERIC、DECIMAL）
        if (PRECISION_REQUIRED_TYPES.contains(dbField)) {
            int len = (defaultLength != null && defaultLength > 0) ? defaultLength : 18;
            int dec = (decimalPlaces != null && decimalPlaces >= 0) ? decimalPlaces : 2;
            return dbField + "(" + len + "," + dec + ")";
        }
        
        // 其他类型直接使用 database_field 的值（如 TEXT, TIMESTAMP, BOOLEAN, DATE, int8, text[] 等）
        return mapping.getDatabaseField();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recreatePhysicalTable(Long entityId) {
        try {
            // 1. 获取业务实体信息
            MetadataBusinessEntityDO entity = getBusinessEntity(entityId);
            if (entity == null) {
                throw new RuntimeException("业务实体不存在，ID: " + entityId);
            }

            // 2. 检查实体类型是否需要创建物理表
            if (!BusinessEntityTypeEnum.needCreatePhysicalTable(entity.getEntityType())) {
                log.info("实体类型为 {}, 不需要创建物理表", entity.getEntityType());
                return;
            }

            // 3. 获取数据源信息
            MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasourceByUuid(entity.getDatasourceUuid());
            if (datasource == null) {
                throw new RuntimeException("未找到数据源UUID为 " + entity.getDatasourceUuid() + " 的数据源配置");
            }

            // 4. 获取系统字段信息
            List<MetadataSystemFieldsDO> systemFields = getSystemFieldsWithCache();

            // 5. 创建物理表
            java.util.Map<String, FieldTypeMappingDO> defaultMappings =
                    fieldTypeMappingRepository.getDefaultMappingsByDatabaseType(datasource.getDatasourceType());
            createPhysicalTable(datasource, entity.getTableName(), systemFields, defaultMappings);

            // 6. 保存实体字段信息到 metadata_entity_field 表（如果还没有保存的话）
            saveEntityFields(entity.getEntityUuid(), systemFields, entity.getApplicationId(), defaultMappings);

            log.info("成功重新创建业务实体 {} 的物理表: {}", entity.getDisplayName(), entity.getTableName());
        } catch (Exception e) {
            log.error("重新创建物理表失败，实体ID: {}, 错误: {}", entityId, e.getMessage(), e);
            throw new RuntimeException("重新创建物理表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从datasource配置中获取数据库名称
     * <p>
     * 仅用于日志记录，不影响核心功能。
     *
     * @param datasource 数据源配置对象
     * @return 数据库名称，获取失败返回"unknown"
     */
    protected String getDatabaseNameFromConfig(MetadataDatasourceDO datasource) {
        try {
            if (datasource == null || datasource.getConfig() == null) {
                return "unknown";
            }
            
            String config = datasource.getConfig();
            
            // 尝试从JSON配置中解析database字段
            // 配置格式示例: {"host":"localhost","port":5432,"database":"mydb",...}
            if (config.contains("\"database\"")) {
                int startIdx = config.indexOf("\"database\"");
                int colonIdx = config.indexOf(":", startIdx);
                int quoteStartIdx = config.indexOf("\"", colonIdx);
                int quoteEndIdx = config.indexOf("\"", quoteStartIdx + 1);
                
                if (quoteStartIdx > 0 && quoteEndIdx > quoteStartIdx) {
                    return config.substring(quoteStartIdx + 1, quoteEndIdx);
                }
            }
            
            return "unknown";
        } catch (Exception e) {
            log.debug("从配置获取数据库名称失败: {}", e.getMessage());
            return "unknown";
        }
    }

}
