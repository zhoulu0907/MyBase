package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 流程代理实例表
 *
 * @author liyang
 * @date 2025-11-22
 */
@Data
@Table(name = "bpm_flow_agent_ins")
public class BpmFlowAgentInsDO extends TenantBaseDO {
    public static final String PRINCIPAL_ID = "principal_id";

    public static final String AGENT_ID = "agent_id";

    public static final String AGENT_NAME = "agent_name";

    public static final String PRINCIPAL_NAME = "principal_name";

    /**
     * 被代理人用户ID
     */
    @Column(name = PRINCIPAL_ID)
    private Long principalId;

    /**
    *被代理人用户名称
     */
    @Column(name = PRINCIPAL_NAME)
    private String principalName;

    /**
     * 代理人用户ID
     */
    @Column(name = AGENT_ID)
    private Long agentId;

    /**
     * 代理人用户名称
     */
    @Column(name = AGENT_NAME)
    private String agentName;

    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 流程实例ID
     */
    @Column(name = "instance_id")
    private Long instanceId;

    /**
     * 是否执行者
     */
    @Column(name = "is_executor")
    private Integer isExecutor;
}
