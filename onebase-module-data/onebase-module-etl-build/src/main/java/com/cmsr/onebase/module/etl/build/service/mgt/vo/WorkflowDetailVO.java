package com.cmsr.onebase.module.etl.build.service.mgt.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class WorkflowDetailVO {

    private Long id;

    private String flowName;

    private String config;

    public JsonNode getConfig() {
        return JsonUtils.parseTree(this.config);
    }
}
