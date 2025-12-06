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
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataAppAndDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataSystemFieldsCoreService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.enums.BusinessEntityTypeEnum;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.framework.aynline.AnylineDdlHelper;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_CODE_DUPLICATE;

/**
 * 业务实体 Service 实现类
 */
@Service
@Slf4j
public class MetadataBusinessEntityBuildServiceImpl implements MetadataBusinessEntityBuildService {

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;
    @Resource
    private MetadataEntityRelationshipRepository metadataEntityRelationshipRepository;
    @Resource
    private MetadataDatasourceBuildService metadataDatasourceBuildService;
    @Resource
    private MetadataSystemFieldsCoreService metadataSystemFieldsCoreService;
    @Resource
    private MetadataEntityFieldBuildService metadataEntityFieldBuildService;
    @Resource
    private MetadataEntityRelationshipBuildService metadataEntityRelationshipBuildService;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private MetadataAppAndDatasourceCoreService metadataAppAndDatasourceCoreService;
    @Resource
    private MetadataEntityFieldOptionBuildService fieldOptionService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @Resource
    private AppApplicationApi appApplicationApi;

    // 系统字段缓存，避免频繁查询数据库
    private volatile List<MetadataSystemFieldsDO> systemFieldsCache = null;
    private volatile long lastCacheTime = 0;
    private static final long CACHE_EXPIRE_TIME = 5 * 60 * 1000; // 缓存5分钟

    /**
     * 安全地将字符串转换为Long类型
     * 处理空字符串和null的情况，返回null而不是抛出异常
     *
     * @param str 要转换的字符串
     * @return Long值，如果字符串为空或null则返回null
     */
    private Long safeParseLong(String str) {
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
        if (BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType())) {
            // 同步创建物理表，确保事务原子性
            createPhysicalTableForEntitySync(businessEntity, createReqVO, systemFields);
        }

        // 生成实体UUID（如果为空）
        if (businessEntity.getEntityUuid() == null || businessEntity.getEntityUuid().isEmpty()) {
            businessEntity.setEntityUuid(UuidUtils.getUuid());
        }

        // 插入业务实体到数据库，如果前面创建物理表失败，这里不会执行
        metadataBusinessEntityRepository.save(businessEntity);

