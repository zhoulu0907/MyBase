package com.cmsr.onebase.module.flow.core.dal.database.connector;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.connector.FlowConnectorScriptDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class FlowConnectorScriptRepository extends DataRepository<FlowConnectorScriptDO> {
    public FlowConnectorScriptRepository() {
        super(FlowConnectorScriptDO.class);
    }
}
