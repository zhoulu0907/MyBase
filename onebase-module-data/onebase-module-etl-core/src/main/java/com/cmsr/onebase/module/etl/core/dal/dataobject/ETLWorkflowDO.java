package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "etl_workflow")
@com.mybatisflex.annotation.Table("etl_workflow")
public class ETLWorkflowDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "workflow_name")
    private String workflowName;

    @Column(name = "declaration")
    private String declaration;

    @Column(name = "config")
    private String config;

    @Column(name = "is_enabled")
    private Integer isEnabled;

    // FIXED, OBSERVE, MANUALLY(default)
    @Column(name = "schedule_strategy")
    private String scheduleStrategy;

    @Column(name = "schedule_config")
    private String scheduleConfig;

    public Boolean isEnabled() {
        return isEnabled == 1;
    }

    public void setConfig(JsonNode config) {
        this.config = JsonUtils.toJsonString(config);
    }
}
