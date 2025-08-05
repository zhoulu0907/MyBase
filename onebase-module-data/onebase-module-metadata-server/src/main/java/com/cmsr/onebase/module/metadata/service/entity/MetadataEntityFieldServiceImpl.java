package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.aynline.DataRepository;
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
import com.cmsr.onebase.module.metadata.enums.FieldTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private DataRepository dataRepository;

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
            if (businessEntity != null && businessEntity.getTableName() != null && 
                !businessEntity.getTableName().trim().isEmpty()) {
                datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
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

            dataRepository.insert(entityField);
            fieldIds.add(entityField.getId().toString());
            successCount++;
            
            // 同步到物理表
            if (businessEntity != null && datasource != null) {
                try {
                    addColumnToTable(datasource, businessEntity.getTableName(), entityField);
                } catch (Exception e) {
                    log.error("批量创建字段时同步到物理表失败，字段: {} - {}", entityField.getFieldName(), e.getMessage(), e);
                    // 不抛出异常，继续创建其他字段
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
        
        if (queryVO.getEntityId() != null) {
            configStore.and("entity_id", queryVO.getEntityId());
        }
        if (queryVO.getKeyword() != null) {
            configStore.and(Compare.LIKE, "field_name", "%" + queryVO.getKeyword() + "%")
                    .or(Compare.LIKE, "display_name", "%" + queryVO.getKeyword() + "%");
        }
        if (queryVO.getIsSystemField() != null) {
            configStore.and("is_system_field", queryVO.getIsSystemField());
        }
        
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        
        return dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    public EntityFieldDetailRespVO getEntityFieldDetail(String id) {
        Long longId = Long.valueOf(id);
        MetadataEntityFieldDO entityField = dataRepository.findById(MetadataEntityFieldDO.class, longId);
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
                MetadataEntityFieldDO firstField = dataRepository.findOne(MetadataEntityFieldDO.class, configStore);
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

            dataRepository.update(updateObj);
            successCount++;
            
            // 同步到物理表
            if (businessEntity != null && datasource != null) {
                try {
                    // 需要获取完整的字段信息进行更新
                    DefaultConfigStore configStore = new DefaultConfigStore();
                    configStore.and("id", Long.valueOf(fieldItem.getId()));
                    MetadataEntityFieldDO fullFieldInfo = dataRepository.findOne(MetadataEntityFieldDO.class, configStore);
                    if (fullFieldInfo != null) {
                        alterColumnInTable(datasource, businessEntity.getTableName(), fullFieldInfo);
                    }
                } catch (Exception e) {
                    log.error("批量更新字段时同步到物理表失败，字段ID: {} - {}", fieldItem.getId(), e.getMessage(), e);
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
            dataRepository.update(updateObj);
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

        // 插入实体字段
        MetadataEntityFieldDO entityField = BeanUtils.toBean(createReqVO, MetadataEntityFieldDO.class);
        dataRepository.insert(entityField);
        
        // 同步到物理表
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
            // 不抛出异常，避免影响字段的创建
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

        // 更新实体字段
        MetadataEntityFieldDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityFieldDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setEntityId(Long.valueOf(updateReqVO.getEntityId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        dataRepository.update(updateObj);
        
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
            // 不抛出异常，避免影响字段的更新
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
        MetadataEntityFieldDO existingField = dataRepository.findOne(MetadataEntityFieldDO.class, configStore);
        
        // 删除实体字段
        dataRepository.deleteById(MetadataEntityFieldDO.class, longId);
        
        // 从物理表删除字段
        if (existingField != null) {
            try {
                MetadataBusinessEntityDO businessEntity = getBusinessEntityById(existingField.getEntityId().toString());
                if (businessEntity != null && businessEntity.getTableName() != null && 
                    !businessEntity.getTableName().trim().isEmpty()) {
                    MetadataDatasourceDO datasource = getDatasourceById(businessEntity.getDatasourceId().toString());
                    if (datasource != null) {
                        dropColumnFromTable(datasource, businessEntity.getTableName(), existingField.getFieldName());
                    }
                }
            } catch (Exception e) {
                log.error("删除字段时从物理表删除失败: {}", e.getMessage(), e);
                // 不抛出异常，避免影响字段的删除
            }
        }
    }

    private void validateEntityFieldExists(String id) {
        Long longId = Long.valueOf(id);
        if (dataRepository.findById(MetadataEntityFieldDO.class, longId) == null) {
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
        
        long count = dataRepository.countByConfig(MetadataEntityFieldDO.class, configStore);
        if (count > 0) {
            throw exception(ENTITY_FIELD_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataEntityFieldDO getEntityField(String id) {
        Long longId = Long.valueOf(id);
        return dataRepository.findById(MetadataEntityFieldDO.class, longId);
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        // 添加查询条件
        if (pageReqVO.getEntityId() != null) {
            configStore.and("entity_id", pageReqVO.getEntityId());
        }
        if (pageReqVO.getFieldName() != null) {
            configStore.and(Compare.LIKE, "field_name", "%" + pageReqVO.getFieldName() + "%");
        }
        if (pageReqVO.getDisplayName() != null) {
            configStore.and(Compare.LIKE, "display_name", "%" + pageReqVO.getDisplayName() + "%");
        }
        if (pageReqVO.getFieldType() != null) {
            configStore.and("field_type", pageReqVO.getFieldType());
        }
        if (pageReqVO.getIsSystemField() != null) {
            configStore.and("is_system_field", pageReqVO.getIsSystemField());
        }
        
        // 分页查询
        return dataRepository.findPageWithConditions(MetadataEntityFieldDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", longEntityId);
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityFieldsByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", longEntityId);
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
        
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
            dataRepository.deleteById(MetadataEntityFieldDO.class, field.getId());
            
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
        return dataRepository.findOne(MetadataBusinessEntityDO.class, configStore);
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
        return dataRepository.findOne(MetadataDatasourceDO.class, configStore);
    }
    
    /**
     * 添加字段到物理表
     */
    private void addColumnToTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            // 准备数据源配置信息
            Map<String, Object> config = DatasourceConvert.INSTANCE.stringToMap(datasource.getConfig());
            config.put("datasourceType", datasource.getDatasourceType());
            
            // 创建临时的AnylineService用于数据库操作
            AnylineService<?> temporaryService = dataRepository.createTemporaryService(config);
            
            // 生成ADD COLUMN DDL语句
            String ddl = generateAddColumnDDL(tableName, field);
            
            // 执行DDL
            temporaryService.execute(ddl);
            
            log.info("成功为表 {} 添加字段: {}", tableName, field.getFieldName());
        } catch (Exception e) {
            log.error("为表 {} 添加字段 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            // 不抛出异常，避免影响字段的创建
        }
    }
    
    /**
     * 修改物理表字段
     */
    private void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            // 准备数据源配置信息
            Map<String, Object> config = DatasourceConvert.INSTANCE.stringToMap(datasource.getConfig());
            config.put("datasourceType", datasource.getDatasourceType());
            
            // 创建临时的AnylineService用于数据库操作
            AnylineService<?> temporaryService = dataRepository.createTemporaryService(config);
            
            // 生成ALTER COLUMN DDL语句
            String ddl = generateAlterColumnDDL(tableName, field);
            
            // 执行DDL
            temporaryService.execute(ddl);
            
            log.info("成功修改表 {} 的字段: {}", tableName, field.getFieldName());
        } catch (Exception e) {
            log.error("修改表 {} 的字段 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            // 不抛出异常，避免影响字段的更新
        }
    }
    
    /**
     * 从物理表删除字段
     */
    private void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName) {
        try {
            // 准备数据源配置信息
            Map<String, Object> config = DatasourceConvert.INSTANCE.stringToMap(datasource.getConfig());
            config.put("datasourceType", datasource.getDatasourceType());
            
            // 创建临时的AnylineService用于数据库操作
            AnylineService<?> temporaryService = dataRepository.createTemporaryService(config);
            
            // 生成DROP COLUMN DDL语句
            String ddl = generateDropColumnDDL(tableName, fieldName);
            
            // 执行DDL
            temporaryService.execute(ddl);
            
            log.info("成功从表 {} 删除字段: {}", tableName, fieldName);
        } catch (Exception e) {
            log.error("从表 {} 删除字段 {} 失败: {}", tableName, fieldName, e.getMessage(), e);
            // 不抛出异常，避免影响字段的删除
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

}
