package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "flow_process_stat")
public class FlowProcessStatDO extends TenantBaseDO {
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
     * 上次执行时间
     */
    @Column(name = "last_exec_time", length = 29)
    private LocalDateTime lastExecTime;
    /**
     * 执行次数
     */
    @Column(name = "exec_count", length = 19, nullable = false)
    private Long execCount;
}