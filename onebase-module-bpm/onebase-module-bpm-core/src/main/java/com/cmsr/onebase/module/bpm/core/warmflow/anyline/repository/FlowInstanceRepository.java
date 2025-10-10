package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowInstanceDO;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow 流程实例 Repository
 *
 * @author liyang
 * @date 2025-01-27
 */
@Repository
public class FlowInstanceRepository extends DataRepository<BpmFlowInstanceDO> {

    public FlowInstanceRepository() {
        super(BpmFlowInstanceDO.class);
    }
}