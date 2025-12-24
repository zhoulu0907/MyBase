package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import com.cmsr.onebase.framework.ds.model.workflow.WorkflowDefinitionResp;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Pair<Long, Long> splitFlowName(String flowName) {
        String[] split = StringUtils.split(flowName, "-");
        if (split == null || split.length != 2) {
            return Pair.of(0L, 0L);
        }
        return Pair.of(Long.parseLong(split[0]), Long.parseLong(split[1]));
    }


    private Set<Long> extractJobIds(Long applicationId, List<WorkflowDefinitionResp> workflows) {
        Set<Long> jobIds = new HashSet<>();
        for (WorkflowDefinitionResp workflow : workflows) {
            String name = workflow.getName();
            if (name == null) {
                continue;
            }
            Pair<Long, Long> idid = splitFlowName(name);
            if (idid.getLeft().equals(applicationId)) {
                jobIds.add(workflow.getCode());
            }
        }
        return jobIds;
    }


    public void deleteJob(Long applicationId, Long processId) {
        try {
            String flowName = createFlowName(applicationId, processId);
            List<WorkflowDefinitionResp> workflows = dolphinSchedulerClient.queryWorkflowListByName(flowProjectCode, flowName);
            Set<Long> jobIds = extractJobIds(applicationId, workflows);
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
            List<WorkflowDefinitionResp> workflows = dolphinSchedulerClient.queryWorkflowListByName(flowProjectCode, flowName);
            Set<Long> jobIds = extractJobIds(applicationId, workflows);
            for (Long jobId : jobIds) {
                dolphinSchedulerClient.purgeWorkflow(flowProjectCode, jobId);
            }
        } catch (Exception e) {
            log.error("删除工作流失败: {}", applicationId, e);
            throw new RuntimeException("删除工作流失败: " + applicationId, e);
        }
    }

    public Set<Long> queryProcessIds(Long applicationId) {
        try {
            String flowName = String.valueOf(applicationId);
            List<String> flowNames = dolphinSchedulerClient.queryWorkflowNameListByName(flowProjectCode, flowName);
            Set<Long> processIds = new HashSet<>();
            for (String name : flowNames) {
                Pair<Long, Long> idid = splitFlowName(name);
                if (idid.getLeft().equals(applicationId)) {
                    processIds.add(idid.getRight());
                }
            }
            return processIds;
        } catch (Exception e) {
            log.error("查询工作流失败: {}", applicationId, e);
            throw new RuntimeException("查询工作流失败: " + applicationId, e);
        }
    }

}
