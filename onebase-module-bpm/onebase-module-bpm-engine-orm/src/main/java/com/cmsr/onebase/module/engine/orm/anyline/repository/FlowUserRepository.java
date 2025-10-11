package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowUser;
import org.springframework.stereotype.Repository;

/**
 * 流程用户 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowUserRepository extends DataRepository<FlowUser> {

    public FlowUserRepository() {
        super(FlowUser.class);
    }
}


