package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.mybatis.BaseBizEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("etl_workflow")
public class ETLWorkflowDO extends BaseBizEntity {

    @Column("workflow_name")
    private String workflowName;

    @Column("declaration")
    private String declaration;

    @Column("config")
    private String config;

    @Column("is_enabled")
    private Integer isEnabled;

    // FIXED, OBSERVE, MANUALLY(default)
    @Column("schedule_strategy")
    private String scheduleStrategy;

    @Column("schedule_config")
    private String scheduleConfig;

    public Boolean isEnabled() {
        return isEnabled == 1;
    }

    public void setConfig(JsonNode config) {
        this.config = JsonUtils.toJsonString(config);
    }
}
