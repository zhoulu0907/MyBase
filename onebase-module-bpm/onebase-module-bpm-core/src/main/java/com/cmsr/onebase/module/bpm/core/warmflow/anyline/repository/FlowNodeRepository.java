package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowNodeDO;
import org.springframework.stereotype.Repository;

/**
 * 流程节点 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowNodeRepository extends DataRepository<BpmFlowNodeDO> {

    public FlowNodeRepository() {
        super(BpmFlowNodeDO.class);
    }
}


