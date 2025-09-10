package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataOutboxDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataOutboxRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Outbox Service 实现
 *
 * @author bty418
 * @date 2025-08-22
 */
@Service
public class OutboxServiceImpl implements OutboxService {

    @Resource
    private MetadataOutboxRepository repository;

    @Override
    public void append(MetadataOutboxDO outbox) {
        repository.insert(outbox);
    }
}
