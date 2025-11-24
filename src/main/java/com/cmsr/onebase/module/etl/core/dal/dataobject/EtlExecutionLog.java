package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import java.io.Serial;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ETL实例日志表 实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_execution_log")
public class EtlExecutionLog extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工作流Id
     */
    private Long workflowId;

    /**
     * 计划执行日期
     */
    private Timestamp businessDate;

    /**
     * 开始时间
     */
    private Timestamp startTime;

    /**
     * 结束时间
     */
    private Timestamp endTime;

    /**
     * 执行时间（毫秒）
     */
    private Long durationTime;

    /**
     * 触发类型
     */
    private String triggerType;

    /**
     * 触发用户
     */
    private Long triggerUser;

    /**
     * 任务状态
     */
    private String taskStatus;

}
