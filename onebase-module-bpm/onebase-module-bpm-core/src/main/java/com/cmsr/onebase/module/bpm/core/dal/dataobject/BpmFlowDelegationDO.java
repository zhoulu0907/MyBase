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
@Table(name = "bpm_flow_delegation")
public class BpmFlowDelegationDO extends TenantBaseDO {
    /**
     * 应用ID
     */
    @Column(name = "app_id", length = 100)
    private Long appId;

    /**
     * 发起人ID
     */
    @Column(name = "principal_id")
    private Long principalId;

    /**
     * 发起人名称（冗余字段，方便查询显示）
     */
    @Column(name = "delegate_id")
    private Long delegateId;

    /**
     * 代理开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 代理结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 撤销人ID
     */
    @Column(name = "revoker_id")
    private Long revokerId;

    /**
     * 撤销时间
     */
    @Column(name = "revoked_time")
    private LocalDateTime revokedTime;
}
