package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerEntityDO;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:38
 */
@Repository
public class FlowProcessTriggerEntityRepository extends DataRepository<FlowProcessTriggerEntityDO> {

    public FlowProcessTriggerEntityRepository() {
        super(FlowProcessTriggerEntityDO.class);
    }
}
