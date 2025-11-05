package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import org.springframework.stereotype.Repository;

/**
 * BPM流程实例扩展数据访问层
 *
 * @author liyang
 * @date 2025-10-28
 */
@Repository
public class BpmFlowInsBizExtRepository extends DataRepository<BpmFlowInsBizExtDO> {
    public BpmFlowInsBizExtRepository() {
        super(BpmFlowInsBizExtDO.class);
    }
}
