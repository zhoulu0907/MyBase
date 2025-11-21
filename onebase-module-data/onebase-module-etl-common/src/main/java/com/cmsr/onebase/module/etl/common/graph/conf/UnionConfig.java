package com.cmsr.onebase.module.etl.common.graph.conf;

import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnionConfig extends NodeConfig {

    private List<ColumnMapping> colTitles;

    @Data
    public static class ColumnMapping {

        private String fieldFqn;

        private String fieldName;

        private String fieldType;

    }
}
