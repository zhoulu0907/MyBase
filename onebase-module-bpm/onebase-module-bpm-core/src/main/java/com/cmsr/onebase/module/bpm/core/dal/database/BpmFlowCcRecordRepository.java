package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import org.springframework.stereotype.Repository;

@Repository
public class BpmFlowCcRecordRepository extends DataRepository<BpmFlowCcRecordDO> {
    public BpmFlowCcRecordRepository() {
        super(BpmFlowCcRecordDO.class);
    }
}
