package com.cmsr.onebase.module.flow.core.job;

import com.aizuda.snailjob.client.job.core.enums.TriggerTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/22 12:52
 */
@Data
public class JobCreateRequest {

    private TriggerTypeEnum triggerType;

    private String triggerInterval;

    private Set<LocalDateTime> triggerTime;

    private String executorInfo;
}
