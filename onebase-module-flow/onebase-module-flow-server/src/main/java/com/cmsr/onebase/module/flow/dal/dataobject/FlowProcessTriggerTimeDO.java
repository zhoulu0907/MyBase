package com.cmsr.onebase.module.flow.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "flow_process_trigger_time")
public class FlowProcessTriggerTimeDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @Column(name = "id", length = 19, nullable = false)
    private Long id;
    /**
     * 流程ID
     */
    @Column(name = "process_id", length = 19, nullable = false)
    private Long processId;
    /**
     * 节点ID
     */
    @Column(name = "node_id", length = 64, nullable = false)
    private String nodeId;
    /**
     * 触发模式,一次性触发/重复性触发
     */
    @Column(name = "trigger_mode", length = 32, nullable = false)
    private String triggerMode;
    /**
     * 重复周期，每天 每周 每月 每年
     */
    @Column(name = "repeat_type", length = 64, nullable = false)
    private String repeatType;
    /**
     * 重复月，1-12
     */
    @Column(name = "repeat_month", length = 64)
    private String repeatMonth;
    /**
     * 重复周，1-52
     */
    @Column(name = "repeat_week", length = 64)
    private String repeatWeek;
    /**
     * 重复日，1-31
     */
    @Column(name = "repeat_day", length = 64)
    private String repeatDay;
    /**
     * 重复小时，0-23
     */
    @Column(name = "repeat_hour", length = 64)
    private String repeatHour;
    /**
     * 重复分钟，0-59
     */
    @Column(name = "repeat_minute", length = 64)
    private String repeatMinute;
    /**
     * CRON 表达式
     */
    @Column(name = "cron_expression", length = 128, nullable = false)
    private String cronExpression;
    /**
     * 定时任务生效的起始时间
     */
    @Column(name = "start_time", length = 29)
    private LocalDateTime startTime;
    /**
     * 定时任务失效的截止时间
     */
    @Column(name = "end_time", length = 29)
    private LocalDateTime endTime;
    /**
     * 延迟执行秒数
     */
    @Column(name = "delay_seconds", length = 10)
    private Integer delaySeconds;
    /**
     * 是否并发执行
     */
    @Column(name = "is_concurrent_exec_allowed", length = 5)
    private Integer isConcurrentExecAllowed;

    /**
     * 任务ID
     */
    @Column(name = "job_id", length = 64)
    private String jobId;

}