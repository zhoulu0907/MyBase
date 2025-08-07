package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchCreateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchCreateRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchSortReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchUpdateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldBatchUpdateRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldCreateItemVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldDetailRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldSortItemVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.EntityFieldUpdateItemVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.convert.datasource.DatasourceConvert;
import com.cmsr.onebase.module.metadata.dal.database.MetadataRepository;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.enums.FieldTypeEnum;
import com.cmsr.onebase.module.metadata.enums.BusinessEntityTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.ENTITY_FIELD_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.ENTITY_FIELD_CODE_DUPLICATE;

/**
 * 实体字段 Service 实现类
 */
@Service
@Slf4j
public class MetadataEntityFieldServiceImpl implements MetadataEntityFieldService {

    @Resource
    private DatasourceConvert datasourceConvert;
    @Resource
    private MetadataRepository metadataRepository;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Override
    public List<FieldTypeConfigRespVO> getFieldTypes() {
        return Arrays.stream(FieldTypeEnum.values())
                .map(this::convertToFieldTypeConfigRespVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchCreateRespVO batchCreateEntityFields(@Valid EntityFieldBatchCreateReqVO reqVO) {
        EntityFieldBatchCreateRespVO result = new EntityFieldBatchCreateRespVO();
        List<String> fieldIds = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // 获取业务实体和数据源信息（用于批量创建物理表字段）
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = getBusinessEntityById(reqVO.getEntityId());
            log.info("获取到业务实体: {}, 表名: {}, 数据源ID: {}", 
                businessEntity != null ? businessEntity.getId() : "null",
                businessEntity != null ? businessEntity.getTableName() : "null",
                businessEntity != null ? businessEntity.getDatasourceId() : "null");
                
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
                log.info("获取到数据源: {}, 数据源名称: {}, 数据源类型: {}", 
                    datasource != null ? datasource.getId() : "null",
                    datasource != null ? datasource.getDatasourceName() : "null",
                    datasource != null ? datasource.getDatasourceType() : "null");
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取业务实体信息失败: " + e.getMessage(), e);
        }

        for (EntityFieldCreateItemVO fieldItem : reqVO.getFields()) {
            // 直接执行创建逻辑，异常由全局统一处理
            validateEntityFieldNameUnique(null, reqVO.getEntityId(), fieldItem.getFieldName());
            // 创建字段及数据库插入操作
            MetadataEntityFieldDO entityField = new MetadataEntityFieldDO();
            entityField.setEntityId(Long.valueOf(reqVO.getEntityId()));
            entityField.setFieldName(fieldItem.getFieldName());
            entityField.setDisplayName(fieldItem.getDisplayName());
            entityField.setFieldType(fieldItem.getFieldType());
            entityField.setDataLength(fieldItem.getDataLength());
            entityField.setDescription(fieldItem.getDescription());
            entityField.setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : false);
            entityField.setIsUnique(fieldItem.getIsUnique() != null ? fieldItem.getIsUnique() : false);
            entityField.setAllowNull(fieldItem.getAllowNull() != null ? fieldItem.getAllowNull() : true);
            entityField.setDefaultValue(fieldItem.getDefaultValue());
            entityField.setSortOrder(fieldItem.getSortOrder() != null ? fieldItem.getSortOrder() : 0);
            entityField.setIsSystemField(false);
            entityField.setIsPrimaryKey(false);
            entityField.setAppId(Long.valueOf(reqVO.getAppId()));

            metadataRepository.insert(entityField);
            fieldIds.add(entityField.getId().toString());
            successCount++;

            // 同步到物理表 - 失败时直接抛出异常回滚事务
            if (businessEntity != null && datasource != null) {
                try {
                    addColumnToTable(datasource, businessEntity.getTableName(), entityField);
                } catch (Exception e) {
                    log.error("批量创建字段时同步到物理表失败，字段: {} - {}", entityField.getFieldName(), e.getMessage(), e);
                    throw new RuntimeException("添加列失败: " + e.getMessage(), e);
                }
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setFieldIds(fieldIds);
        return result;
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByConditions(EntityFieldQueryVO queryVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (queryVO.getEntityId() != null && !queryVO.getEntityId().trim().isEmpty()) {
            configStore.and("entity_id", Long.valueOf(queryVO.getEntityId()));
        }
        if (queryVO.getKeyword() != null && !queryVO.getKeyword().trim().isEmpty()) {
            configStore.and(Compare.LIKE, "field_name", "%" + queryVO.getKeyword() + "%")
                    .or(Compare.LIKE, "display_name", "%" + queryVO.getKeyword() + "%");
        }
        if (queryVO.getIsSystemField() != null) {
            configStore.and("is_system_field", queryVO.getIsSystemField());
        }
        if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
            configStore.and(Compare.LIKE, "field_code", "%" + queryVO.getFieldCode() + "%");
        }

        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);

        return metadataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    public EntityFieldDetailRespVO getEntityFieldDetail(String id) {
        Long longId = Long.valueOf(id);
        MetadataEntityFieldDO entityField = metadataRepository.findById(MetadataEntityFieldDO.class, longId);
        if (entityField == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }

        EntityFieldDetailRespVO result = BeanUtils.toBean(entityField, EntityFieldDetailRespVO.class);

        // 获取实体名称（这里简化处理，实际项目中可能需要关联查询）
        result.setEntityName("实体名称");

        // 获取校验规则（这里暂时返回空列表，后续实现校验规则管理时再完善）
        result.setValidationRules(new ArrayList<>());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchUpdateRespVO batchUpdateEntityFields(@Valid EntityFieldBatchUpdateReqVO reqVO) {
        EntityFieldBatchUpdateRespVO result = new EntityFieldBatchUpdateRespVO();
        int successCount = 0;
        int failureCount = 0;

        // 获取业务实体和数据源信息（用于批量更新物理表字段）
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            // 获取第一个字段的entityId来确定业务实体
            if (!reqVO.getFields().isEmpty()) {
                String firstFieldId = reqVO.getFields().get(0).getId();
                DefaultConfigStore configStore = new DefaultConfigStore();
                configStore.and("id", Long.valueOf(firstFieldId));
                MetadataEntityFieldDO firstField = metadataRepository.findOne(MetadataEntityFieldDO.class, configStore);
                if (firstField != null) {
                    businessEntity = getBusinessEntityById(firstField.getEntityId().toString());
                    if (businessEntity != null && businessEntity.getTableName() != null &&
                        !businessEntity.getTableName().trim().isEmpty()) {
                        datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (EntityFieldUpdateItemVO fieldItem : reqVO.getFields()) {
            // 校验字段存在
            validateEntityFieldExists(fieldItem.getId());
            // 更新字段
            MetadataEntityFieldDO updateObj = new MetadataEntityFieldDO();
            updateObj.setId(Long.valueOf(fieldItem.getId()));
            if (fieldItem.getDisplayName() != null && !fieldItem.getDisplayName().trim().isEmpty()) {
                updateObj.setDisplayName(fieldItem.getDisplayName());
            }
            if (fieldItem.getDescription() != null && !fieldItem.getDescription().trim().isEmpty()) {
                updateObj.setDescription(fieldItem.getDescription());
            }
            if (fieldItem.getIsRequired() != null) {
                updateObj.setIsRequired(fieldItem.getIsRequired());
            }
            if (fieldItem.getDataLength() != null) {
                updateObj.setDataLength(fieldItem.getDataLength());
            }

            metadataRepository.update(updateObj);
            successCount++;

            // 同步到物理表
            if (businessEntity != null && datasource != null) {
                try {
                    // 需要获取完整的字段信息进行更新
                    DefaultConfigStore configStore = new DefaultConfigStore();
                    configStore.and("id", Long.valueOf(fieldItem.getId()));
                    MetadataEntityFieldDO fullFieldInfo = metadataRepository.findOne(MetadataEntityFieldDO.class, configStore);
                    if (fullFieldInfo != null) {
                        alterColumnInTable(datasource, businessEntity.getTableName(), fullFieldInfo);
                    }
                } catch (Exception e) {
                    log.error("批量更新字段时同步到物理表失败，字段ID: {} - {}", fieldItem.getId(), e.getMessage(), e);
                    failureCount++;
                    // 不抛出异常，继续更新其他字段
                }
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSortEntityFields(@Valid EntityFieldBatchSortReqVO reqVO) {
        for (EntityFieldSortItemVO sortItem : reqVO.getFieldSorts()) {
            // 校验字段存在
            validateEntityFieldExists(sortItem.getFieldId());

            // 更新排序
            MetadataEntityFieldDO updateObj = new MetadataEntityFieldDO();
            updateObj.setId(Long.valueOf(sortItem.getFieldId()));
            updateObj.setSortOrder(sortItem.getSortOrder());
            metadataRepository.update(updateObj);
        }
    }

    /**
     * 将字段类型枚举转换为响应VO
     *
     * @param fieldTypeEnum 字段类型枚举
     * @return 字段类型配置响应VO
     */
    private FieldTypeConfigRespVO convertToFieldTypeConfigRespVO(FieldTypeEnum fieldTypeEnum) {
        FieldTypeConfigRespVO respVO = new FieldTypeConfigRespVO();
        respVO.setFieldType(fieldTypeEnum.getFieldType());
        respVO.setDisplayName(fieldTypeEnum.getDisplayName());
        respVO.setCategory(fieldTypeEnum.getCategory());
        respVO.setSupportLength(fieldTypeEnum.getSupportLength());
        respVO.setSupportDecimal(fieldTypeEnum.getSupportDecimal());
        respVO.setDefaultLength(fieldTypeEnum.getDefaultLength());
        respVO.setMaxLength(fieldTypeEnum.getMaxLength());
        // 对于支持小数位的类型，设置默认小数位数
        if (fieldTypeEnum.getSupportDecimal()) {
            respVO.setDefaultDecimal(2);
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntityField(@Valid EntityFieldSaveReqVO createReqVO) {
        // 校验字段名唯一性
        validateEntityFieldNameUnique(null, createReqVO.getEntityId(), createReqVO.getFieldName());

        // 校验实体类型是否允许修改表结构
        validateEntityAllowModifyStructure(Long.valueOf(createReqVO.getEntityId()));

        // 插入实体字段
        MetadataEntityFieldDO entityField = BeanUtils.toBean(createReqVO, MetadataEntityFieldDO.class);
        entityField.setEntityId(Long.valueOf(createReqVO.getEntityId()));
        entityField.setAppId(Long.valueOf(createReqVO.getAppId()));
        entityField.setIsSystemField(false); // 手动创建的字段不是系统字段
        entityField.setRunMode(0); // 默认编辑态
        entityField.setStatus(0); // 默认开启
        // 如果没有提供fieldCode，则根据fieldName生成
        if (entityField.getFieldCode() == null || entityField.getFieldCode().trim().isEmpty()) {
            entityField.setFieldCode(generateFieldCode(createReqVO.getFieldName()));
        }

        metadataRepository.insert(entityField);

        // 同步到物理表 - 失败时直接抛出异常回滚事务
        try {
            MetadataBusinessEntityDO businessEntity = getBusinessEntityById(createReqVO.getEntityId().toString());
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
                if (datasource != null) {
                    addColumnToTable(datasource, businessEntity.getTableName(), entityField);
                }
            }
        } catch (Exception e) {
            log.error("创建字段时同步到物理表失败: {}", e.getMessage(), e);
            throw new RuntimeException("同步物理表失败: " + e.getMessage(), e);
        }

        return entityField.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntityField(@Valid EntityFieldSaveReqVO updateReqVO) {
        // 校验存在
        validateEntityFieldExists(updateReqVO.getId());
        // 校验字段名唯一性
        validateEntityFieldNameUnique(updateReqVO.getId(), updateReqVO.getEntityId(), updateReqVO.getFieldName());
        // 校验实体类型是否允许修改表结构
        validateEntityAllowModifyStructure(Long.valueOf(updateReqVO.getEntityId()));

        // 更新实体字段
        MetadataEntityFieldDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityFieldDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setEntityId(Long.valueOf(updateReqVO.getEntityId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        // 如果没有提供fieldCode，则根据fieldName生成
        if (updateObj.getFieldCode() == null || updateObj.getFieldCode().trim().isEmpty()) {
            updateObj.setFieldCode(generateFieldCode(updateReqVO.getFieldName()));
        }
        metadataRepository.update(updateObj);

        // 同步到物理表
        try {
            MetadataBusinessEntityDO businessEntity = getBusinessEntityById(updateReqVO.getEntityId().toString());
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
                if (datasource != null) {
                    alterColumnInTable(datasource, businessEntity.getTableName(), updateObj);
                }
            }
        } catch (Exception e) {
            log.error("更新字段时同步到物理表失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新物理表字段失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityField(String id) {
        // 校验存在
        validateEntityFieldExists(id);

        // 获取字段信息（在删除前需要获取相关信息用于删除物理表字段）
        Long longId = Long.valueOf(id);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", longId);
        MetadataEntityFieldDO existingField = metadataRepository.findOne(MetadataEntityFieldDO.class, configStore);

        if (existingField != null) {
            // 校验实体类型是否允许修改表结构
            validateEntityAllowModifyStructure(existingField.getEntityId());
        }

        // 删除实体字段
        metadataRepository.deleteById(MetadataEntityFieldDO.class, longId);

        // 从物理表删除字段
        if (existingField != null) {
            try {
                MetadataBusinessEntityDO businessEntity = getBusinessEntityById(existingField.getEntityId());
                if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                    MetadataDatasourceDO datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
                    if (datasource != null) {
                        dropColumnFromTable(datasource, businessEntity.getTableName(), existingField.getFieldName());
                    }
                }
            } catch (Exception e) {
                log.error("删除字段时从物理表删除失败: {}", e.getMessage(), e);
                throw new RuntimeException("删除物理表字段失败: " + e.getMessage(), e);
            }
        }
    }

    private void validateEntityFieldExists(String id) {
        Long longId = Long.valueOf(id);
        if (metadataRepository.findById(MetadataEntityFieldDO.class, longId) == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
    }

    private void validateEntityFieldNameUnique(String id, String entityId, String fieldName) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", longEntityId);
        configStore.and("field_name", fieldName);
        if (id != null) {
            Long longId = Long.valueOf(id);
            configStore.and(Compare.NOT_EQUAL, "id", longId);
        }

        long count = metadataRepository.countByConfig(MetadataEntityFieldDO.class, configStore);
        if (count > 0) {
            throw exception(ENTITY_FIELD_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataEntityFieldDO getEntityField(String id) {
        Long longId = Long.valueOf(id);
        return metadataRepository.findById(MetadataEntityFieldDO.class, longId);
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (pageReqVO.getEntityId() != null && !pageReqVO.getEntityId().trim().isEmpty()) {
            configStore.and("entity_id", Long.valueOf(pageReqVO.getEntityId()));
        }
        if (pageReqVO.getFieldName() != null && !pageReqVO.getFieldName().trim().isEmpty()) {
            configStore.and(Compare.LIKE, "field_name", "%" + pageReqVO.getFieldName() + "%");
        }
        if (pageReqVO.getDisplayName() != null && !pageReqVO.getDisplayName().trim().isEmpty()) {
            configStore.and(Compare.LIKE, "display_name", "%" + pageReqVO.getDisplayName() + "%");
        }
        if (pageReqVO.getFieldType() != null && !pageReqVO.getFieldType().trim().isEmpty()) {
            configStore.and("field_type", pageReqVO.getFieldType());
        }
        if (pageReqVO.getIsSystemField() != null) {
            configStore.and("is_system_field", pageReqVO.getIsSystemField());
        }
        if (pageReqVO.getIsPrimaryKey() != null) {
            configStore.and("is_primary_key", pageReqVO.getIsPrimaryKey());
        }
        if (pageReqVO.getIsRequired() != null) {
            configStore.and("is_required", pageReqVO.getIsRequired());
        }
        if (pageReqVO.getRunMode() != null) {
            configStore.and("run_mode", pageReqVO.getRunMode());
        }
        if (pageReqVO.getAppId() != null && !pageReqVO.getAppId().trim().isEmpty()) {
            configStore.and("app_id", Long.valueOf(pageReqVO.getAppId()));
        }
        if (pageReqVO.getFieldCode() != null && !pageReqVO.getFieldCode().trim().isEmpty()) {
            configStore.and(Compare.LIKE, "field_code", "%" + pageReqVO.getFieldCode() + "%");
        }

        // 添加排序：按照字段排序优先，然后按创建时间倒序
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);

        // 分页查询
        return metadataRepository.findPageWithConditions(MetadataEntityFieldDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", longEntityId);
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityFieldsByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", longEntityId);
        List<MetadataEntityFieldDO> fields = metadataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);

        // 获取业务实体信息，用于批量删除物理表字段
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = getBusinessEntityById(entityId);
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (MetadataEntityFieldDO field : fields) {
            // 删除数据库记录
            metadataRepository.deleteById(MetadataEntityFieldDO.class, field.getId());

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
     * 获取业务实体信息
     */
    private MetadataBusinessEntityDO getBusinessEntityById(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", Long.valueOf(entityId));
        return metadataRepository.findOne(MetadataBusinessEntityDO.class, configStore);
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
     * 添加列到表
     */
    private void addColumnToTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            log.info("开始为表 {} 添加列 {}, 数据源: {} ({})", 
                tableName, field.getFieldName(), 
                datasource.getDatasourceName(), datasource.getDatasourceType());
            log.info("数据源配置: {}", datasource.getConfig());
                
            // 创建 AnylineService 实例
            AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

            // 验证连接的数据库
            String currentDb = getCurrentDatabase(service);
            log.info("当前连接的数据库: {}, 期望连接的数据库: onebase_business", currentDb);

            // 首先检查表是否存在
            if (!checkTableExists(service, tableName)) {
                throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
            }

            // 生成添加列 DDL
            String addColumnDDL = generateAddColumnDDL(tableName, field);

            // 执行添加列语句
            service.execute(addColumnDDL);

            log.info("成功为表 {} 添加列: {}", tableName, field.getFieldName());
        } catch (Exception e) {
            log.error("为表 {} 添加列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("添加列失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查表是否存在
     */
    private boolean checkTableExists(AnylineService<?> service, String tableName) {
        try {
            log.info("检查表是否存在 - 表名: {}", tableName);
            
            // 简单尝试查询表，如果表不存在会抛出异常
            String testSql = "SELECT 1 FROM " + tableName + " LIMIT 1";
            service.querys(testSql);
            
            log.info("表 {} 存在，当前连接的数据库: {}", tableName, getCurrentDatabase(service));
            return true;
        } catch (Exception e) {
            log.warn("表 {} 不存在或查询失败: {}", tableName, e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前连接的数据库名称（用于调试）
     */
    private String getCurrentDatabase(AnylineService<?> service) {
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
     * 修改表中的列
     */
    private void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            // 创建 AnylineService 实例
            AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

            // 生成修改列 DDL
            String alterColumnDDL = generateAlterColumnDDL(tableName, field);

            // 执行修改列语句
            service.execute(alterColumnDDL);

            log.info("成功修改表 {} 的列: {}", tableName, field.getFieldName());
        } catch (Exception e) {
            log.error("修改表 {} 的列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("修改列失败", e);
        }
    }

    /**
     * 从表中删除列
     */
    private void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName) {
        try {
            // 创建 AnylineService 实例
            AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

            // 生成删除列 DDL
            String dropColumnDDL = generateDropColumnDDL(tableName, fieldName);

            // 执行删除列语句
            service.execute(dropColumnDDL);

            log.info("成功从表 {} 删除列: {}", tableName, fieldName);
        } catch (Exception e) {
            log.error("从表 {} 删除列 {} 失败: {}", tableName, fieldName, e.getMessage(), e);
            throw new RuntimeException("删除列失败", e);
        }
    }

    /**
     * 生成添加字段的DDL语句
     */
    private String generateAddColumnDDL(String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ADD COLUMN \"")
           .append(field.getFieldName()).append("\" ");

        // 字段类型映射
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append(columnType);

        // 是否必填
        if (field.getIsRequired() != null && field.getIsRequired()) {
            ddl.append(" NOT NULL");
        }

        // 默认值
        if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
            ddl.append(" DEFAULT ").append(field.getDefaultValue());
        }

        ddl.append(";");

        // 添加字段注释
        if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
            ddl.append("\nCOMMENT ON COLUMN \"").append(tableName).append("\".\"")
               .append(field.getFieldName()).append("\" IS '").append(field.getDescription()).append("';");
        }

        return ddl.toString();
    }

    /**
     * 生成修改字段的DDL语句
     */
    private String generateAlterColumnDDL(String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();

        // 修改字段类型
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
           .append(field.getFieldName()).append("\" TYPE ").append(columnType).append(";\n");

        // 修改是否允许为空
        if (field.getIsRequired() != null) {
            if (field.getIsRequired()) {
                ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
                   .append(field.getFieldName()).append("\" SET NOT NULL;\n");
            } else {
                ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
                   .append(field.getFieldName()).append("\" DROP NOT NULL;\n");
            }
        }

        // 修改默认值
        if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
            ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
               .append(field.getFieldName()).append("\" SET DEFAULT ").append(field.getDefaultValue()).append(";\n");
        }

        // 更新字段注释
        if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
            ddl.append("COMMENT ON COLUMN \"").append(tableName).append("\".\"")
               .append(field.getFieldName()).append("\" IS '").append(field.getDescription()).append("';");
        }

        return ddl.toString();
    }

    /**
     * 生成删除字段的DDL语句
     */
    private String generateDropColumnDDL(String tableName, String fieldName) {
        return "ALTER TABLE \"" + tableName + "\" DROP COLUMN IF EXISTS \"" + fieldName + "\";";
    }

    /**
     * 字段类型映射
     */
    private String mapFieldType(String fieldType, Integer dataLength) {
        switch (fieldType.toUpperCase()) {
            case "BIGINT":
                return "BIGINT";
            case "VARCHAR":
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
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
            case "DATE":
                return "DATE";
            case "TIME":
                return "TIME";
            case "JSON":
                return "JSONB";
            default:
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")"; // 默认类型
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
     * 校验实体是否允许修改表结构
     *
     * @param entityId 实体ID
     */
    private void validateEntityAllowModifyStructure(Long entityId) {
        // 获取业务实体信息
        MetadataBusinessEntityDO businessEntity = getBusinessEntityById(entityId);
        if (businessEntity == null) {
            throw new IllegalArgumentException("业务实体不存在");
        }

        // 检查实体类型是否允许修改表结构
        if (!BusinessEntityTypeEnum.allowModifyTableStructure(businessEntity.getEntityType())) {
            BusinessEntityTypeEnum entityType = BusinessEntityTypeEnum.getByCode(businessEntity.getEntityType());
            String typeName = entityType != null ? entityType.getName() : "未知类型";
            throw new IllegalArgumentException(
                String.format("实体类型为 %s (%s)，不允许修改表结构", typeName, businessEntity.getEntityType()));
        }
    }

    /**
     * 根据ID获取业务实体
     *
     * @param entityId 实体ID
     * @return 业务实体DO
     */
    private MetadataBusinessEntityDO getBusinessEntityById(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", entityId);
        return metadataRepository.findOne(MetadataBusinessEntityDO.class, configStore);
    }

}
