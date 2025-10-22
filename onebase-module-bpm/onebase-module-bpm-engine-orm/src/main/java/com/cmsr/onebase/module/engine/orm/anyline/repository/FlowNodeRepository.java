package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowNode;
import org.springframework.stereotype.Repository;

/**
 * 流程节点 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowNodeRepository extends DataRepository<FlowNode> {

    public FlowNodeRepository() {
        super(FlowNode.class);
    }
}


