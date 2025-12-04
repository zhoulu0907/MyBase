package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleDefinitionVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleDefinitionRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 校验规则定义 Service 实现类
 *
 * @author bty418
 * @date 2025-01-27
 */
@Service
@Slf4j
public class MetadataValidationRuleDefinitionBuildServiceImpl implements MetadataValidationRuleDefinitionBuildService {

    @Resource
    private MetadataValidationRuleDefinitionRepository validationRuleDefinitionRepository;

    @Resource
    private ModelMapper modelMapper;

    @Override
    public void deleteByGroupId(Long groupId) {
        validationRuleDefinitionRepository.deleteByGroupId(groupId);
    }

    @Override
    public void saveValueRules(Long groupId, List<Object> valueRules) {
        if (CollectionUtils.isEmpty(valueRules)) {
            return;
        }

        for (Object valueRule : valueRules) {
            // valueRule 可能是 VO 或 Map；使用 BeanUtils 或 ModelMapper 转换，确保类型一致性
            MetadataValidationRuleDefinitionDO ruleDefinition;
            if (valueRule instanceof ValidationRuleDefinitionVO vo) {
                ruleDefinition = modelMapper.map(vo, MetadataValidationRuleDefinitionDO.class);
            } else {
                ruleDefinition = BeanUtils.toBean(valueRule, MetadataValidationRuleDefinitionDO.class);
            }
            ruleDefinition.setGroupUuid(String.valueOf(groupId));
            validationRuleDefinitionRepository.saveOrUpdate(ruleDefinition);
        }
    }

    @Override
    public List<MetadataValidationRuleDefinitionDO> getByGroupId(Long groupId) {
        return validationRuleDefinitionRepository.selectByGroupId(groupId);
    }

    @Override
    public void saveRuleDefinition(MetadataValidationRuleDefinitionDO ruleDefinition) {
        validationRuleDefinitionRepository.saveOrUpdate(ruleDefinition);
    }
}
