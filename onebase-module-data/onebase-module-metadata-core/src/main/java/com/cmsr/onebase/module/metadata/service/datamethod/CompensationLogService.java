package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataCompensationLogDO;

/**
 * 补偿日志 Service
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface CompensationLogService {
    void append(MetadataCompensationLogDO compensation);
}
