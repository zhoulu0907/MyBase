package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 校验规则 Service 实现类
 *
 * @author bty418
 * @date 2025-01-27
 */
@Service
@Slf4j
public class MetadataValidationRuleBuildServiceImpl implements MetadataValidationRuleBuildService {

    @Resource
    private MetadataValidationRuleRepository validationRuleRepository;

    @Override
    public List<MetadataValidationRuleDefinitionDO> findAllByConfig(DefaultConfigStore configStore) {
        return validationRuleRepository.findAllByConfig(configStore);
    }
}
