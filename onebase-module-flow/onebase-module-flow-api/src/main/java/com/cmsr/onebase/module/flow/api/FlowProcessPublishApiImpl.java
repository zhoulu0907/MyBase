package com.cmsr.onebase.module.flow.api;

import com.aizuda.snailjob.client.job.core.enums.TriggerTypeEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowBuildCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowPublishStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.event.FlowEventPublisher;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.job.JobClient;
import com.cmsr.onebase.module.flow.core.job.JobCreateRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/19 14:28
 */
@Setter
@Service
public class FlowProcessPublishApiImpl implements FlowProcessPublishApi {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Autowired
    private JobClient jobClient;

    @Autowired
    private FlowEventPublisher flowEventPublisher;

    @Override
    public void onlineApplicationFlowProcess(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus());
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            startJob(flowProcessDO);
            flowProcessDO.setPublishStatus(FlowPublishStatusEnum.ONLINE.getStatus());
            flowProcessRepository.update(flowProcessDO);
        }
    }

    @Override
    public void offlineApplicationFlowProcess(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationId(applicationId);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            flowProcessDO.setPublishStatus(FlowPublishStatusEnum.OFFLINE.getStatus());
            flowProcessRepository.update(flowProcessDO);
            stopJob(flowProcessDO);
        }
    }

    private void startJob(FlowProcessDO flowProcessDO) {
        if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            startTimeJob(flowProcessDO);
        }
        if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
            startDateFieldJob(flowProcessDO);
        }
    }

    private void startDateFieldJob(FlowProcessDO flowProcessDO) {
        JsonGraph jsonGraph = JsonGraphBuilder.build(flowProcessDO.getProcessDefinition());
        StartDateFieldNodeData startDateFieldNodeData = (StartDateFieldNodeData) jsonGraph.getStartNode().getData();
        FlowProcessDateFieldDO flowProcessDateFieldDO = flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId());
        String jobId;
        if (flowProcessDateFieldDO == null) {
            jobId = jobClient.startJob(flowProcessDO.getId(), consumerSettingParams(startDateFieldNodeData));
            flowProcessDateFieldDO = new FlowProcessDateFieldDO();
            flowProcessDateFieldDO.setProcessId(flowProcessDO.getId());
            flowProcessDateFieldDO.setJobId(jobId);
            flowProcessDateFieldRepository.insert(flowProcessDateFieldDO);
        } else {
            jobId = jobClient.startJob(flowProcessDO.getId(), flowProcessDateFieldDO.getJobId(), consumerSettingParams(startDateFieldNodeData));
            flowProcessDateFieldDO.setJobId(jobId);
            flowProcessDateFieldRepository.update(flowProcessDateFieldDO);
        }
    }

    private JobCreateRequest consumerSettingParams(StartDateFieldNodeData startDateFieldNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setTriggerType(TriggerTypeEnum.CRON);
        jobCreateRequest.setTriggerInterval(startDateFieldNodeData.createCronExpression());
        jobCreateRequest.setExecutorInfo(JobClient.JOB_EXECUTOR_INFO_DATE_FIELD);
        return jobCreateRequest;
    }


    private void startTimeJob(FlowProcessDO flowProcessDO) {
        JsonGraph jsonGraph = JsonGraphBuilder.build(flowProcessDO.getProcessDefinition());
        StartTimeNodeData startTimeNodeData = (StartTimeNodeData) jsonGraph.getStartNode().getData();
        FlowProcessTimeDO flowProcessTimeDO = flowProcessTimeRepository.findByProcessId(flowProcessDO.getId());
        String jobId;
        if (flowProcessTimeDO == null) {
            jobId = jobClient.startJob(flowProcessDO.getId(), consumerSettingParams(startTimeNodeData));
            flowProcessTimeDO = new FlowProcessTimeDO();
            flowProcessTimeDO.setProcessId(flowProcessDO.getId());
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeRepository.insert(flowProcessTimeDO);
        } else {
            jobId = jobClient.startJob(flowProcessDO.getId(), flowProcessTimeDO.getJobId(), consumerSettingParams(startTimeNodeData));
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeRepository.update(flowProcessTimeDO);
        }
    }

    private JobCreateRequest consumerSettingParams(StartTimeNodeData startTimeNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        if (startTimeNodeData.getRepeatType().equals(StartTimeNodeData.REPEAT_TYPE_NONE)) {
            jobCreateRequest.setTriggerType(TriggerTypeEnum.POINT_IN_TIME);
            LocalDateTime localDateTime = LocalDateTime.parse(startTimeNodeData.getTriggerDatetime(), JsonGraphConstant.DATE_TIME_FORMATTER);
            jobCreateRequest.setTriggerTime(Set.of(localDateTime));
        } else {
            jobCreateRequest.setTriggerType(TriggerTypeEnum.CRON);
            jobCreateRequest.setTriggerInterval(startTimeNodeData.createCronExpression());
        }
        jobCreateRequest.setExecutorInfo(JobClient.JOB_EXECUTOR_INFO_TIME);
        return jobCreateRequest;
    }


    private void stopJob(FlowProcessDO flowProcessDO) {
        if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            stopTimeJob(flowProcessDO);
        }
        if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
            stopDateFieldJob(flowProcessDO);
        }
    }

    private void stopTimeJob(FlowProcessDO flowProcessDO) {
        FlowProcessTimeDO flowProcessTimeDO = flowProcessTimeRepository.findByProcessId(flowProcessDO.getId());
        jobClient.stopJob(flowProcessTimeDO.getJobId());
        flowProcessTimeDO.setJobId("");
        flowProcessTimeRepository.update(flowProcessTimeDO);
    }

    private void stopDateFieldJob(FlowProcessDO flowProcessDO) {
        FlowProcessDateFieldDO flowProcessDateFieldDO = flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId());
        jobClient.stopJob(flowProcessDateFieldDO.getJobId());
        flowProcessDateFieldDO.setJobId("");
        flowProcessDateFieldRepository.update(flowProcessDateFieldDO);
    }

}
