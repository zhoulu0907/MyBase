package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataOutboxDO;

/**
 * Outbox Service
 *
 * 用于跨数据源写入的事件记录与后续异步投递。
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface OutboxService {
    void append(MetadataOutboxDO outbox);
}
