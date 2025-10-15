package com.cmsr.onebase.framework.remote.dto.instance;

import lombok.Data;

/** 流程实例查询响应 DTO */
@Data
public class ProcessInstanceQueryRespDTO {
    private Long id;
    private Long processDefinitionCode;
    private String processDefinitionName;
    private String state;
}

