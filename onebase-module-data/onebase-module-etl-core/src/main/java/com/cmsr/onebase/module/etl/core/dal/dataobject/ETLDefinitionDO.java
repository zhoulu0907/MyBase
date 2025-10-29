package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Table(name = "etl_definition")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ETLDefinitionDO extends TenantBaseDO {

    @Column(name = "etl_name")
    private String ETLName;

    // FIXED, OBSERVE, MANUALLY(default)
    @Column(name = "schedule_strategy")
    private String scheduleStrategy;

    @Column(name = "job_status")
    private String jobStatus;

    @Column(name = "last_job_time")
    private LocalDateTime lastJobTime;

    @Column(name = "last_success_time")
    private LocalDateTime lastSuccessTime;

    @Column(name = "config")
    private String config;

    @Column(name = "schedule_config")
    private String scheduleConfig;

    @Column(name = "workflow_code")
    private String workflowCode;

}
