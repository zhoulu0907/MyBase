package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowUserDO;
import org.springframework.stereotype.Repository;

/**
 * 流程用户 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowUserRepository extends DataRepository<BpmFlowUserDO> {

    public FlowUserRepository() {
        super(BpmFlowUserDO.class);
    }
}


