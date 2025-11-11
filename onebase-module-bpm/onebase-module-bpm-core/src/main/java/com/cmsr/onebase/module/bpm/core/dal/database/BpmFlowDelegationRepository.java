package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowDelegationDO;
import org.springframework.stereotype.Repository;

/**
 * BPM流程代理仓库
 *
 * @author liyang
 * @date 2025-11-10
 */
@Repository
public class BpmFlowDelegationRepository extends DataRepository<BpmFlowDelegationDO> {
    public BpmFlowDelegationRepository() {
        super(BpmFlowDelegationDO.class);
    }
}
