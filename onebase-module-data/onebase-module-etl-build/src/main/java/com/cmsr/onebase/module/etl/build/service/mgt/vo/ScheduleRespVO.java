package com.cmsr.onebase.module.etl.build.service.mgt.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ScheduleRespVO {

    private Long applicationId;

    private Long workflowId;

    private String flowName;

    private Integer enableStatus;

    private String scheduleStrategy;

    private String config;

    public JsonNode getConfig() {
        if (StringUtils.isBlank(this.config)) {
            return JsonUtils.createObjectNode();
        }
        return JsonUtils.parseTree(config);
    }
}