        // 如果需要创建物理表，保存系统字段信息到 metadata_entity_field 表
        if (BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType()) && systemFields != null && appId != null) {
            saveEntityFields(businessEntity.getEntityUuid(), systemFields, appId);
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
    private void validateEntityType(Integer entityType) {
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
    private void validateBusinessEntityCodeUniqueWithLock(Long id, String code, Long appId) {
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
    private List<MetadataSystemFieldsDO> getSystemFieldsWithCache() {
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
    private void createPhysicalTableForEntitySync(MetadataBusinessEntityDO businessEntity,
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
            createPhysicalTable(datasource, businessEntity.getTableName(), systemFields);

            // 3. 预先保存实体字段信息到 metadata_entity_field 表（待实体创建成功后会一起提交）
            // 注意：这里不能直接调用saveEntityFields，因为它需要实体ID，而此时实体还未保存
            // 我们将在主方法中处理字段保存

            log.info("成功为业务实体 {} 创建物理表: {}", businessEntity.getDisplayName(), businessEntity.getTableName());
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
    private void handleTableNameByEntityType(MetadataBusinessEntityDO businessEntity, BusinessEntitySaveReqVO createReqVO) {
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
    private List<MetadataSystemFieldsDO> getSystemFields() {
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
    private void saveEntityFields(String entityUuid, List<MetadataSystemFieldsDO> systemFields, Long appId) {
        int sortOrder = 1;
        for (MetadataSystemFieldsDO systemField : systemFields) {
            // 特殊处理 parent_id：不是主键、不是必填、不是唯一
            boolean isParentId = "parent_id".equalsIgnoreCase(systemField.getFieldName());
            int isPrimaryKey = isParentId ? StatusEnumUtil.NO : BooleanStatusEnum.toStatusValue(systemField.getIsSnowflakeId());
            int isRequired = isParentId ? StatusEnumUtil.NO : BooleanStatusEnum.toStatusValue(systemField.getIsRequired());
            int isUnique = isParentId ? StatusEnumUtil.NO : BooleanStatusEnum.toStatusValue(systemField.getIsSnowflakeId());
            
            MetadataEntityFieldDO entityField = new MetadataEntityFieldDO();
            entityField.setEntityUuid(entityUuid);
            entityField.setFieldName(systemField.getFieldName());
            // 优先使用系统字段的显示名称，为空则回退为字段名
            entityField.setDisplayName(CharSequenceUtil.isNotEmpty(systemField.getDisplayName())
                ? systemField.getDisplayName()
                : systemField.getFieldName());
            entityField.setFieldType(systemField.getFieldType());
            entityField.setDataLength(getDefaultDataLength(systemField.getFieldType())); // 根据字段类型设置默认长度
            entityField.setDecimalPlaces(getDefaultDecimalPlaces(systemField.getFieldType())); // 根据字段类型设置默认小数位
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
            entityField.setStatus(0); // 默认开启
            entityField.setFieldCode(generateFieldCode(systemField.getFieldName())); // 生成字段编码

            metadataEntityFieldBuildService.createEntityFieldInternal(entityField);
        }

        log.info("成功保存 {} 个系统字段到实体字段表", systemFields.size());
    }

    /**
     * 根据字段类型获取默认数据长度
     */
    private Integer getDefaultDataLength(String fieldType) {
        switch (fieldType.toUpperCase()) {
            case "VARCHAR":
                return 255;
            case "BIGINT":
                return 19;
            case "INTEGER":
                return 10;
            case "DECIMAL":
                return 18;
            default:
                return null;
        }
    }

    /**
     * 根据字段类型获取默认小数位数
     */
    private Integer getDefaultDecimalPlaces(String fieldType) {
        switch (fieldType.toUpperCase()) {
            case "DECIMAL":
                return 2;
            default:
                return null;
        }
    }

    /**
     * 生成字段编码
     * 将字段名转换为大写，下划线保持不变
     *
     * @param fieldName 字段名
     * @return 字段编码
     */
    private String generateFieldCode(String fieldName) {
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
    private void createPhysicalTable(MetadataDatasourceDO datasource, String tableName, List<MetadataSystemFieldsDO> systemFields) {
        int maxRetries = 3;
        Exception lastException = null;
        
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
                Table<?> table = buildTableFromSystemFields(tableName, systemFields);

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
    private Table<?> buildTableFromSystemFields(String tableName, List<MetadataSystemFieldsDO> systemFields) {
        Table<?> table = new Table<>(tableName);
        table.setComment("业务实体表");

        String detectedIdField = null;
        String candidatePk = null;

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
            column.setTypeName(mapFieldType(field.getFieldType()));

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

    /**
     * 生成创建表的DDL语句（保留用于兼容和调试）
     */
    @Deprecated
    private String generateCreateTableDDL(String tableName, List<MetadataSystemFieldsDO> systemFields) {
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
     */
    private String mapFieldType(String fieldType) {
        switch (fieldType.toUpperCase()) {
            case "BIGINT":
                return "BIGINT";
            case "VARCHAR":
                return "VARCHAR(255)";
            case "TEXT":
            case "LONGVARCHAR":
                // LONGVARCHAR 类型映射为 TEXT，用于存储较长的文本数据
                // 包括：单选列表、多选列表、结构化对象、数组列表、文件、图片、地理位置、用户多选、部门多选、数据多选等
                return "TEXT";
            case "TIMESTAMP":
                return "TIMESTAMP";
            case "BOOLEAN":
                return "BOOLEAN";
            case "INTEGER":
                return "INTEGER";
            case "DECIMAL":
                return "DECIMAL(18,2)";
            default:
                return "VARCHAR(255)"; // 默认类型
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBusinessEntity(@Valid BusinessEntitySaveReqVO updateReqVO) {
        // ID转UUID兼容处理：支持前端传入datasourceId或datasourceUuid
        String resolvedDatasourceUuid = idUuidConverter.resolveDatasourceUuidOptional(
                updateReqVO.getDatasourceUuid(), updateReqVO.getDatasourceId());
        updateReqVO.setDatasourceUuid(resolvedDatasourceUuid);

        // 安全转换 ID 和 appId
        Long id = safeParseLong(updateReqVO.getId());
        Long appId = safeParseLong(updateReqVO.getApplicationId());
        String datasourceUuid = updateReqVO.getDatasourceUuid();
        
        // 校验存在
        if (id != null) {
            validateBusinessEntityExists(id);
        }
        
        // 校验编码唯一性（只有当code不为空时才校验）
        if (CharSequenceUtil.isNotEmpty(updateReqVO.getCode()) && id != null && appId != null) {
            validateBusinessEntityCodeUnique(id, updateReqVO.getCode(), appId);
        }
        
        // 校验实体类型
        validateEntityType(updateReqVO.getEntityType());

        // 更新业务实体
        MetadataBusinessEntityDO updateObj = BeanUtils.toBean(updateReqVO, MetadataBusinessEntityDO.class);
        updateObj.setId(id);
        updateObj.setApplicationId(appId);
        updateObj.setDatasourceUuid(datasourceUuid);

        // 处理code字段：如果为空或空字符串，则生成UUID
        if (CharSequenceUtil.isEmpty(updateReqVO.getCode())) {
            updateObj.setCode(IdUtil.simpleUUID());
        }

        // 根据实体类型处理表名
        handleTableNameByEntityType(updateObj, updateReqVO);

        metadataBusinessEntityRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessEntity(Long id) {
        // 校验存在
        validateBusinessEntityExists(id);
        
        // 先获取实体信息以获取UUID
        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.getBusinessEntityById(id);
        String entityUuid = entity != null ? entity.getEntityUuid() : null;

        // 删除业务实体
        metadataBusinessEntityRepository.removeById(id);

        // 删除实体关联关系
        if (entityUuid != null) {
            QueryWrapper relationshipQueryWrapper = QueryWrapper.create()
                    .eq(MetadataEntityRelationshipDO::getSourceEntityUuid, entityUuid)
                    .or(MetadataEntityRelationshipDO::getTargetEntityUuid).eq(entityUuid);
            List<MetadataEntityRelationshipDO> relationshipDOs = metadataEntityRelationshipBuildService.findAllByConfig(relationshipQueryWrapper);
            for(MetadataEntityRelationshipDO relationshipDO : relationshipDOs){
                metadataEntityRelationshipBuildService.deleteEntityRelationship(relationshipDO.getId());
            }
        }
    }

    private void validateBusinessEntityExists(Long id) {
        if (!metadataBusinessEntityRepository.existsBusinessEntity(id)) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
    }

    private void validateBusinessEntityCodeUnique(Long id, String code, Long appId) {
        if (!metadataBusinessEntityRepository.isBusinessEntityCodeUnique(id, code, appId)) {
            throw exception(BUSINESS_ENTITY_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntity(Long id) {
        return metadataBusinessEntityRepository.getBusinessEntityById(id);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByUuid(String entityUuid) {
        return metadataBusinessEntityRepository.getByEntityUuid(entityUuid);
    }

    @Override
    public PageResult<MetadataBusinessEntityDO> getBusinessEntityPage(BusinessEntityPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        // 默认不显示中间表（entity_type = 3）
        queryWrapper.ne(MetadataBusinessEntityDO::getEntityType, BusinessEntityTypeEnum.MIDDLE_TABLE.getCode());

        // 添加查询条件
        if (pageReqVO.getDisplayName() != null) {
            queryWrapper.like(MetadataBusinessEntityDO::getDisplayName, pageReqVO.getDisplayName());
        }
        if (pageReqVO.getCode() != null) {
            queryWrapper.like(MetadataBusinessEntityDO::getCode, pageReqVO.getCode());
        }
        if (pageReqVO.getEntityType() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getEntityType, pageReqVO.getEntityType());
        }
        if (pageReqVO.getDatasourceId() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getDatasourceUuid, pageReqVO.getDatasourceId());
        }
        if (pageReqVO.getVersionTag() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getVersionTag, pageReqVO.getVersionTag());
        }
        if (pageReqVO.getApplicationId() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getApplicationId, pageReqVO.getApplicationId());
        }
        if (pageReqVO.getStatus() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getStatus, pageReqVO.getStatus());
        }

        // 分页查询
        queryWrapper.orderBy(MetadataBusinessEntityDO::getCreateTime, false);

        return metadataBusinessEntityRepository.getBusinessEntityPage(queryWrapper,
            pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        return metadataBusinessEntityRepository.getBusinessEntityList();
    }

    @Override
    public List<MetadataBusinessEntityDO> findAllByConfig(QueryWrapper queryWrapper) {
        return metadataBusinessEntityRepository.list(queryWrapper);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        return metadataBusinessEntityRepository.getBusinessEntityByCode(code);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceUuid(String datasourceUuid) {
        return metadataBusinessEntityRepository.getBusinessEntityListByDatasourceUuid(datasourceUuid);
    }

    @Override
    public ERDiagramRespVO getERDiagramByDatasourceUuid(String datasourceUuid) {
        // 1. 获取数据源信息
        MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasourceByUuid(datasourceUuid);
        if (datasource == null) {
            throw new IllegalArgumentException("数据源不存在，UUID: " + datasourceUuid);
        }

        // 2. 获取该数据源下的所有业务实体
        List<MetadataBusinessEntityDO> entities = getBusinessEntityListByDatasourceUuid(datasourceUuid);

        // 3. 构建ER图响应对象
        ERDiagramRespVO result = new ERDiagramRespVO();
        result.setDatasourceId(datasourceUuid);
        result.setDatasourceName(datasource.getDatasourceName());

        // 4. 转换实体信息，包括字段信息
        List<EREntityVO> erEntities = new ArrayList<>();
        for (MetadataBusinessEntityDO entity : entities) {
            EREntityVO erEntity = convertToEREntity(entity);
            erEntities.add(erEntity);
        }
        result.setEntities(erEntities);

        // 5. 构建关联关系（基于外键关系）
        List<ERRelationshipVO> relationships = buildRelationships(entities);

        result.setRelationships(relationships);

        //设主子关系
        Set<String> sourceIds = relationships.stream()
                .map(ERRelationshipVO::getSourceEntityId)
                .collect(Collectors.toSet());
        Set<String> targetIds = relationships.stream()
                .map(ERRelationshipVO::getTargetEntityId)
                .collect(Collectors.toSet());

        for (EREntityVO entity : erEntities) {
            if (sourceIds.contains(entity.getEntityId())) {
                entity.setRelationType("PARENT");
            }
            if (targetIds.contains(entity.getEntityId())) {
                entity.setRelationType("CHILD");
            }
        }
        return result;
    }

    /**
     * 将业务实体转换为ER实体VO
     *
     * @param entity 业务实体DO
     * @return ER实体VO
     */
    private EREntityVO convertToEREntity(MetadataBusinessEntityDO entity) {
        return BeanUtils.toBean(entity, EREntityVO.class, erEntity -> {
            erEntity.setEntityId(entity.getId().toString());
            erEntity.setEntityUuid(entity.getEntityUuid());
            erEntity.setEntityName(entity.getDisplayName());
            erEntity.setTableName(entity.getTableName());
            erEntity.setDescription(entity.getDescription());
            erEntity.setEntityType(entity.getEntityType().toString());
            erEntity.setStatus(entity.getStatus());

            // 设置默认坐标（前端可以根据需要调整）
            erEntity.setDisplayConfig(entity.getDisplayConfig() != null ? entity.getDisplayConfig() : "{}");
            erEntity.setCode(entity.getCode());

            // 获取字段信息
            List<ERFieldVO> fields = getEntityFields(entity.getId());
            erEntity.setFields(fields);
        });
    }

    /**
     * 获取实体的字段信息
     *
     * @param entityId 实体ID
     * @return 字段VO列表
     */
    private List<ERFieldVO> getEntityFields(Long entityId) {
        List<MetadataEntityFieldDO> fieldList = metadataEntityFieldBuildService.getEntityFieldListByEntityId(String.valueOf(entityId));
        List<ERFieldVO> erFields = new ArrayList<>();

        for (MetadataEntityFieldDO field : fieldList) {
            ERFieldVO erField = BeanUtils.toBean(field, ERFieldVO.class, result -> {
                // 手动设置 fieldId，因为数据库实体中是 id，而 VO 中是 fieldId
                result.setFieldId(field.getId());
            });
            
            // 填充选项信息（单选、多选字段）
            if ("SELECT".equalsIgnoreCase(field.getFieldType()) ||
                "SINGLE_SELECT".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_SELECT".equalsIgnoreCase(field.getFieldType()) ||
                "PICKLIST".equalsIgnoreCase(field.getFieldType()) ||
                "DATA_SELECTION".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_USER".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_DEPARTMENT".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_DATA_SELECTION".equalsIgnoreCase(field.getFieldType())) {
                List<MetadataEntityFieldOptionDO> options = fieldOptionService.listByFieldId(field.getFieldUuid());
                if (options != null && !options.isEmpty()) {
                    List<FieldOptionRespVO> optionVOs = options.stream().map(o -> {
                        FieldOptionRespVO item = new FieldOptionRespVO();
                        item.setId(o.getId() != null ? String.valueOf(o.getId()) : null);
                        item.setFieldUuid(o.getFieldUuid());
                        item.setOptionLabel(o.getOptionLabel());
                        item.setOptionValue(o.getOptionValue());
                        item.setOptionOrder(o.getOptionOrder());
                        item.setIsEnabled(o.getIsEnabled());
                        item.setDescription(o.getDescription());
                        return item;
                    }).toList();
                    erField.setOptions(optionVOs);
                }
            }
            
            erFields.add(erField);
        }

        return erFields;
    }

    /**
     * 构建实体间的关联关系
     * 基于实际存储的关系数据
     *
     * @param entities 业务实体列表
     * @return 关联关系列表
     */
    private List<ERRelationshipVO> buildRelationships(List<MetadataBusinessEntityDO> entities) {
        List<ERRelationshipVO> relationships = new ArrayList<>();

        if (entities.isEmpty()) {
            return relationships;
        }

        // 获取该数据源下的所有实体关系
        List<String> entityUuids = entities.stream()
                .map(MetadataBusinessEntityDO::getEntityUuid)
                .toList();

        QueryWrapper relationshipQueryWrapper = QueryWrapper.create()
                .in(MetadataEntityRelationshipDO::getSourceEntityUuid, entityUuids)
                .or(MetadataEntityRelationshipDO::getTargetEntityUuid).in(entityUuids)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);

        List<MetadataEntityRelationshipDO> relationshipDOs = metadataEntityRelationshipBuildService.findAllByConfig(
                relationshipQueryWrapper);

        // 转换为ER关系VO
        for (MetadataEntityRelationshipDO relationshipDO : relationshipDOs) {
            ERRelationshipVO relationship = convertToERRelationship(relationshipDO, entities);
            if (relationship != null) {
                relationships.add(relationship);
            }
        }

        return relationships;
    }

    /**
     * 根据字段ID获取字段名称
     *
     * @param fieldId 字段ID
     * @return 字段名称
     */
    private String getFieldNameById(Long fieldId) {
        if (fieldId == null) {
            return null;
        }

        try {
            MetadataEntityFieldDO field = metadataEntityFieldBuildService.getEntityField(String.valueOf(fieldId));
            return field != null ? field.getFieldName() : null;
        } catch (Exception e) {
            log.warn("获取字段名称失败，字段ID: {}, 错误: {}", fieldId, e.getMessage());
            return null;
        }
    }

    /**
     * 根据字段UUID获取字段名称
     *
     * @param fieldUuid 字段UUID
     * @return 字段名称
     */
    private String getFieldNameByUuid(String fieldUuid) {
        if (fieldUuid == null || fieldUuid.isEmpty()) {
            return null;
        }

        try {
            MetadataEntityFieldDO field = metadataEntityFieldBuildService.getEntityFieldByUuid(fieldUuid);
            return field != null ? field.getFieldName() : null;
        } catch (Exception e) {
            log.warn("获取字段名称失败，字段UUID: {}, 错误: {}", fieldUuid, e.getMessage());
            return null;
        }
    }

    /**
     * 将实体关系DO转换为ER关系VO
     *
     * @param relationshipDO 实体关系DO
     * @param entities 实体列表，用于获取实体名称
     * @return ER关系VO
     */
    private ERRelationshipVO convertToERRelationship(MetadataEntityRelationshipDO relationshipDO,
                                                    List<MetadataBusinessEntityDO> entities) {
        // 查找源实体和目标实体（使用UUID匹配）
        MetadataBusinessEntityDO sourceEntity = entities.stream()
                .filter(entity -> entity.getEntityUuid().equals(relationshipDO.getSourceEntityUuid()))
                .findFirst()
                .orElse(null);

        MetadataBusinessEntityDO targetEntity = entities.stream()
                .filter(entity -> entity.getEntityUuid().equals(relationshipDO.getTargetEntityUuid()))
                .findFirst()
                .orElse(null);

        if (sourceEntity == null || targetEntity == null) {
            return null;
        }

        ERRelationshipVO relationship = BeanUtils.toBean(relationshipDO, ERRelationshipVO.class, rel -> {
            rel.setRelationshipId(relationshipDO.getId().toString());
            // 源实体：同时设置ID和UUID
            rel.setSourceEntityId(sourceEntity.getId().toString());
            rel.setSourceEntityUuid(relationshipDO.getSourceEntityUuid());
            rel.setSourceEntityName(sourceEntity.getDisplayName());
            // 源字段：同时设置ID和UUID
            rel.setSourceFieldUuid(relationshipDO.getSourceFieldUuid());
            Long sourceFieldId = idUuidConverter.resolveFieldId(relationshipDO.getSourceFieldUuid());
            rel.setSourceFieldId(sourceFieldId != null ? sourceFieldId.toString() : null);
            rel.setSourceFieldName(getFieldNameByUuid(relationshipDO.getSourceFieldUuid()));
            // 目标实体：同时设置ID和UUID
            rel.setTargetEntityId(targetEntity.getId().toString());
            rel.setTargetEntityUuid(relationshipDO.getTargetEntityUuid());
            rel.setTargetEntityName(targetEntity.getDisplayName());
            // 目标字段：同时设置ID和UUID
            rel.setTargetFieldUuid(relationshipDO.getTargetFieldUuid());
            Long targetFieldId = idUuidConverter.resolveFieldId(relationshipDO.getTargetFieldUuid());
            rel.setTargetFieldId(targetFieldId != null ? targetFieldId.toString() : null);
            rel.setTargetFieldName(getFieldNameByUuid(relationshipDO.getTargetFieldUuid()));
        });

        return relationship;
    }



    @Override
    public List<SimpleEntityRespVO> getSimpleEntityListByAppId(Long appId) {
        log.info("开始查询应用实体列表，应用ID: {}", appId);

        // 1. 根据appId查询该应用下的所有实体
        List<MetadataBusinessEntityDO> entities = metadataBusinessEntityRepository.getSimpleEntityListByAppId(appId);

        if (entities.isEmpty()) {
            log.info("应用下未找到任何实体，应用ID: {}", appId);
            return List.of();
        }

        // 2. 转换为简单实体信息VO
        List<SimpleEntityRespVO> result = entities.stream()
                .map(this::convertToSimpleEntity)
                .toList();

        // 3. 查询应用下的所有关系，判断主子表类型
        List<MetadataEntityRelationshipDO> relationships = metadataEntityRelationshipRepository.findByApplicationId(appId);
        
        // 提取所有作为源实体的UUID集合
        Set<String> sourceEntityUuids = relationships.stream()
                .map(MetadataEntityRelationshipDO::getSourceEntityUuid)
                .collect(Collectors.toSet());
        
        // 提取所有作为目标实体的UUID集合
        Set<String> targetEntityUuids = relationships.stream()
                .map(MetadataEntityRelationshipDO::getTargetEntityUuid)
                .collect(Collectors.toSet());

        // 遍历实体列表，设置关系类型
        for (SimpleEntityRespVO entity : result) {
            String uuid = entity.getEntityUuid();
            boolean isSource = sourceEntityUuids.contains(uuid);
            boolean isTarget = targetEntityUuids.contains(uuid);

            if (isSource && !isTarget) {
                // 只在source出现且未在target出现 -> 主表
                entity.setRelationType("MASTER");
            } else if (isTarget) {
                // 只要在target出现 -> 子表
                entity.setRelationType("SLAVE");
            } else {
                // 都没有 -> 无关系
                entity.setRelationType("NONE");
            }
        }

        log.info("查询应用实体列表完成，应用ID: {}, 实体数量: {}", appId, result.size());
        return result;
    }

    /**
     * 转换实体DO为简单实体信息VO
     *
     * @param entityDO 实体DO
     * @return 简单实体信息VO
     */
    private SimpleEntityRespVO convertToSimpleEntity(MetadataBusinessEntityDO entityDO) {
        return BeanUtils.toBean(entityDO, SimpleEntityRespVO.class, simpleEntity -> {
            simpleEntity.setEntityId(entityDO.getId());
            simpleEntity.setEntityUuid(entityDO.getEntityUuid());
            simpleEntity.setEntityName(entityDO.getDisplayName());
            // 设置实际表名
            simpleEntity.setTableName(entityDO.getTableName());
        });
    }

    @Override
    public BusinessEntityRespVO createBusinessEntityWithResponse(@Valid BusinessEntitySaveReqVO reqVO) {
        // 修改企业主表更新时间
        Long appId = Long.valueOf(reqVO.getApplicationId());
        appApplicationApi.updateAppTimeById(appId);

        Long id = createBusinessEntity(reqVO);
        MetadataBusinessEntityDO businessEntity = getBusinessEntity(id);
        return modelMapper.map(businessEntity, BusinessEntityRespVO.class);
    }

    @Override
    public BusinessEntityRespVO getBusinessEntityDetail(Long id) {
        MetadataBusinessEntityDO businessEntity = getBusinessEntity(id);
        return modelMapper.map(businessEntity, BusinessEntityRespVO.class);
    }

    @Override
    public PageResult<BusinessEntityRespVO> getBusinessEntityPageWithResponse(BusinessEntityPageReqVO pageReqVO) {
        PageResult<MetadataBusinessEntityDO> pageResult = getBusinessEntityPage(pageReqVO);
        PageResult<BusinessEntityRespVO> convertedResult = new PageResult<>();
        convertedResult.setTotal(pageResult.getTotal());
        convertedResult.setList(pageResult.getList().stream()
                .map(entity -> modelMapper.map(entity, BusinessEntityRespVO.class))
                .toList());
        return convertedResult;
    }

    @Override
    public List<BusinessEntityRespVO> getBusinessEntityListByDatasourceUuidWithRelationType(String datasourceUuid) {
        // 1. 获取业务实体列表
        List<MetadataBusinessEntityDO> list = getBusinessEntityListByDatasourceUuid(datasourceUuid);

        // 2. 转换为 VO
        List<BusinessEntityRespVO> result = list.stream()
                .map(entity -> modelMapper.map(entity, BusinessEntityRespVO.class))
                .toList();

        // 3. 填充 relationType 字段
        // relationType 用于标识实体在关系中的角色：PARENT(主表/父表) 或 CHILD(子表)
        if (!result.isEmpty()) {
            // 复用 ER 图服务获取关系信息，保持逻辑一致性
            ERDiagramRespVO erDiagram = getERDiagramByDatasourceUuid(datasourceUuid);
            List<ERRelationshipVO> relationships = erDiagram.getRelationships();

            // 收集所有作为源实体(主表)和目标实体(子表)的ID
            Set<String> sourceIds = relationships.stream()
                    .map(ERRelationshipVO::getSourceEntityId)
                    .collect(Collectors.toSet());
            Set<String> targetIds = relationships.stream()
                    .map(ERRelationshipVO::getTargetEntityId)
                    .collect(Collectors.toSet());

            // 为每个实体设置关系类型
            for (BusinessEntityRespVO entity : result) {
                if (sourceIds.contains(entity.getId())) {
                    entity.setRelationType("PARENT");  // 主表：其他表引用此表
                }
                if (targetIds.contains(entity.getId())) {
                    entity.setRelationType("CHILD");   // 子表：引用其他表的外键
                }
                // 注意：一个实体可能既是某些关系的主表，又是其他关系的子表
                // 在这种情况下，最后设置的值会覆盖前面的值
                // 如果既不是源实体也不是目标实体，relationType 保持 null
            }
        }

        return result;
    }

    /**
     * 重新创建业务实体的物理表
     * 当发现表不存在时，可以调用此方法来重新创建表
     *
     * @param entityId 业务实体ID
     */
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
            createPhysicalTable(datasource, entity.getTableName(), systemFields);

            // 6. 保存实体字段信息到 metadata_entity_field 表（如果还没有保存的话）
            saveEntityFields(entity.getEntityUuid(), systemFields, entity.getApplicationId());

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
    private String getDatabaseNameFromConfig(MetadataDatasourceDO datasource) {
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
