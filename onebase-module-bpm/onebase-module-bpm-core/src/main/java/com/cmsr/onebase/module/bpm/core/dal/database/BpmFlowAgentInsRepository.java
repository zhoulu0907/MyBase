package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import org.springframework.stereotype.Repository;

/**
 * BPM流程代理实例数据访问层
 *
 * @author liyang
 * @date 2025-11-22
 */
@Repository
public class BpmFlowAgentInsRepository extends DataRepository<BpmFlowAgentInsDO> {
    public BpmFlowAgentInsRepository() {
        super(BpmFlowAgentInsDO.class);
    }
}
