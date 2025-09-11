package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataCompensationLogDO;
import org.springframework.stereotype.Repository;

/**
 * 补偿日志 Repository 实现
 *
 * @author bty418
 * @date 2025-08-25
 */
@Repository
public class MetadataCompensationLogRepositoryImpl extends DataRepository<MetadataCompensationLogDO>
        implements MetadataCompensationLogRepository {

    public MetadataCompensationLogRepositoryImpl() {
        super(MetadataCompensationLogDO.class);
    }

    @Override
    public MetadataCompensationLogDO insert(MetadataCompensationLogDO compensation) {
        return super.insert(compensation);
    }
}
