package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowDefinitionDO;
import org.springframework.stereotype.Repository;

/**
 * 流程定义 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowDefinitionRepository extends DataRepository<BpmFlowDefinitionDO> {

    public FlowDefinitionRepository() {
        super(BpmFlowDefinitionDO.class);
    }
}


