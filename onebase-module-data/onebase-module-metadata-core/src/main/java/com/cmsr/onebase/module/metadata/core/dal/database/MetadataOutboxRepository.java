package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataOutboxDO;

/**
 * Outbox Repository
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface MetadataOutboxRepository {
    MetadataOutboxDO insert(MetadataOutboxDO outbox);
}
