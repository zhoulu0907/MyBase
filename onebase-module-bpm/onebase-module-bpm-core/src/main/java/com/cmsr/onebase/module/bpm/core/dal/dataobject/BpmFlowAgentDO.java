package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程代理表
 *
 * @author liyang
 * @date 2025-11-10
 */
@Data
@Table(name = "bpm_flow_agent")
public class BpmFlowAgentDO extends TenantBaseDO {
    public static final String PRINCIPAL_ID = "principal_id";

    public static final String AGENT_ID = "agent_id";

    public static final String APP_ID = "app_id";

    public static final String START_TIME = "start_time";

    public static final String END_TIME = "end_time";

    public static final String REVOKED_TIME = "revoked_time";

    public static final String AGENT_NAME = "agent_name";

    public static final String PRINCIPAL_NAME = "principal_name";

    public static final String REVOKED_ID = "revoked_id";

    /**
     * 应用ID
     */
    @Column(name = APP_ID, length = 100)
    private Long appId;

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
     * 代理开始时间
     */
    @Column(name = START_TIME)
    private LocalDateTime startTime;

    /**
     * 代理结束时间
     */
    @Column(name = END_TIME)
    private LocalDateTime endTime;

    /**
     * 撤销人ID
     */
    @Column(name = REVOKED_ID)
    private Long revokerId;

    /**
     * 撤销时间
     */
    @Column(name = REVOKED_TIME)
    private LocalDateTime revokedTime;
}
