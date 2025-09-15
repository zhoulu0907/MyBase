package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataCompensationLogDO;

/**
 * 补偿日志 Repository
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface MetadataCompensationLogRepository {
    MetadataCompensationLogDO insert(MetadataCompensationLogDO compensation);
}
