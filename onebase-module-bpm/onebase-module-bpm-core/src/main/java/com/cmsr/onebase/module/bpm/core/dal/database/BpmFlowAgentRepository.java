package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * BPM流程代理仓库
 *
 * @author liyang
 * @date 2025-11-10
 */
@Repository
public class BpmFlowAgentRepository extends DataRepository<BpmFlowAgentDO> {
    public BpmFlowAgentRepository() {
        super(BpmFlowAgentDO.class);
    }

    /**
     * 根据被代理人ID查询有效代理记录
     *
     * @param principalId 被代理人ID
     * @return 代理记录列表
     */
    public List<BpmFlowAgentDO> findAllActiveAgent(Long appId, Long principalId) {
       return findAllActiveAgent(appId, List.of(principalId));
    }

    public List<BpmFlowAgentDO> findAllActiveAgent(Long appId, Collection<Long> principalIds) {
        ConfigStore configs = new DefaultConfigStore();

        if (principalIds.size() == 1) {
            configs.in(BpmFlowAgentDO.PRINCIPAL_ID, principalIds);
        } else {
            configs.in(BpmFlowAgentDO.PRINCIPAL_ID, principalIds);
        }

        configs.eq(BpmFlowAgentDO.APP_ID, appId);

        configs.ge(BpmFlowAgentDO.END_TIME, LocalDateTime.now());
        configs.le(BpmFlowAgentDO.START_TIME, LocalDateTime.now());

        return findAllByConfig(configs);
    }
}
