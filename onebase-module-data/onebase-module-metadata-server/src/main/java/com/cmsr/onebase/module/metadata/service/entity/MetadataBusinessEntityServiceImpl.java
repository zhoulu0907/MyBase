package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.ERDiagramRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EREntityVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.ERFieldVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.ERRelationshipVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataDatasourceRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataSystemFieldsRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.enums.BusinessEntityTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.BUSINESS_ENTITY_CODE_DUPLICATE;

/**
 * 业务实体 Service 实现类
 */
@Service
@Slf4j
public class MetadataBusinessEntityServiceImpl implements MetadataBusinessEntityService {

    @Resource
    private DatasourceConvert datasourceConvert;
    @Resource
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;
    @Resource
    private MetadataDatasourceRepository metadataDatasourceRepository;
    @Resource
    private MetadataSystemFieldsRepository metadataSystemFieldsRepository;
    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    private MetadataEntityRelationshipRepository metadataEntityRelationshipRepository;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBusinessEntity(@Valid BusinessEntitySaveReqVO createReqVO) {
        // 校验编码唯一性
        validateBusinessEntityCodeUnique(null, createReqVO.getCode(), Long.valueOf(createReqVO.getAppId()));

        // 校验实体类型
        validateEntityType(createReqVO.getEntityType());

        // 插入业务实体
        MetadataBusinessEntityDO businessEntity = BeanUtils.toBean(createReqVO, MetadataBusinessEntityDO.class);
        businessEntity.setAppId(Long.valueOf(createReqVO.getAppId()));

        // 根据实体类型处理表名
        handleTableNameByEntityType(businessEntity, createReqVO);

        metadataBusinessEntityRepository.insert(businessEntity);

        // 根据实体类型决定是否创建物理表
        if (BusinessEntityTypeEnum.needCreatePhysicalTable(createReqVO.getEntityType())) {
            createPhysicalTableForEntity(businessEntity, createReqVO);
        } else {
            BusinessEntityTypeEnum entityTypeEnum = BusinessEntityTypeEnum.getByCode(createReqVO.getEntityType());
            String typeName = entityTypeEnum != null ? entityTypeEnum.getName() : "未知类型";
            log.info("实体类型为 {} ({}), 跳过物理表创建", createReqVO.getEntityType(), typeName);
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
     * 为实体创建物理表
     *
     * @param businessEntity 业务实体
     * @param createReqVO 创建请求VO
     */
    private void createPhysicalTableForEntity(MetadataBusinessEntityDO businessEntity, BusinessEntitySaveReqVO createReqVO) {
        try {
            // 1. 通过数据源 id 获取对应的数据源信息
            MetadataDatasourceDO datasource = getDatasourceById(createReqVO.getDatasourceId());
            if (datasource != null) {
                // 2. 获取系统字段信息
                List<MetadataSystemFieldsDO> systemFields = getSystemFields();

                // 3. 生成 DDL 并在数据源内建物理表
                createPhysicalTable(datasource, businessEntity.getTableName(), systemFields);

                // 4. 保存实体字段信息到 metadata_entity_field 表
                saveEntityFields(businessEntity.getId(), systemFields, Long.valueOf(createReqVO.getAppId()));

                log.info("成功为业务实体 {} 创建物理表: {}", businessEntity.getDisplayName(), businessEntity.getTableName());
            } else {
                log.warn("未找到数据源ID为 {} 的数据源配置，跳过物理表创建", createReqVO.getDatasourceId());
            }
        } catch (Exception e) {
            log.error("创建业务实体物理表失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响业务实体的创建
        }
    }

    /**
     * 获取数据源信息
     */
    private MetadataDatasourceDO getDatasourceById(String datasourceId) {
        if (datasourceId == null || datasourceId.trim().isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", Long.valueOf(datasourceId));
        return metadataDatasourceRepository.findOne(configStore);
    }

    /**
     * 获取系统字段信息
     */
    private List<MetadataSystemFieldsDO> getSystemFields() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_enabled", CommonStatusEnum.ENABLE.getStatus()); // 只获取启用的系统字段（0-启用，1-禁用）
        configStore.order("id", Order.TYPE.ASC);
        // 直接使用配置的查询条件，不再调用仓储类的方法（避免重复条件）
        List<MetadataSystemFieldsDO> systemFields = metadataSystemFieldsRepository.findAllByConfig(configStore);
        
        log.info("获取系统字段结果: 总数={}, is_enabled条件={}", 
                systemFields.size(), CommonStatusEnum.ENABLE.getStatus());
        
        // 如果启用的系统字段为空，尝试获取所有系统字段（忽略启用状态）
        if (systemFields.isEmpty()) {
            log.warn("未找到启用的系统字段，尝试获取所有系统字段");
            DefaultConfigStore allConfigStore = new DefaultConfigStore();
            allConfigStore.order("id", Order.TYPE.ASC);
            systemFields = metadataSystemFieldsRepository.findAllByConfig(allConfigStore);
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
     * @param entityId 业务实体ID
     * @param systemFields 系统字段列表
     * @param appId 应用ID
     */
    private void saveEntityFields(Long entityId, List<MetadataSystemFieldsDO> systemFields, Long appId) {
        int sortOrder = 1;
        for (MetadataSystemFieldsDO systemField : systemFields) {
            MetadataEntityFieldDO entityField = MetadataEntityFieldDO.builder()
                    .entityId(entityId)
                    .fieldName(systemField.getFieldName())
                    .displayName(systemField.getFieldName()) // 使用字段名作为显示名称
                    .fieldType(systemField.getFieldType())
                    .dataLength(getDefaultDataLength(systemField.getFieldType())) // 根据字段类型设置默认长度
                    .decimalPlaces(getDefaultDecimalPlaces(systemField.getFieldType())) // 根据字段类型设置默认小数位
                    .defaultValue(systemField.getDefaultValue())
                    .description(systemField.getDescription())
                    .isSystemField(0) // 标记为系统字段：0-是
                    .isPrimaryKey(systemField.getIsSnowflakeId() == 1 ? 0 : 1) // 雪花ID字段设为主键：0-是，1-不是
                    .isRequired(systemField.getIsRequired() == 1 ? 0 : 1) // 0-是，1-不是
                    .isUnique(systemField.getIsSnowflakeId() == 1 ? 0 : 1) // 主键字段唯一：0-是，1-不是
                    .allowNull(systemField.getIsRequired() != 1 ? 0 : 1) // 必填字段不允许为空：0-是，1-不是
                    .sortOrder(sortOrder++)
                    .validationRules(null) // 系统字段暂不设置校验规则
                    .runMode(0) // 默认编辑态
                    .appId(appId)
                    .status(0) // 默认开启
                    .fieldCode(generateFieldCode(systemField.getFieldName())) // 生成字段编码
                    .build();

            metadataEntityFieldRepository.insert(entityField);
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
     */
    private void createPhysicalTable(MetadataDatasourceDO datasource, String tableName, List<MetadataSystemFieldsDO> systemFields) {
        try {
            log.info("=== 开始创建物理表调试信息 ===");
            log.info("目标表名: {}", tableName);
            log.info("数据源配置: {}", datasource.getConfig());
            log.info("数据源类型: {}", datasource.getDatasourceType());
            
            // 创建 AnylineService 实例 - 使用新的TemporaryDatasourceService
            AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

            // 生成建表 DDL
            String createTableDDL = generateCreateTableDDL(tableName, systemFields);
            log.info("生成的DDL语句: \n{}", createTableDDL);

            // 验证服务连接的数据库
            try {
                String currentDatabase = service.query("SELECT current_database()").toString();
                log.info("当前连接的数据库: {}", currentDatabase);
            } catch (Exception e) {
                log.warn("无法获取当前数据库信息: {}", e.getMessage());
            }

            // 执行建表语句
            service.execute(createTableDDL);

            log.info("=== 物理表创建完成 ===");
            log.info("成功创建物理表: {}", tableName);
        } catch (Exception e) {
            log.error("创建物理表失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建物理表失败", e);
        }
    }

    /**
     * 生成创建表的DDL语句
     */
    private String generateCreateTableDDL(String tableName, List<MetadataSystemFieldsDO> systemFields) {
        StringJoiner ddl = new StringJoiner("\n");
        ddl.add("CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (");

        StringJoiner columns = new StringJoiner(",\n  ");
        String primaryKeyField = null;

        for (MetadataSystemFieldsDO field : systemFields) {
            StringBuilder columnDef = new StringBuilder();
            columnDef.append("\"").append(field.getFieldName()).append("\" ");

            // 字段类型映射
            String columnType = mapFieldType(field.getFieldType());
            columnDef.append(columnType);

            // 是否必填：0-是，1-不是
            if (field.getIsRequired() == 0) {
                columnDef.append(" NOT NULL");
            }

            // 默认值
            if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                columnDef.append(" DEFAULT ").append(field.getDefaultValue());
            }

            // 雪花ID字段设置为主键
            if (field.getIsSnowflakeId() == 1) {
                primaryKeyField = field.getFieldName();
            }

            columns.add(columnDef.toString());
        }

        ddl.add("  " + columns.toString());

        // 添加主键约束
        if (primaryKeyField != null) {
            ddl.add(",  PRIMARY KEY (\"" + primaryKeyField + "\")");
        }

        ddl.add(");");

        // 添加表注释
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
        // 校验存在
        validateBusinessEntityExists(Long.valueOf(updateReqVO.getId()));
        // 校验编码唯一性
        validateBusinessEntityCodeUnique(Long.valueOf(updateReqVO.getId()), updateReqVO.getCode(), Long.valueOf(updateReqVO.getAppId()));
        // 校验实体类型
        validateEntityType(updateReqVO.getEntityType());

        // 更新业务实体
        MetadataBusinessEntityDO updateObj = BeanUtils.toBean(updateReqVO, MetadataBusinessEntityDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));

        // 根据实体类型处理表名
        handleTableNameByEntityType(updateObj, updateReqVO);

        metadataBusinessEntityRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessEntity(Long id) {
        // 校验存在
        validateBusinessEntityExists(id);

        // 删除业务实体
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        metadataBusinessEntityRepository.deleteByConfig(configStore);
    }

    private void validateBusinessEntityExists(Long id) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        if (metadataBusinessEntityRepository.findOne(configStore) == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
    }

    private void validateBusinessEntityCodeUnique(Long id, String code, Long appId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        configStore.and("app_id", appId);
        if (id != null) {
            configStore.and(Compare.NOT_EQUAL, "id", id);
        }

        long count = metadataBusinessEntityRepository.countByConfig(configStore);
        if (count > 0) {
            throw exception(BUSINESS_ENTITY_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntity(Long id) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        return metadataBusinessEntityRepository.findOne(configStore);
    }

    @Override
    public PageResult<MetadataBusinessEntityDO> getBusinessEntityPage(BusinessEntityPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 默认不显示中间表（entity_type = 3）
        configStore.and(Compare.NOT_EQUAL, "entity_type", BusinessEntityTypeEnum.MIDDLE_TABLE.getCode());

        // 添加查询条件
        if (pageReqVO.getDisplayName() != null) {
            configStore.and(Compare.LIKE, "display_name", "%" + pageReqVO.getDisplayName() + "%");
        }
        if (pageReqVO.getCode() != null) {
            configStore.and(Compare.LIKE, "code", "%" + pageReqVO.getCode() + "%");
        }
        if (pageReqVO.getEntityType() != null) {
            configStore.and("entity_type", pageReqVO.getEntityType());
        }
        if (pageReqVO.getDatasourceId() != null) {
            configStore.and("datasource_id", pageReqVO.getDatasourceId());
        }
        if (pageReqVO.getRunMode() != null) {
            configStore.and("run_mode", pageReqVO.getRunMode());
        }
        if (pageReqVO.getAppId() != null) {
            configStore.and("app_id", pageReqVO.getAppId());
        }

        // 分页查询
        configStore.order("create_time", Order.TYPE.DESC);

        return metadataBusinessEntityRepository.findPageWithConditions(configStore,
            pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataBusinessEntityRepository.findAllByConfig(configStore);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        return metadataBusinessEntityRepository.findOne(configStore);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceId(Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("datasource_id", datasourceId);
        configStore.order("create_time", Order.TYPE.DESC);
        List<MetadataBusinessEntityDO> a = metadataBusinessEntityRepository.findAllByConfig(configStore);
        return a;
    }

    @Override
    public ERDiagramRespVO getERDiagramByDatasourceId(Long datasourceId) {
        // 1. 获取数据源信息
        MetadataDatasourceDO datasource = getDatasourceById(datasourceId);
        if (datasource == null) {
            throw new IllegalArgumentException("数据源不存在，ID: " + datasourceId);
        }

        // 2. 获取该数据源下的所有业务实体
        List<MetadataBusinessEntityDO> entities = getBusinessEntityListByDatasourceId(datasourceId);

        // 3. 构建ER图响应对象
        ERDiagramRespVO result = new ERDiagramRespVO();
        result.setDatasourceId(datasourceId);
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
        EREntityVO erEntity = new EREntityVO();
        erEntity.setEntityId(entity.getId().toString());
        erEntity.setEntityName(entity.getDisplayName());
        erEntity.setTableName(entity.getTableName());
        erEntity.setDescription(entity.getDescription());
        erEntity.setEntityType(entity.getEntityType().toString());
        
        // 设置默认坐标（前端可以根据需要调整）
        erEntity.setDisplayConfig(entity.getDisplayConfig() != null ? entity.getDisplayConfig() : "{}");
        erEntity.setCode(entity.getCode());

        // 获取字段信息
        List<ERFieldVO> fields = getEntityFields(entity.getId());
        erEntity.setFields(fields);

        return erEntity;
    }

    /**
     * 获取实体的字段信息
     *
     * @param entityId 实体ID
     * @return 字段VO列表
     */
    private List<ERFieldVO> getEntityFields(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.ASC);

        List<MetadataEntityFieldDO> fieldList = metadataEntityFieldRepository.getEntityFieldListByEntityId(entityId);
        List<ERFieldVO> erFields = new ArrayList<>();

        for (MetadataEntityFieldDO field : fieldList) {
            ERFieldVO erField = new ERFieldVO();
            erField.setFieldId(field.getId());
            erField.setFieldName(field.getFieldName());
            erField.setDisplayName(field.getDisplayName());
            erField.setFieldType(field.getFieldType());
            erField.setDataLength(field.getDataLength());
            erField.setDescription(field.getDescription());
            erField.setIsRequired(field.getIsRequired());
            erField.setIsUnique(field.getIsUnique());
            erField.setIsPrimaryKey(field.getIsPrimaryKey());
            erField.setIsSystemField(field.getIsSystemField());
            erField.setDefaultValue(field.getDefaultValue());
            erField.setSortOrder(field.getSortOrder());
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
        List<Long> entityIds = entities.stream()
                .map(MetadataBusinessEntityDO::getId)
                .toList();

        DefaultConfigStore relationshipConfigStore = new DefaultConfigStore();
        // 使用嵌套 OR + IN，避免把整段表达式再次包裹 IN 导致 SQL 语法与参数不匹配
        ConfigStore orStore = new DefaultConfigStore();
        ((DefaultConfigStore) orStore).or(Compare.IN, "source_entity_id", entityIds)
                .or(Compare.IN, "target_entity_id", entityIds);
        relationshipConfigStore.and(orStore);
        relationshipConfigStore.order("create_time", Order.TYPE.DESC);

        List<MetadataEntityRelationshipDO> relationshipDOs = metadataEntityRelationshipRepository.findAllByConfig(
                relationshipConfigStore);

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
     * 将实体关系DO转换为ER关系VO
     *
     * @param relationshipDO 实体关系DO
     * @param entities 实体列表，用于获取实体名称
     * @return ER关系VO
     */
    private ERRelationshipVO convertToERRelationship(MetadataEntityRelationshipDO relationshipDO, 
                                                    List<MetadataBusinessEntityDO> entities) {
        // 查找源实体和目标实体
        MetadataBusinessEntityDO sourceEntity = entities.stream()
                .filter(entity -> entity.getId().equals(relationshipDO.getSourceEntityId()))
                .findFirst()
                .orElse(null);
                
        MetadataBusinessEntityDO targetEntity = entities.stream()
                .filter(entity -> entity.getId().equals(relationshipDO.getTargetEntityId()))
                .findFirst()
                .orElse(null);

        if (sourceEntity == null || targetEntity == null) {
            return null;
        }

        ERRelationshipVO relationship = new ERRelationshipVO();
        relationship.setRelationshipId(relationshipDO.getId().toString());
        relationship.setSourceEntityId(relationshipDO.getSourceEntityId().toString());
        relationship.setSourceEntityName(sourceEntity.getDisplayName());
        relationship.setSourceFieldId(relationshipDO.getSourceFieldId().toString());
        relationship.setSourceFieldName(getFieldNameById(relationshipDO.getSourceFieldId()));
        relationship.setTargetEntityId(relationshipDO.getTargetEntityId().toString());
        relationship.setTargetEntityName(targetEntity.getDisplayName());
        relationship.setTargetFieldId(relationshipDO.getTargetFieldId().toString());
        relationship.setTargetFieldName(getFieldNameById(relationshipDO.getTargetFieldId()));
        relationship.setRelationshipType(relationshipDO.getRelationshipType());
        relationship.setRelationshipName(relationshipDO.getRelationName());
        relationship.setDescription(relationshipDO.getDescription());
        
        return relationship;
    }

    /**
     * 根据字段ID获取字段名称
     *
     * @param fieldId 字段ID
     * @return 字段名称
     */
    private String getFieldNameById(String fieldId) {
        if (fieldId == null) {
            return null;
        }
        
        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and("id", Long.valueOf(fieldId));
            MetadataEntityFieldDO field = metadataEntityFieldRepository.findById(Long.valueOf(fieldId));
            return field != null ? field.getFieldName() : null;
        } catch (NumberFormatException e) {
            log.warn("无效的字段ID: {}", fieldId);
            return null;
        }
    }

    /**
     * 根据ID获取数据源信息
     *
     * @param datasourceId 数据源ID
     * @return 数据源DO
     */
    private MetadataDatasourceDO getDatasourceById(Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", datasourceId);
        return metadataDatasourceRepository.findOne(configStore);
    }

    @Override
    public List<SimpleEntityRespVO> getSimpleEntityListByAppId(Long appId) {
        log.info("开始查询应用实体列表，应用ID: {}", appId);

        // 1. 根据appId查询该应用下的所有实体
        DefaultConfigStore entityConfigStore = new DefaultConfigStore();
        entityConfigStore.and("app_id", appId);
        entityConfigStore.order("create_time", Order.TYPE.ASC);

        List<MetadataBusinessEntityDO> entities = metadataBusinessEntityRepository.findAllByConfig(
                entityConfigStore);

        if (entities.isEmpty()) {
            log.info("应用下未找到任何实体，应用ID: {}", appId);
            return List.of();
        }

        // 2. 转换为简单实体信息VO
        List<SimpleEntityRespVO> result = entities.stream()
                .map(this::convertToSimpleEntity)
                .toList();

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
        SimpleEntityRespVO simpleEntity = new SimpleEntityRespVO();
        simpleEntity.setEntityId(entityDO.getId());
        simpleEntity.setEntityName(entityDO.getDisplayName());
        return simpleEntity;
    }

}
