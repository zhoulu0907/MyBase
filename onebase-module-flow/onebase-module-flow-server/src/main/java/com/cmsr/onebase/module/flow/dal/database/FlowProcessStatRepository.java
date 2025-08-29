package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
public class FlowProcessStatRepository extends DataRepository<FlowProcessDO> {

    public FlowProcessStatRepository() {
        super(FlowProcessDO.class);
    }
}
