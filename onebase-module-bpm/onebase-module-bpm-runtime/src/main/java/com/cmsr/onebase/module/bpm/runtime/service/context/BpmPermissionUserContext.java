package com.cmsr.onebase.module.bpm.runtime.service.context;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.warm.flow.core.entity.User;

/**
 *
 *
 *
 * @author liyang
 * @date 2025-11-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BpmPermissionUserContext {

    /**
     * 匹配的用户（审批人/转办人/委派人）
     */
    private User matchedUser;

    /**
     * 代理人记录（如果当前用户是代理人，则不为null）
     */
    private BpmFlowAgentInsDO agentIns;
}

