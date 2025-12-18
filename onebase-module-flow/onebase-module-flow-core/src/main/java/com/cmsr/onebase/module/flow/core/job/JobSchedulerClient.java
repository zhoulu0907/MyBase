package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:07
 */
@Slf4j
@Setter
@Component
public class JobSchedulerClient {

    public static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${onebase.scheduler.flow-project}")
    private Long flowProjectCode;

    @Value("${onebase.scheduler.flow-url}")
    private String flowUrl;

    @Autowired
    private DolphinSchedulerClient dolphinSchedulerClient;

    public String startJob(JobCreateRequest jobCreateRequest) {
        try {
            String flowName = jobCreateRequest.getFlowRemoteCallRequest().getApplicationId() + "-" + jobCreateRequest.getFlowRemoteCallRequest().getProcessId();
            HttpTask httpTask = HttpTask.ofUrl(flowUrl)
                    .method(HttpTask.HttpMethod.POST)
                    .body(JsonUtils.toJsonString(jobCreateRequest.getFlowRemoteCallRequest()));
            Long jobId = dolphinSchedulerClient.createSingletonHttpWorkflow(flowProjectCode, flowName, httpTask, jobCreateRequest.getFlowRemoteCallRequest().getProcessName());
            Schedule schedule = new Schedule();
            schedule.setStartTime(LocalDateTime.parse(jobCreateRequest.getStartTime(), DATETIME_FORMATTER));
            schedule.setEndTime(LocalDateTime.parse(jobCreateRequest.getEndTime(), DATETIME_FORMATTER));
            schedule.setCrontab(jobCreateRequest.getCrontab());
            dolphinSchedulerClient.onlineWorkflowWithSchedule(flowProjectCode, jobId, schedule);
            return String.valueOf(jobId);
        } catch (Exception e) {
            log.error("启动工作流失败: {}", jobCreateRequest, e);
            throw new RuntimeException("启动工作流失败: " + jobCreateRequest, e);
        }
    }

    private String createFlowName(Long applicationId, Long processId) {
        return applicationId + "-" + processId;
    }

    public void deleteJob(Long applicationId, Long processId) {
        try {
            String flowName = createFlowName(applicationId, processId);
            List<Long> jobIds = dolphinSchedulerClient.queryWorkflowCodeListByName(flowProjectCode, flowName);
            for (Long jobId : jobIds) {
                dolphinSchedulerClient.purgeWorkflow(flowProjectCode, jobId);
            }
        } catch (Exception e) {
            log.error("删除工作流失败: {}", applicationId, e);
            throw new RuntimeException("删除工作流失败: " + applicationId, e);
        }
    }

    public void deleteJob(Long applicationId) {
        try {
            String flowName = String.valueOf(applicationId);
            List<Long> jobIds = dolphinSchedulerClient.queryWorkflowCodeListByName(flowProjectCode, flowName);
            for (Long jobId : jobIds) {
                dolphinSchedulerClient.purgeWorkflow(flowProjectCode, jobId);
            }
        } catch (Exception e) {
            log.error("删除工作流失败: {}", applicationId, e);
            throw new RuntimeException("删除工作流失败: " + applicationId, e);
        }
    }

}
