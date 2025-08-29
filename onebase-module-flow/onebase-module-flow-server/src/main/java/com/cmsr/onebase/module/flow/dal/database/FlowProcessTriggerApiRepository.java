package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerApiDO;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:38
 */
public class FlowProcessTriggerApiRepository extends DataRepository<FlowProcessTriggerApiDO> {

    public FlowProcessTriggerApiRepository() {
        super(FlowProcessTriggerApiDO.class);
    }
}
