package com.cmsr.onebase.module.flow.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process")
public class FlowProcessDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @Column(name = "id", length = 19, nullable = false)
    private Long id;
    /**
     * 应用ID
     */
    @Column(name = "application_id", length = 19, nullable = false)
    private Long applicationId;
    /**
     * 流程名称
     */
    @Column(name = "process_name", length = 128, nullable = false)
    private String processName;
    /**
     * 状态（编辑、发布）
     */
    @Column(name = "process_status", length = 5, nullable = false)
    private Integer processStatus;
    /**
     * 描述
     */
    @Column(name = "process_description", length = 1024)
    private String processDescription;
    /**
     * 流程json
     */
    @Column(name = "process_definition", length = 2147483647, nullable = false)
    private String processDefinition;
    /**
     * 流程类型，如表达触发，定时触发，API触发等
     */
    @Column(name = "trigger_type", length = 64, nullable = false)
    private String triggerType;

}