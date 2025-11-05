package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import org.springframework.stereotype.Repository;

/**
 * 历史任务 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowHisTaskRepository extends DataRepository<FlowHisTask> {
    public FlowHisTaskRepository() {
        super(FlowHisTask.class);
    }
}


