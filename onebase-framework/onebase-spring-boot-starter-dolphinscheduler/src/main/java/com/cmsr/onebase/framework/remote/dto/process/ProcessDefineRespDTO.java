package com.cmsr.onebase.framework.remote.dto.process;

import lombok.Data;

/** 流程定义简要信息 DTO */
@Data
public class ProcessDefineRespDTO {
    private Long code;
    private String name;
    private String description;
    private Integer version;
}

