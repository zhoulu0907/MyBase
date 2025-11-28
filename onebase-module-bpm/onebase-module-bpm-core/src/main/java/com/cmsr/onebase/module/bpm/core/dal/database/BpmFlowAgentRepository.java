package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentDO;
import com.cmsr.onebase.module.bpm.core.dal.mapper.BpmFlowAgentMapper;
import com.cmsr.onebase.module.bpm.core.enums.BpmAgentStatus;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.module.bpm.core.dal.dataobject.table.BpmFlowAgentTableDef.BPM_FLOW_AGENT;


/**
 * BPM流程代理仓库
 *
 * @author liyang
 * @date 2025-11-10
 */
@Repository
public class BpmFlowAgentRepository extends ServiceImpl<BpmFlowAgentMapper, BpmFlowAgentDO> {
    public QueryCondition buildConditionByAgentStatus(BpmAgentStatus agentStatus) {
        QueryCondition condition = QueryCondition.createEmpty();
        LocalDateTime now = LocalDateTime.now();

        switch (agentStatus) {
            case INACTIVE:
                // 待生效：当前时间 < 代理生效时间
                // 同时需要确保未被撤销
                condition.and(BPM_FLOW_AGENT.START_TIME.gt(now));
                condition.and(BPM_FLOW_AGENT.REVOKED_TIME.isNull());
                break;
            case ACTIVE:
                // 代理中：当前时间 >= 代理生效时间 且 当前时间 <= 代理失效时间
                // 同时需要确保未被撤销
                condition.and(BPM_FLOW_AGENT.START_TIME.le(now));
                condition.and(BPM_FLOW_AGENT.END_TIME.ge(now));
                condition.and(BPM_FLOW_AGENT.REVOKED_TIME.isNull());
                break;
            case EXPIRED:
                // 已失效：当前时间 > 代理失效时间
                // 同时需要确保未被撤销
                condition.and(BPM_FLOW_AGENT.END_TIME.lt(now));
                condition.and(BPM_FLOW_AGENT.REVOKED_TIME.isNull());
                break;
            case REVOKED:
                // 已撤销：撤销时间不为空
                condition.and(BPM_FLOW_AGENT.REVOKED_TIME.isNotNull());
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
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (principalIds.size() == 1) {
            queryWrapper.eq(BpmFlowAgentDO::getPrincipalId, principalIds.iterator().next());
        } else {
            queryWrapper.in(BpmFlowAgentDO::getPrincipalId, principalIds);
        }

        queryWrapper.eq(BpmFlowAgentDO::getAppId, appId);
        queryWrapper.ge(BpmFlowAgentDO::getEndTime, LocalDateTime.now());
        queryWrapper.le(BpmFlowAgentDO::getStartTime, LocalDateTime.now());
        queryWrapper.isNull(BpmFlowAgentDO::getRevokedTime);

        return list(queryWrapper);
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
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BpmFlowAgentDO::getAppId, appId);
        queryWrapper.eq(BpmFlowAgentDO::getPrincipalId, principalId);

        // 记录开始时间 < 新记录结束时间 并且 记录结束时间 > 新记录开始时间
        queryWrapper.le(BpmFlowAgentDO::getStartTime, endTime);
        queryWrapper.gt(BpmFlowAgentDO::getEndTime, startTime);

        QueryCondition statusConfig = QueryCondition.createEmpty();

        // 待生效的记录
        statusConfig.or(buildConditionByAgentStatus(BpmAgentStatus.INACTIVE));

        // 已生效的记录
        statusConfig.or(buildConditionByAgentStatus(BpmAgentStatus.ACTIVE));

        queryWrapper.and(statusConfig);

        return list(queryWrapper);
    }
}
