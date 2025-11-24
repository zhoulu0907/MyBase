package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BPM流程代理实例数据访问层
 *
 * @author liyang
 * @date 2025-11-22
 */
@Repository
public class BpmFlowAgentInsRepository extends DataRepository<BpmFlowAgentInsDO> {
    public BpmFlowAgentInsRepository() {
        super(BpmFlowAgentInsDO.class);
    }

    public BpmFlowAgentInsDO findOneByTaskIdAndAgentId(Long taskId, String agentId) {
        ConfigStore configs = new DefaultConfigStore();

        configs.eq(BpmFlowAgentInsDO.TASK_ID, taskId);
        configs.eq(BpmFlowAgentInsDO.AGENT_ID, agentId);

        return findOne(configs);
    }

    public List<BpmFlowAgentInsDO> findAllByTaskIdAndAgentId(Long taskId, String agentId) {
        ConfigStore configs = new DefaultConfigStore();

        configs.eq(BpmFlowAgentInsDO.TASK_ID, taskId);
        configs.eq(BpmFlowAgentInsDO.AGENT_ID, agentId);

        return findAllByConfig(configs);
    }
}
