package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:07
 */
@Setter
@Component
public class JobClient {

    public static final String JOB_EXECUTOR_INFO_TIME = "flow_process_time_job";

    public static final String JOB_EXECUTOR_INFO_DATE_FIELD = "flow_process_date_field_job";

    @Value("${onebase.scheduler.flow-project}")
    private Long flowProjectCode;

    @Value("${onebase.scheduler.flow-url}")
    private String flowUrl;

    @Value("${onebase.scheduler.flow-token}")
    private String flowToken;

    @Autowired
    private DolphinSchedulerClient dolphinSchedulerClient;

    public String startJob(Long processId, JobCreateRequest jobCreateRequest) {
        String flowName = String.valueOf(processId);
        Map<String, Object> body = new HashMap<>();
        body.put("processId", processId);
        body.put("token", flowToken);
        HttpTask httpTask = HttpTask.ofUrl(flowUrl)
                .method(HttpTask.HttpMethod.POST)
                .body(JsonUtils.toJsonString(body));
        Long jobId = dolphinSchedulerClient.createSingletonWorkflow(flowProjectCode, flowName, httpTask, null);
        Schedule schedule = new Schedule();
        schedule.setStartTime(LocalDateTime.parse(jobCreateRequest.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setEndTime(LocalDateTime.parse(jobCreateRequest.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setCrontab(jobCreateRequest.getCrontab());
        dolphinSchedulerClient.onlineWorkflowWithSchedule(flowProjectCode, jobId, schedule);
        return String.valueOf(jobId);
    }


    public String startJob(Long processId, String jobId, JobCreateRequest jobCreateRequest) {
        dolphinSchedulerClient.purgeWorkflow(flowProjectCode, Long.parseLong(jobId));
        return startJob(processId, jobId, jobCreateRequest);
    }

    public void deleteJob(Long processId) {
        Long jobId = dolphinSchedulerClient.queryWorkflowByName(flowProjectCode, String.valueOf(processId));
        dolphinSchedulerClient.purgeWorkflow(flowProjectCode, jobId);
    }

    public void deleteJob(String jobId) {
        dolphinSchedulerClient.purgeWorkflow(flowProjectCode, Long.parseLong(jobId));
    }

}
