package com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowSkipDO;
import org.springframework.stereotype.Repository;

/**
 * 节点跳转 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowSkipRepository extends DataRepository<BpmFlowSkipDO> {

    public FlowSkipRepository() {
        super(BpmFlowSkipDO.class);
    }
}


