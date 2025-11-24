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
 * ETL任务作业表 实体类。
 *
 * @author HuangJie
 * @since 2025-11-23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_schedule_job")
public class EtlScheduleJob extends BaseBizEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工作流Id
     */
    private Long workflowId;

    /**
     * 工作流调度编码
     */
    private String jobId;

    /**
     * 状态
     */
    private String jobStatus;

    /**
     * 最近一次执行时间
     */
    private Timestamp lastJobTime;

    /**
     * 最近一次成功执行时间
     */
    private Timestamp lastSuccessTime;

}
