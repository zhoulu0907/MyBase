package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow 流程实例 Repository
 *
 * @author liyang
 * @date 2025-10-10
 */
@Repository
public class FlowInstanceRepository extends DataRepository<FlowInstance> {

    public FlowInstanceRepository() {
        super(FlowInstance.class);
    }
}