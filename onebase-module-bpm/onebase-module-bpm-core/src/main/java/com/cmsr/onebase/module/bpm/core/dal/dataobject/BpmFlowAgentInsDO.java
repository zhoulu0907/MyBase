package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 代理关系实例表 实体类。
 *
 * @author liyang
 * @since 2025-11-28
 */
@Data
@Table("bpm_flow_agent_ins")
public class BpmFlowAgentInsDO extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 被代理人ID
     */
    private Long principalId;

    /**
     * 被代理人用户名称
     */
    private String principalName;

    /**
     * 代理人ID
     */
    private Long agentId;

    /**
     * 代理人用户名称
     */
    private String agentName;

    /**
     * 是否执行人：0=未操作, 1=执行人
     */
    private Integer isExecutor;

    /**
     * 租户id
     */
    private Long tenantId;

}
