package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataSystemMethodDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDataSystemMethodRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataDataSystemMethodDO.METHOD_CODE, methodCode);
        configStore.and(MetadataDataSystemMethodDO.IS_ENABLED, CommonStatusEnum.ENABLE.getStatus());
        configStore.and("deleted", 0);
        return metadataDataSystemMethodRepository.findOne(configStore);
    }

    @Override
    public List<MetadataDataSystemMethodDO> getEnabledDataMethodList() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataDataSystemMethodDO.IS_ENABLED, CommonStatusEnum.ENABLE.getStatus());
        configStore.and("deleted", 0);
        configStore.order(MetadataDataSystemMethodDO.METHOD_CODE, org.anyline.entity.Order.TYPE.ASC);
        return metadataDataSystemMethodRepository.findAllByConfig(configStore);
    }

    @Override
    public MetadataDataSystemMethodDO getDataMethodById(Long id) {
        return metadataDataSystemMethodRepository.findById(id);
    }
}
