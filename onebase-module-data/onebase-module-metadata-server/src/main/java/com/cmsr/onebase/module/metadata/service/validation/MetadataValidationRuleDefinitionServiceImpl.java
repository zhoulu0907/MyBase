package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRuleDefinitionRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
public class MetadataValidationRuleDefinitionServiceImpl implements MetadataValidationRuleDefinitionService {

    @Resource
    private MetadataValidationRuleDefinitionRepository validationRuleDefinitionRepository;

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
            // valueRule 可能是 VO 或 Map；优先使用 MapStruct 转换，确保类型一致性
            MetadataValidationRuleDefinitionDO ruleDefinition;
            if (valueRule instanceof com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleDefinitionVO vo) {
                ruleDefinition = com.cmsr.onebase.module.metadata.convert.validation.ValidationRuleGroupConvert.INSTANCE.convertToRuleDefinitionDO(vo);
            } else {
                ruleDefinition = BeanUtils.toBean(valueRule, MetadataValidationRuleDefinitionDO.class);
            }
            ruleDefinition.setGroupId(groupId);
            validationRuleDefinitionRepository.upsert(ruleDefinition);
        }
    }

    @Override
    public List<MetadataValidationRuleDefinitionDO> getByGroupId(Long groupId) {
        return validationRuleDefinitionRepository.selectByGroupId(groupId);
    }

    @Override
    public void saveRuleDefinition(MetadataValidationRuleDefinitionDO ruleDefinition) {
        validationRuleDefinitionRepository.upsert(ruleDefinition);
    }
}
