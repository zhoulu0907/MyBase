package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationTypeRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 校验类型查询 Service 实现
 *
 * @author matianyu
 * @date 2025-12-05
 */
@Service
@Slf4j
public class MetadataValidationTypeBuildServiceImpl implements MetadataValidationTypeBuildService {

    @Resource
    private MetadataValidationTypeRepository validationTypeRepository;

    @Override
    public Map<Long, MetadataValidationTypeDO> getByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        List<MetadataValidationTypeDO> list = validationTypeRepository.findByIds(ids);
        return list.stream()
                .filter(vo -> vo != null && vo.getId() != null)
                .collect(Collectors.toMap(MetadataValidationTypeDO::getId, vo -> vo));
    }
}
