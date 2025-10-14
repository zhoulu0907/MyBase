package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowDefinition;
import org.springframework.stereotype.Repository;

/**
 * 流程定义 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowDefinitionRepository extends DataRepository<FlowDefinition> {

    public FlowDefinitionRepository() {
        super(FlowDefinition.class);
    }
}


