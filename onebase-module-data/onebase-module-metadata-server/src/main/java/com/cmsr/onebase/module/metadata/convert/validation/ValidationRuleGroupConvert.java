package com.cmsr.onebase.module.metadata.convert.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 校验规则分组 Convert
 *
 * @author bty418
 * @date 2025-01-25
 */
@Mapper
public interface ValidationRuleGroupConvert {

    ValidationRuleGroupConvert INSTANCE = Mappers.getMapper(ValidationRuleGroupConvert.class);

    ValidationRuleGroupRespVO convert(MetadataValidationRuleGroupDO bean);

    List<ValidationRuleGroupRespVO> convertList(List<MetadataValidationRuleGroupDO> list);

    @Mapping(target = "fieldValue", expression = "java(convertLongToString(bean.getFieldValue()))")
    @Mapping(target = "fieldValue2", expression = "java(convertLongToString(bean.getFieldValue2()))")
    ValidationRuleDefinitionVO convertRuleDefinition(MetadataValidationRuleDefinitionDO bean);

    List<ValidationRuleDefinitionVO> convertRuleDefinitionList(List<MetadataValidationRuleDefinitionDO> list);

    @Mapping(target = "fieldValue", expression = "java(convertStringToLong(vo.getFieldValue()))")
    @Mapping(target = "fieldValue2", expression = "java(convertStringToLong(vo.getFieldValue2()))")
    MetadataValidationRuleDefinitionDO convertToRuleDefinitionDO(ValidationRuleDefinitionVO vo);

    List<MetadataValidationRuleDefinitionDO> convertToRuleDefinitionDOList(List<ValidationRuleDefinitionVO> list);

    default PageResult<ValidationRuleGroupRespVO> convertPage(PageResult<MetadataValidationRuleGroupDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    /**
     * Long转String
     */
    default String convertLongToString(Long value) {
        return value != null ? value.toString() : null;
    }

    /**
     * String转Long
     */
    default Long convertStringToLong(String value) {
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
