package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRulePageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationTypeConfigRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDO;
import com.cmsr.onebase.module.metadata.enums.ValidationTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.VALIDATION_RULE_NOT_EXISTS;

/**
 * 校验规则 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataValidationRuleServiceImpl implements MetadataValidationRuleService {

    @Resource
    private DataRepository dataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createValidationRule(@Valid ValidationRuleSaveReqVO createReqVO) {
        // 插入校验规则
        MetadataValidationRuleDO validationRule = BeanUtils.toBean(createReqVO, MetadataValidationRuleDO.class);
        validationRule.setEntityId(Long.valueOf(createReqVO.getEntityId()));
        validationRule.setFieldId(Long.valueOf(createReqVO.getFieldId()));
        validationRule.setAppId(Long.valueOf(createReqVO.getAppId()));
        dataRepository.insert(validationRule);
        
        return validationRule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateValidationRule(@Valid ValidationRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateValidationRuleExists(Long.valueOf(updateReqVO.getId()));

        // 更新校验规则
        MetadataValidationRuleDO updateObj = BeanUtils.toBean(updateReqVO, MetadataValidationRuleDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setEntityId(Long.valueOf(updateReqVO.getEntityId()));
        updateObj.setFieldId(Long.valueOf(updateReqVO.getFieldId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        dataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteValidationRule(Long id) {
        // 校验存在
        validateValidationRuleExists(id);
        
        // 删除校验规则
        dataRepository.deleteById(MetadataValidationRuleDO.class, id);
    }

    @Override
    public ValidationRuleRespVO getValidationRuleDetail(Long id) {
        MetadataValidationRuleDO validationRule = dataRepository.findById(MetadataValidationRuleDO.class, id);
        if (validationRule == null) {
            throw exception(VALIDATION_RULE_NOT_EXISTS);
        }

        ValidationRuleRespVO result = BeanUtils.toBean(validationRule, ValidationRuleRespVO.class);
        
        // 这里可以添加关联查询，获取实体名称和字段名称
        // 为了简化，暂时使用占位符
        result.setEntityName("实体名称");
        result.setFieldName("字段名称");
        
        return result;
    }

    @Override
    public PageResult<ValidationRuleRespVO> getValidationRulePage(ValidationRulePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        // 添加查询条件
        if (pageReqVO.getAppId() != null) {
            configStore.and("app_id", pageReqVO.getAppId());
        }
        if (pageReqVO.getKeyword() != null) {
            configStore.and(Compare.LIKE, "validation_name", "%" + pageReqVO.getKeyword() + "%")
                    .or(Compare.LIKE, "validation_code", "%" + pageReqVO.getKeyword() + "%");
        }
        if (pageReqVO.getEntityId() != null) {
            Long entityIdLong = Long.valueOf(pageReqVO.getEntityId());
            log.info("解析校验规则实体ID: 字符串={}, Long={}", pageReqVO.getEntityId(), entityIdLong);
            
            // 先检查该实体是否存在任何校验规则
            DefaultConfigStore checkConfigStore = new DefaultConfigStore();
            if (pageReqVO.getAppId() != null) {
                checkConfigStore.and("app_id", pageReqVO.getAppId());
            }
            checkConfigStore.and("entity_id", entityIdLong);
            
            // 查询是否存在相关记录
            List<MetadataValidationRuleDO> existingRules = dataRepository.findAllByConfig(
                    MetadataValidationRuleDO.class, checkConfigStore);
            
            if (existingRules.isEmpty()) {
                // 如果找不到任何相关记录，直接返回空结果
                log.info("未找到实体相关校验规则，实体ID: {}，返回空结果", pageReqVO.getEntityId());
                return new PageResult<>(List.of(), 0L);
            }
            
            configStore.and("entity_id", entityIdLong);
            log.info("查询实体相关校验规则，实体ID: {}，找到 {} 条相关记录", pageReqVO.getEntityId(), existingRules.size());
        }
        if (pageReqVO.getFieldId() != null) {
            configStore.and("field_id", Long.valueOf(pageReqVO.getFieldId()));
        }
        if (pageReqVO.getValidationType() != null) {
            configStore.and("validation_type", pageReqVO.getValidationType());
        }
        
        // 分页查询
        configStore.order("create_time", Order.TYPE.DESC);
        
        // 添加分页参数调试日志
        log.info("ValidationRule分页查询参数详情: pageNo={}, pageSize={}, entityId={}, appId={}", 
                pageReqVO.getPageNo(), pageReqVO.getPageSize(), pageReqVO.getEntityId(), pageReqVO.getAppId());
        log.info("ValidationRule分页查询前ConfigStore条件: {}", configStore.toString());
        
        // 确保分页参数正确传递
        int actualPageNo = pageReqVO.getPageNo() != null ? pageReqVO.getPageNo() : 1;
        int actualPageSize = pageReqVO.getPageSize() != null ? pageReqVO.getPageSize() : 10;
        
        log.info("ValidationRule实际分页参数: actualPageNo={}, actualPageSize={}", actualPageNo, actualPageSize);
        
        PageResult<MetadataValidationRuleDO> pageResult = dataRepository.findPageWithConditions(
            MetadataValidationRuleDO.class, configStore, actualPageNo, actualPageSize);
        
        log.info("ValidationRule分页查询结果: 当前页记录数={}, 总记录数={}", 
                pageResult.getList().size(), pageResult.getTotal());
        
        // 转换为响应VO
        return new PageResult<>(
            pageResult.getList().stream().map(this::convertToRespVO).toList(),
            pageResult.getTotal()
        );
    }

    @Override
    public List<ValidationTypeConfigRespVO> getValidationTypes() {
        return Arrays.stream(ValidationTypeEnum.values())
                .map(this::convertToValidationTypeConfigRespVO)
                .toList();
    }

    /**
     * 校验规则是否存在
     */
    private void validateValidationRuleExists(Long id) {
        if (dataRepository.findById(MetadataValidationRuleDO.class, id) == null) {
            throw exception(VALIDATION_RULE_NOT_EXISTS);
        }
    }

    /**
     * 转换为响应VO
     */
    private ValidationRuleRespVO convertToRespVO(MetadataValidationRuleDO validationRuleDO) {
        ValidationRuleRespVO result = BeanUtils.toBean(validationRuleDO, ValidationRuleRespVO.class);
        // 这里可以添加关联查询，获取实体名称和字段名称
        result.setEntityName("实体名称");
        result.setFieldName("字段名称");
        return result;
    }

    /**
     * 转换校验类型枚举为响应VO
     */
    private ValidationTypeConfigRespVO convertToValidationTypeConfigRespVO(ValidationTypeEnum validationTypeEnum) {
        ValidationTypeConfigRespVO respVO = new ValidationTypeConfigRespVO();
        respVO.setValidationType(validationTypeEnum.getValidationType());
        respVO.setDisplayName(validationTypeEnum.getDisplayName());
        respVO.setDescription(validationTypeEnum.getDescription());
        respVO.setSupportedConditions(validationTypeEnum.getSupportedConditions());
        return respVO;
    }

} 