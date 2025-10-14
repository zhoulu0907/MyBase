package com.cmsr.onebase.module.flow.core.job;

import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:07
 */
@Setter
@Component
public class JobClient {

    public static final String JOB_EXECUTOR_INFO_TIME = "flow_process_time_job";

    public static final String JOB_EXECUTOR_INFO_DATE_FIELD = "flow_process_date_field_job";

    public String startJob(Long processId, JobCreateRequest jobCreateRequest) {
        return null;
    }


    public String startJob(Long processId, String jobId, JobCreateRequest jobCreateRequest) {
        Long jobIdLong = NumberUtils.toLong(jobId, 0);
        if (jobIdLong == 0) {
            return startJob(processId, jobCreateRequest);
        }
        return null;
    }

    public void deleteJob(String jobId) {

    }

}
