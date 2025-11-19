package com.cmsr.onebase.module.flow.build.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectorScriptVO {

    private Long scriptId;

    private Long connectorId;

    private String scriptName;

    private String scriptType;

    private String description;

    private String rawScript;

    private String inputParameter;

    private String outputParameter;

    private LocalDateTime createTime;

}
