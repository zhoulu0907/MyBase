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
 *  实体类。
 *
 * @author v1endr3
 * @since 2025-11-22
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Table("etl_execution_log")
public class EtlExecutionLog extends TenantBaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long applicationId;

    private Long workflowId;

    private Timestamp businessDate;

    private Timestamp startTime;

    private Timestamp endTime;

    private Long durationTime;

    private String triggerType;

    private Long triggerUser;

    private String taskStatus;

    private String errorMessage;

    private Long deleted;

    private Long creator;

    private Timestamp createTime;

    private Long updater;

    private Timestamp updateTime;

    private Integer lockVersion;

}
