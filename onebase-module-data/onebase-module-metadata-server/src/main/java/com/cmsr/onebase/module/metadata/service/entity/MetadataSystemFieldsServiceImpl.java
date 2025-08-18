package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataSystemFieldsRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
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
public class MetadataSystemFieldsServiceImpl implements MetadataSystemFieldsService {

    @Resource
    private MetadataSystemFieldsRepository metadataSystemFieldsRepository;

    @Override
    public List<MetadataSystemFieldsDO> findAllByConfig(DefaultConfigStore configStore) {
        return metadataSystemFieldsRepository.findAllByConfig(configStore);
    }
}
