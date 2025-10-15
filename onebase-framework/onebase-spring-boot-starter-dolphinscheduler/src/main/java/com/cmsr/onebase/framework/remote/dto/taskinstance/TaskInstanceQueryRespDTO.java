package com.cmsr.onebase.framework.remote.dto.taskinstance;

import lombok.Data;

/** 任务实例查询响应 DTO */
@Data
public class TaskInstanceQueryRespDTO {
    private Long id;
    private Long processInstanceId;
    private String name;
    private String state;
}

