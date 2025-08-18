package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRuleRepository;
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
public class MetadataValidationRuleServiceImpl implements MetadataValidationRuleService {

    @Resource
    private MetadataValidationRuleRepository validationRuleRepository;

    @Override
    public List<MetadataValidationRuleDefinitionDO> findAllByConfig(DefaultConfigStore configStore) {
        return validationRuleRepository.findAllByConfig(configStore);
    }
}
