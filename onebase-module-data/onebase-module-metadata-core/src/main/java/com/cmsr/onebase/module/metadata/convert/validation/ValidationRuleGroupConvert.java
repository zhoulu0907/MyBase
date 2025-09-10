package com.cmsr.onebase.module.metadata.convert.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 校验规则分组转换器
 *
 * @author bty418
 * @date 2025-01-25
 */
public class ValidationRuleGroupConvert {

    public static final ValidationRuleGroupConvert INSTANCE = new ValidationRuleGroupConvert();

    public ValidationRuleGroupRespVO convert(MetadataValidationRuleGroupDO bean) {
        return BeanUtils.toBean(bean, ValidationRuleGroupRespVO.class);
    }

    public List<ValidationRuleGroupRespVO> convertList(List<MetadataValidationRuleGroupDO> list) {
        return BeanUtils.toBean(list, ValidationRuleGroupRespVO.class);
    }

    public ValidationRuleDefinitionVO convertRuleDefinition(MetadataValidationRuleDefinitionDO bean) {
        return BeanUtils.toBean(bean, ValidationRuleDefinitionVO.class, vo -> {
            vo.setFieldValue(convertLongToString(bean.getFieldValue()));
            vo.setFieldValue2(convertLongToString(bean.getFieldValue2()));
        });
    }

    public List<ValidationRuleDefinitionVO> convertRuleDefinitionList(List<MetadataValidationRuleDefinitionDO> list) {
        return list.stream()
                .map(this::convertRuleDefinition)
                .toList();
    }

    public MetadataValidationRuleDefinitionDO convertToRuleDefinitionDO(ValidationRuleDefinitionVO vo) {
        return BeanUtils.toBean(vo, MetadataValidationRuleDefinitionDO.class, entity -> {
            entity.setFieldValue(convertStringToLong(vo.getFieldValue()));
            entity.setFieldValue2(convertStringToLong(vo.getFieldValue2()));
        });
    }

    public List<MetadataValidationRuleDefinitionDO> convertToRuleDefinitionDOList(List<ValidationRuleDefinitionVO> list) {
        return list.stream()
                .map(this::convertToRuleDefinitionDO)
                .toList();
    }

    public PageResult<ValidationRuleGroupRespVO> convertPage(PageResult<MetadataValidationRuleGroupDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    /**
     * Long转String
     */
    public String convertLongToString(Long value) {
        return value != null ? value.toString() : null;
    }

    /**
     * String转Long
     */
    public Long convertStringToLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
