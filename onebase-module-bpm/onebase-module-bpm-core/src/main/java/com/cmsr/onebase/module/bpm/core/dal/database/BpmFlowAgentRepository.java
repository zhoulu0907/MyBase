package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentDO;
import com.cmsr.onebase.module.bpm.core.enums.BpmAgentStatus;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
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

    public ConfigStore buildConditionByAgentStatus(BpmAgentStatus agentStatus) {
        DefaultConfigStore condition = new DefaultConfigStore();
        LocalDateTime now = LocalDateTime.now();

        switch (agentStatus) {
            case INACTIVE:
                // 待生效：当前时间 < 代理生效时间
                // 同时需要确保未被撤销
                condition.and(Compare.GREAT, "start_time", now);
                condition.and(Compare.NULL, "revoked_time");
                break;
            case ACTIVE:
                // 代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
                // 同时需要确保未被撤销
                condition.and(Compare.LESS_EQUAL, "start_time", now);
                condition.and(Compare.GREAT_EQUAL, "end_time", now);
                condition.and(Compare.NULL, "revoked_time");
                break;
            case EXPIRED:
                // 已失效：当前时间 > 代理失效时间
                // 同时需要确保未被撤销
                condition.and(Compare.LESS, "end_time", now);
                condition.and(Compare.NULL, "revoked_time");
                break;
            case REVOKED:
                // 已撤销：撤销时间不为空
                condition.and(Compare.NOT_NULL, "revoked_time");
                break;
            default:
                break;
        }

        return condition;
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
        configs.isNull(BpmFlowAgentDO.REVOKED_TIME);

        return findAllByConfig(configs);
    }

    /**
     * 查找与指定时间范围重叠的代理记录
     *
     * @param appId
     * @param principalId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<BpmFlowAgentDO> findAllOverlapAgent(Long appId, Long principalId, LocalDateTime startTime, LocalDateTime endTime) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(BpmFlowAgentDO.APP_ID, appId);
        configs.eq(BpmFlowAgentDO.PRINCIPAL_ID, principalId);

        // 记录开始时间 < 新记录结束时间 并且 记录结束时间 > 新记录开始时间
        configs.and(Compare.LESS, BpmFlowAgentDO.START_TIME, endTime);
        configs.and(Compare.GREAT, BpmFlowAgentDO.END_TIME, startTime);

        ConfigStore statusConfig = new DefaultConfigStore();

        // 待生效的记录
        statusConfig.or(buildConditionByAgentStatus(BpmAgentStatus.INACTIVE));

        // 已生效的记录
        statusConfig.or(buildConditionByAgentStatus(BpmAgentStatus.ACTIVE));

        configs.and(statusConfig);

        return findAllByConfig(configs);
    }
}
