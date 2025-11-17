package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeTypeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FlowNodeTypeRepository extends DataRepository<FlowNodeTypeDO> {
    public FlowNodeTypeRepository() {
        super(FlowNodeTypeDO.class);
    }
}
