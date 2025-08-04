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
import com.cmsr.onebase.module.metadata.enums.FieldTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

        for (EntityFieldCreateItemVO fieldItem : reqVO.getFields()) {
            // 直接执行创建逻辑，异常由全局统一处理
            validateEntityFieldNameUnique(null, reqVO.getEntityId(), fieldItem.getFieldName());
            // 创建字段及数据库插入操作
            MetadataEntityFieldDO entityField = new MetadataEntityFieldDO();
            entityField.setEntityId(reqVO.getEntityId());
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
            entityField.setAppId(reqVO.getAppId());

            dataRepository.insert(entityField);
            fieldIds.add(entityField.getId().toString());
            successCount++;
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
    public EntityFieldDetailRespVO getEntityFieldDetail(Long id) {
        MetadataEntityFieldDO entityField = dataRepository.findById(MetadataEntityFieldDO.class, id);
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

        for (EntityFieldUpdateItemVO fieldItem : reqVO.getFields()) {
            // 校验字段存在
            validateEntityFieldExists(fieldItem.getId());
            // 更新字段
            MetadataEntityFieldDO updateObj = new MetadataEntityFieldDO();
            updateObj.setId(fieldItem.getId());
            if (StringUtils.hasText(fieldItem.getDisplayName())) {
                updateObj.setDisplayName(fieldItem.getDisplayName());
            }
            if (StringUtils.hasText(fieldItem.getDescription())) {
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
            updateObj.setId(sortItem.getFieldId());
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
        dataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityField(Long id) {
        // 校验存在
        validateEntityFieldExists(id);
        
        // 删除实体字段
        dataRepository.deleteById(MetadataEntityFieldDO.class, id);
    }

    private void validateEntityFieldExists(Long id) {
        if (dataRepository.findById(MetadataEntityFieldDO.class, id) == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
    }

    private void validateEntityFieldNameUnique(Long id, Long entityId, String fieldName) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        configStore.and("field_name", fieldName);
        if (id != null) {
            configStore.and(Compare.NOT_EQUAL, "id", id);
        }
        
        long count = dataRepository.countByConfig(MetadataEntityFieldDO.class, configStore);
        if (count > 0) {
            throw exception(ENTITY_FIELD_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataEntityFieldDO getEntityField(Long id) {
        return dataRepository.findById(MetadataEntityFieldDO.class, id);
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
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        configStore.order("sort_order", Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityFieldsByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        List<MetadataEntityFieldDO> fields = dataRepository.findAllByConfig(MetadataEntityFieldDO.class, configStore);
        
        for (MetadataEntityFieldDO field : fields) {
            dataRepository.deleteById(MetadataEntityFieldDO.class, field.getId());
        }
    }

}
