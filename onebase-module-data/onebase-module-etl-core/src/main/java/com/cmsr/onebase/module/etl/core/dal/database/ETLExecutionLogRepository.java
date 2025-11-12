package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLExecutionLogRepository extends DataRepository<ETLExecutionLogDO> {
    public ETLExecutionLogRepository() {
        super(ETLExecutionLogDO.class);
    }

    public void deleteByWorkflowId(Long workflowId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("workflow_id", workflowId);
        deleteByConfig(cs);
    }
}
