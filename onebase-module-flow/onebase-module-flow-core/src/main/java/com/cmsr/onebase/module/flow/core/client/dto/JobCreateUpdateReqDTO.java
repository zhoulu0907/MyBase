package com.cmsr.onebase.module.flow.core.client.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:08
 */
@Data
public class JobCreateUpdateReqDTO {

    private Long processId;

    private JobScheduleTypeEnum scheduleType;

    private String scheduleConf;

    private String executorParam;


}
