package com.cmsr.onebase.module.bpm.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程代理表 实体类。
 *
 * @author liyang
 * @since 2025-11-29
 */
@Data
@Table("bpm_flow_agent")
public class BpmFlowAgentDO extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 被代理人用户ID（即委托人），代理关系的发起方
     */
    private String principalId;

    /**
     * 被代理人用户名称（即委托人）
     */
    private String principalName;

    /**
     * 代理人用户ID，接受委托代为处理流程任务
     */
    private String agentId;

    /**
     * 代理人用户名称
     */
    private String agentName;

    /**
     * 代理生效开始时间
     */
    private LocalDateTime startTime;

    /**
     * 代理结束时间，必须晚于开始时间
     */
    private LocalDateTime endTime;

    /**
     * 撤销人
     */
    private String revokerId;

    /**
     * 撤销时间
     */
    private LocalDateTime revokedTime;

    /**
     * 乐观锁
     */
    private Long lockVersion;
}
