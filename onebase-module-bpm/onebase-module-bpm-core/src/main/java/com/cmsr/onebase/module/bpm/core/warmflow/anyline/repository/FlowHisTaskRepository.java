package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowHisTaskDO;
import org.springframework.stereotype.Repository;

/**
 * 历史任务 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowHisTaskRepository extends DataRepository<BpmFlowHisTaskDO> {

    public FlowHisTaskRepository() {
        super(BpmFlowHisTaskDO.class);
    }
}


