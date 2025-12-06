package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataPermitRefOtftRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 关联查询 Service 实现
 *
 * @author matianyu
 * @date 2025-12-05
 */
@Service
@Slf4j
public class MetadataPermitRefOtftBuildServiceImpl implements MetadataPermitRefOtftBuildService {

    @Resource
    private MetadataPermitRefOtftRepository permitRefOtftRepository;

    @Override
    public List<MetadataPermitRefOtftDO> listByFieldTypeIds(Set<Long> fieldTypeIds) {
        if (fieldTypeIds == null || fieldTypeIds.isEmpty()) {
            return new ArrayList<>();
        }
        return permitRefOtftRepository.findByFieldTypeIds(fieldTypeIds);
    }
}
