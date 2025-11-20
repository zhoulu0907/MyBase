package com.cmsr.onebase.module.etl.common.graph.conf;

import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SqlConfig extends NodeConfig {
    private String sqlValue;
}
