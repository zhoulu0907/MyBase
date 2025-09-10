package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataOutboxDO;
import org.springframework.stereotype.Repository;

/**
 * Outbox Repository 实现
 *
 * @author bty418
 * @date 2025-08-25
 */
@Repository
public class MetadataOutboxRepositoryImpl extends DataRepository<MetadataOutboxDO>
        implements MetadataOutboxRepository {

    public MetadataOutboxRepositoryImpl() {
        super(MetadataOutboxDO.class);
    }

    @Override
    public MetadataOutboxDO insert(MetadataOutboxDO outbox) {
        return super.insert(outbox);
    }
}
