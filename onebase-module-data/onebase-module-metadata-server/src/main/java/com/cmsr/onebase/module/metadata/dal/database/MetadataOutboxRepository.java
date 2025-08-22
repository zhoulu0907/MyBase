package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataOutboxDO;

/**
 * Outbox Repository
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface MetadataOutboxRepository {
    void insert(MetadataOutboxDO outbox);
}
