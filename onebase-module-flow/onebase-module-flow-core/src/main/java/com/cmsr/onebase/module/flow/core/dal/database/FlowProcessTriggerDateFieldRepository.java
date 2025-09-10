package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTriggerDateFieldDO;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:38
 */
@Repository
public class FlowProcessTriggerDateFieldRepository extends DataRepository<FlowProcessTriggerDateFieldDO> {

    public FlowProcessTriggerDateFieldRepository() {
        super(FlowProcessTriggerDateFieldDO.class);
    }
}
