package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataCompensationLogDO;

/**
 * 补偿日志 Service
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface CompensationLogCoreService {
    void append(MetadataCompensationLogDO compensation);
}
