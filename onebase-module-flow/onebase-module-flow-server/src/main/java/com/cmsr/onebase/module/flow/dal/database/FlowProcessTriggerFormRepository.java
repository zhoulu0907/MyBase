package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerFormDO;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:38
 */
public class FlowProcessTriggerFormRepository extends DataRepository<FlowProcessTriggerFormDO> {

    public FlowProcessTriggerFormRepository() {
        super(FlowProcessTriggerFormDO.class);
    }
}
