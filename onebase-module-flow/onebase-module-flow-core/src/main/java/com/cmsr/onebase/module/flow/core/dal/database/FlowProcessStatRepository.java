package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessStatRepository extends DataRepository<FlowProcessDO> {

    public FlowProcessStatRepository() {
        super(FlowProcessDO.class);
    }
}
