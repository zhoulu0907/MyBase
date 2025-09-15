package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataOutboxDO;

/**
 * Outbox Service
 *
 * 用于跨数据源写入的事件记录与后续异步投递。
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface OutboxCoreService {
    void append(MetadataOutboxDO outbox);
}
