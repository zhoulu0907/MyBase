package com.cmsr.onebase.module.flow.core.job;

import com.aizuda.snailjob.client.job.core.enums.AllocationAlgorithmEnum;
import com.aizuda.snailjob.client.job.core.enums.TriggerTypeEnum;
import com.aizuda.snailjob.client.job.core.handler.AbstractParamsHandler;
import com.aizuda.snailjob.client.job.core.handler.add.ClusterAddHandler;
import com.aizuda.snailjob.client.job.core.handler.update.ClusterUpdateHandler;
import com.aizuda.snailjob.client.job.core.openapi.SnailJobOpenApi;
import com.aizuda.snailjob.common.core.enums.JobBlockStrategyEnum;
import com.aizuda.snailjob.common.core.enums.StatusEnum;
import com.aizuda.snailjob.model.response.JobApiResponse;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

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
        ClusterAddHandler clusterJob = SnailJobOpenApi.addClusterJob();
        clusterJob.setJobName(processId.toString());
        clusterJob.setJobStatus(StatusEnum.YES);
        clusterJob.addArgsStr(JsonGraphConstant.PROCESS_ID, String.valueOf(processId));
        clusterJob.setRouteKey(AllocationAlgorithmEnum.ROUND);
        clusterJob.setBlockStrategy(JobBlockStrategyEnum.OVERLAY);
        clusterJob.setExecutorTimeout(1800);
        clusterJob.setMaxRetryTimes(3);
        clusterJob.setRetryInterval(300);
        //
        settingParams(clusterJob, jobCreateRequest);
        return String.valueOf(clusterJob.execute());
    }

    private void settingParams(AbstractParamsHandler clusterJob, JobCreateRequest jobCreateRequest) {
        if (jobCreateRequest.getTriggerType() == TriggerTypeEnum.POINT_IN_TIME) {
            clusterJob.setTriggerType(TriggerTypeEnum.POINT_IN_TIME);
            clusterJob.setTriggerTime(jobCreateRequest.getTriggerTime());
        } else {
            clusterJob.setTriggerType(TriggerTypeEnum.CRON);
            clusterJob.setTriggerInterval(jobCreateRequest.getTriggerInterval());
        }
        clusterJob.setExecutorInfo(jobCreateRequest.getExecutorInfo());
    }

    public String startJob(Long processId, String jobId, JobCreateRequest jobCreateRequest) {
        Long jobIdLong = NumberUtils.toLong(jobId, 0);
        if (jobIdLong == 0) {
            return startJob(processId, jobCreateRequest);
        }
        JobApiResponse jobDetail = SnailJobOpenApi.getJobDetail(Long.parseLong(jobId)).execute();
        if (jobDetail.getId() == null) {
            return startJob(processId, jobCreateRequest);
        } else {
            ClusterUpdateHandler clusterJob = SnailJobOpenApi.updateClusterJob(jobDetail.getId());
            settingParams(clusterJob, jobCreateRequest);
            clusterJob.addArgsStr(JsonGraphConstant.PROCESS_ID, String.valueOf(processId));
            clusterJob.execute();
            return String.valueOf(jobDetail.getId());
        }
    }

    public void deleteJob(String jobId) {
        SnailJobOpenApi
                .updateClusterJob(Long.parseLong(jobId))
                .setJobStatus(StatusEnum.NO)
                .execute();
        SnailJobOpenApi
                .deleteJob(Set.of(Long.parseLong(jobId)))
                .execute();
    }


}
