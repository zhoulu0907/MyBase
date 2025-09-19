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
import com.cmsr.onebase.module.flow.core.enums.JsonGraphConstant;
import com.cmsr.onebase.module.flow.core.graph.data.StartTimeNodeData;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:07
 */
@Setter
@Component
public class JobClient {

    private static final String JOB_EXECUTOR_INFO = "flow_process_time_job";

    public String startJob(Long processId, StartTimeNodeData timeNodeData) {
        ClusterAddHandler clusterJob = SnailJobOpenApi.addClusterJob();
        clusterJob.setJobName(processId.toString());
        settingParams(clusterJob, timeNodeData);
        clusterJob.addArgsStr(JsonGraphConstant.PROCESS_ID, String.valueOf(processId));
        clusterJob.setRouteKey(AllocationAlgorithmEnum.ROUND);
        clusterJob.setBlockStrategy(JobBlockStrategyEnum.OVERLAY);
        return String.valueOf(clusterJob.execute());
    }

    public String startJob(Long processId, String jobId, StartTimeNodeData timeNodeData) {
        JobApiResponse jobDetail = SnailJobOpenApi.getJobDetail(Long.parseLong(jobId)).execute();
        if (jobDetail.getId() == null) {
            return startJob(processId, timeNodeData);
        } else {
            ClusterUpdateHandler clusterJob = SnailJobOpenApi.updateClusterJob(jobDetail.getId());
            settingParams(clusterJob, timeNodeData);
            clusterJob.addArgsStr(JsonGraphConstant.PROCESS_ID, String.valueOf(processId));
            clusterJob.execute();
            return String.valueOf(jobDetail.getId());
        }
    }

    private void settingParams(AbstractParamsHandler clusterJob, StartTimeNodeData timeNodeData) {
        clusterJob.setJobStatus(StatusEnum.YES);
        clusterJob.setExecutorInfo(JOB_EXECUTOR_INFO);
        if (timeNodeData.getRepeatType().equals(StartTimeNodeData.REPEAT_TYPE_CRON)) {
            clusterJob.setTriggerType(TriggerTypeEnum.CRON);
            clusterJob.setTriggerInterval(timeNodeData.getCronExpression());
        } else if (timeNodeData.getRepeatType().equals(StartTimeNodeData.REPEAT_TYPE_NONE)) {
            clusterJob.setTriggerType(TriggerTypeEnum.POINT_IN_TIME);
            LocalDateTime localDateTime = LocalDateTime.parse(timeNodeData.getTriggerDatetime(), JsonGraphConstant.DATE_TIME_FORMATTER);
            clusterJob.setTriggerTime(Set.of(localDateTime));
        } else {
            clusterJob.setTriggerType(TriggerTypeEnum.CRON);
            clusterJob.setTriggerInterval(timeNodeData.createCronExpression());
        }
        clusterJob.setExecutorTimeout(1800);
        clusterJob.setMaxRetryTimes(3);
        clusterJob.setRetryInterval(300);
    }

    public void stopJob(String jobId) {
        SnailJobOpenApi
                .updateClusterJob(Long.parseLong(jobId))
                .setJobStatus(StatusEnum.NO)
                .execute();
    }

    public void deleteJob(String jobId) {
        stopJob(jobId);
        SnailJobOpenApi
                .deleteJob(Set.of(Long.parseLong(jobId)))
                .execute();
    }


}
