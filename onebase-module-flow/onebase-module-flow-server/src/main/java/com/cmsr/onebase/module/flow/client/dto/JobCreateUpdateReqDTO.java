package com.cmsr.onebase.module.flow.client.dto;

import com.cmsr.onebase.module.flow.client.enums.JobScheduleTypeEnum;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:08
 */
@Data
public class JobCreateUpdateReqDTO {

    private Long processId;

    private JobScheduleTypeEnum scheduleType;

    private String scheduleValue;

    private String executorParam;

}
