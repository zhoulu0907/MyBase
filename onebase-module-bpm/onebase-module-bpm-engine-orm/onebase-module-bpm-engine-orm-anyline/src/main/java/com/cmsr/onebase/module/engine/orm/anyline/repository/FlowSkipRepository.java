package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowSkip;
import org.springframework.stereotype.Repository;

/**
 * 流程跳转 仓储
 *
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowSkipRepository extends DataRepository<FlowSkip> {

    public FlowSkipRepository() {
        super(FlowSkip.class);
    }
}


