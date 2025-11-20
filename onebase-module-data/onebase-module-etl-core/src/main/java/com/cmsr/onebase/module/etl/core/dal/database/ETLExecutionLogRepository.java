package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
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

    public PageResult<ETLExecutionLogDO> queryPage(Long applicationId, Long workflowId, Integer pageNo, Integer pageSize) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", applicationId);
        if (workflowId != null) {
            cs.eq("workflow_id", workflowId);
        }
        cs.order("create_time", Order.TYPE.DESC);
        cs.order("update_time", Order.TYPE.DESC);
        return findPageWithConditions(cs, pageNo, pageSize);
    }
}
