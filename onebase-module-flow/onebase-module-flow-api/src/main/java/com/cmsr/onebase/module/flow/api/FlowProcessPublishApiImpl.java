package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowPublishStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.event.FlowProcessEventPublisher;
import com.cmsr.onebase.module.flow.core.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.graph.data.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.job.JobClient;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    private JobClient jobClient;

    @Autowired
    private FlowProcessEventPublisher flowProcessEventPublisher;

    @Override
    public void onlineApplicationFlowProcess(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus());
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            startTimeJob(flowProcessDO);
            flowProcessDO.setPublishStatus(FlowPublishStatusEnum.ONLINE.getStatus());
            flowProcessRepository.update(flowProcessDO);
            flowProcessEventPublisher.publishProcessUpdate(flowProcessDO.getId());
        }
    }

    @Override
    public void offlineApplicationFlowProcess(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationId(applicationId);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            flowProcessDO.setPublishStatus(FlowPublishStatusEnum.OFFLINE.getStatus());
            flowProcessRepository.update(flowProcessDO);
            stopTimeJob(flowProcessDO);
        }
    }

    private void startTimeJob(FlowProcessDO flowProcessDO) {
        if (!FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            return;
        }
        JsonGraph jsonGraph = JsonGraph.of(flowProcessDO.getProcessDefinition());
        Map<String, Object> data = jsonGraph.getStartNode().getData();
        StartTimeNodeData startTimeNodeData = new StartTimeNodeData(data);
        FlowProcessTimeDO flowProcessTimeDO = flowProcessTimeRepository.findByProcessId(flowProcessDO.getId());
        String jobId;
        if (flowProcessTimeDO == null) {
            jobId = jobClient.startJob(flowProcessDO.getId(), startTimeNodeData);
            flowProcessTimeDO = new FlowProcessTimeDO();
            flowProcessTimeDO.setProcessId(flowProcessDO.getId());
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeRepository.insert(flowProcessTimeDO);
        } else {
            jobId = jobClient.startJob(flowProcessDO.getId(), flowProcessTimeDO.getJobId(), startTimeNodeData);
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeRepository.update(flowProcessTimeDO);
        }
    }


    private void stopTimeJob(FlowProcessDO flowProcessDO) {
        if (!FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            return;
        }
        FlowProcessTimeDO flowProcessTimeDO = flowProcessTimeRepository.findByProcessId(flowProcessDO.getId());
        jobClient.stopJob(flowProcessTimeDO.getJobId());
        flowProcessTimeDO.setJobId("");
        flowProcessTimeRepository.update(flowProcessTimeDO);
    }

}
