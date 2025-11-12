package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow 待办任务 Repository
 *
 * @author liyang
 * @date 2025-10-10
 */
@Repository
public class FlowTaskRepository extends DataRepository<FlowTask> {

    public FlowTaskRepository() {
        super(FlowTask.class);
    }
}