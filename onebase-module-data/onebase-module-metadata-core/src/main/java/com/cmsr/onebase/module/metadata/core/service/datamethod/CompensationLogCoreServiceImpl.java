package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataCompensationLogDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataCompensationLogRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 补偿日志 Service 实现
 *
 * @author bty418
 * @date 2025-08-22
 */
@Service
public class CompensationLogCoreServiceImpl implements CompensationLogCoreService {

    @Resource
    private MetadataCompensationLogRepository repository;

    @Override
    public void append(MetadataCompensationLogDO compensation) {
        repository.insert(compensation);
    }
}
