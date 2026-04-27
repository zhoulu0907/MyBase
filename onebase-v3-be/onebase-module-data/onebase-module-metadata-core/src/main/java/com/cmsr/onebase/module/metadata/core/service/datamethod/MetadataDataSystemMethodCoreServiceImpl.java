package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDataSystemMethodRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据系统方法 Service 实现类
 *
 * @author bty418
 * @date 2025-01-27
 */
@Service
@Slf4j
public class MetadataDataSystemMethodCoreServiceImpl implements MetadataDataSystemMethodCoreService {

    @Resource
    private MetadataDataSystemMethodRepository metadataDataSystemMethodRepository;

    @Override
    public MetadataDataSystemMethodDO getDataMethodByCode(String methodCode) {
        QueryWrapper queryWrapper = metadataDataSystemMethodRepository.query()
                .eq(MetadataDataSystemMethodDO::getMethodCode, methodCode)
                .eq(MetadataDataSystemMethodDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus());
        return metadataDataSystemMethodRepository.getOne(queryWrapper);
    }

    @Override
    public List<MetadataDataSystemMethodDO> getEnabledDataMethodList() {
        QueryWrapper queryWrapper = metadataDataSystemMethodRepository.query()
                .eq(MetadataDataSystemMethodDO::getIsEnabled, CommonStatusEnum.ENABLE.getStatus())
                .orderBy(MetadataDataSystemMethodDO::getMethodCode, true);
        return metadataDataSystemMethodRepository.list(queryWrapper);
    }

    @Override
    public MetadataDataSystemMethodDO getDataMethodById(Long id) {
        return metadataDataSystemMethodRepository.getById(id);
    }
}
