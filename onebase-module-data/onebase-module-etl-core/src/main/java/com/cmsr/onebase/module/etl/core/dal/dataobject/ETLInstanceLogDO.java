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

@Table(name = "etl_instance_log")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ETLInstanceLogDO extends TenantBaseDO {

    @Column(name = "etl_id")
    private Long ETLId;

    @Column(name = "busi_date")
    private LocalDateTime busiDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "instance_trigger")
    private String instanceTrigger;

    @Column(name = "operate_user")
    private Long operateUser;

    @Column(name = "instance_status")
    private String instanceStatus;
}
