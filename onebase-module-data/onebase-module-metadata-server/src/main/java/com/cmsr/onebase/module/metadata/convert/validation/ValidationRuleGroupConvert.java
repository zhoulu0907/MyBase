package com.cmsr.onebase.module.metadata.convert.validation;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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

    ValidationRuleDefinitionVO convertRuleDefinition(MetadataValidationRuleDefinitionDO bean);

    List<ValidationRuleDefinitionVO> convertRuleDefinitionList(List<MetadataValidationRuleDefinitionDO> list);

    MetadataValidationRuleDefinitionDO convertToRuleDefinitionDO(ValidationRuleDefinitionVO vo);

    List<MetadataValidationRuleDefinitionDO> convertToRuleDefinitionDOList(List<ValidationRuleDefinitionVO> list);

    default PageResult<ValidationRuleGroupRespVO> convertPage(PageResult<MetadataValidationRuleGroupDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
