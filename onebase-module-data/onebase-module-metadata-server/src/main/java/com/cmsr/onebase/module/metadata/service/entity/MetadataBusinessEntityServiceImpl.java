package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.database.MetadataRepository;
import com.cmsr.onebase.module.metadata.enums.BusinessEntityTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;

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
    private MetadataRepository metadataRepository;

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

        metadataRepository.insert(businessEntity);

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
        return metadataRepository.findOne(MetadataDatasourceDO.class, configStore);
    }

    /**
     * 获取系统字段信息
     */
    private List<MetadataSystemFieldsDO> getSystemFields() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("is_enabled", 1); // 只获取启用的系统字段
        configStore.order("id", Order.TYPE.ASC);
        return metadataRepository.findAllByConfig(MetadataSystemFieldsDO.class, configStore);
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
                    .isSystemField(true) // 标记为系统字段
                    .isPrimaryKey(systemField.getIsSnowflakeId() == 1) // 雪花ID字段设为主键
                    .isRequired(systemField.getIsRequired() == 1)
                    .isUnique(systemField.getIsSnowflakeId() == 1) // 主键字段唯一
                    .allowNull(systemField.getIsRequired() != 1) // 必填字段不允许为空
                    .sortOrder(sortOrder++)
                    .validationRules(null) // 系统字段暂不设置校验规则
                    .runMode(0) // 默认编辑态
                    .appId(appId)
                    .status(0) // 默认开启
                    .fieldCode(generateFieldCode(systemField.getFieldName())) // 生成字段编码
                    .build();

            metadataRepository.insert(entityField);
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
            // 创建 AnylineService 实例
            AnylineService<?> service = metadataRepository.createTemporaryService(datasource);

            // 生成建表 DDL
            String createTableDDL = generateCreateTableDDL(tableName, systemFields);

            // 执行建表语句
            service.execute(createTableDDL);

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

            // 是否必填
            if (field.getIsRequired() == 1) {
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

        metadataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessEntity(Long id) {
        // 校验存在
        validateBusinessEntityExists(id);

        // 删除业务实体
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        metadataRepository.deleteByConfig(MetadataBusinessEntityDO.class, configStore);
    }

    private void validateBusinessEntityExists(Long id) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        if (metadataRepository.findOne(MetadataBusinessEntityDO.class, configStore) == null) {
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

        long count = metadataRepository.countByConfig(MetadataBusinessEntityDO.class, configStore);
        if (count > 0) {
            throw exception(BUSINESS_ENTITY_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntity(Long id) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("id", id);
        return metadataRepository.findOne(MetadataBusinessEntityDO.class, configStore);
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

        return metadataRepository.findPageWithConditions(MetadataBusinessEntityDO.class, configStore,
            pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataRepository.findAllByConfig(MetadataBusinessEntityDO.class, configStore);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("code", code);
        return metadataRepository.findOne(MetadataBusinessEntityDO.class, configStore);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceId(Long datasourceId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("datasource_id", datasourceId);
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataRepository.findAllByConfig(MetadataBusinessEntityDO.class, configStore);
    }

}
