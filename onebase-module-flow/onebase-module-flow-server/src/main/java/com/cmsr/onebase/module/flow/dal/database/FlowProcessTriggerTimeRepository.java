package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerTimeDO;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:39
 */
public class FlowProcessTriggerTimeRepository extends DataRepository<FlowProcessTriggerTimeDO> {

    public FlowProcessTriggerTimeRepository() {
        super(FlowProcessTriggerTimeDO.class);
    }
}
