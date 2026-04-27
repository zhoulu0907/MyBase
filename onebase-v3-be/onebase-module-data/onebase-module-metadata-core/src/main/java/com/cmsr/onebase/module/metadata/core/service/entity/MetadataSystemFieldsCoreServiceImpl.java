package com.cmsr.onebase.module.metadata.core.service.entity;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataSystemFieldsRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统字段 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataSystemFieldsCoreServiceImpl implements MetadataSystemFieldsCoreService {

    @Resource
    private MetadataSystemFieldsRepository metadataSystemFieldsRepository;

    @Override
    public List<MetadataSystemFieldsDO> findAllByConfig(QueryWrapper queryWrapper) {
        return metadataSystemFieldsRepository.list(queryWrapper);
    }

    @Override
    public List<MetadataSystemFieldsDO> findAllEnabeldSystemFields() {
        QueryWrapper queryWrapper = QueryWrapper.create().select(MetadataSystemFieldsDO::getId, MetadataSystemFieldsDO::getFieldName, MetadataSystemFieldsDO::getDefaultValue)
                .eq(MetadataSystemFieldsDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus())
                .orderBy(MetadataSystemFieldsDO::getId, true);
        return metadataSystemFieldsRepository.list(queryWrapper);
    }
}
