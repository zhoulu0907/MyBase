package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLExecutionLogRepository extends DataRepository<ETLExecutionLogDO> {
    public ETLExecutionLogRepository() {
        super(ETLExecutionLogDO.class);
    }
}
