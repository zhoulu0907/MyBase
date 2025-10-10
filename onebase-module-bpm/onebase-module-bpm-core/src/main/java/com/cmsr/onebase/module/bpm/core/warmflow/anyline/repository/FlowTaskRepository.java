package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowTaskDO;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow 待办任务 Repository
 *
 * @author liyang
 * @date 2025-01-27
 */
@Repository
public class FlowTaskRepository extends DataRepository<BpmFlowTaskDO> {

    public FlowTaskRepository() {
        super(BpmFlowTaskDO.class);
    }
}