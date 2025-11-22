package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.mybatisflex.annotation.Id;
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
 * @author v1endr3
 * @since 2025-11-22
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_schedule_job")
public class EtlScheduleJob extends TenantBaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @Id
    private Long id;

    /**
     * 应用Id
     */
    private Long applicationId;

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

    /**
     * 是否删除（逻辑删除）
     */
    private Long deleted;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 乐观锁版本
     */
    private Integer lockVersion;

}
