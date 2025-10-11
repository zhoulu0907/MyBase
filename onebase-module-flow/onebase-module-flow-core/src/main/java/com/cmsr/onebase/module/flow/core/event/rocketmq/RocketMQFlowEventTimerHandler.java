package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.aizuda.snailjob.client.job.core.enums.TriggerTypeEnum;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.event.FlowEvent;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.job.JobClient;
import com.cmsr.onebase.module.flow.core.job.JobCreateRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:19
 */
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class RocketMQFlowEventTimerHandler implements MessageListener, ApplicationRunner {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.TOPIC;

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Autowired
    private JobClient jobClient;


    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        String consumerGroup = RocketMQConstants.CONSUMER_GROUP_PREFIX + "timer";
        FilterExpression filterExpression = new FilterExpression();
        this.consumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setConsumerGroup(consumerGroup)
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .setMessageListener(this)
                .build();
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        try {
            FlowEvent event = FlowEvent.decode(messageView.getBody());
            FlowProcessDO flowProcessDO = flowProcessRepository.findById(event.getProcessId());
            if (flowProcessDO == null) {
                log.warn("流程不存在：{}", event.getProcessId());
                return ConsumeResult.SUCCESS;
            }
            if (StringUtils.equalsIgnoreCase(event.getType(), FlowEvent.UPDATE)) {
                startJob(flowProcessDO);
            } else if (StringUtils.equalsIgnoreCase(event.getType(), FlowEvent.DELETE)) {
                deleteJob(flowProcessDO);
            }
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
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


    private void deleteJob(FlowProcessDO flowProcessDO) {
        if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            deleteTimeJob(flowProcessDO);
        }
        if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
            deleteDateFieldJob(flowProcessDO);
        }
    }

    private void deleteTimeJob(FlowProcessDO flowProcessDO) {
        FlowProcessTimeDO flowProcessTimeDO = flowProcessTimeRepository.findByProcessId(flowProcessDO.getId());
        jobClient.deleteJob(flowProcessTimeDO.getJobId());
        flowProcessTimeDO.setJobId("0");
        flowProcessTimeRepository.update(flowProcessTimeDO);
    }

    private void deleteDateFieldJob(FlowProcessDO flowProcessDO) {
        FlowProcessDateFieldDO flowProcessDateFieldDO = flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId());
        jobClient.deleteJob(flowProcessDateFieldDO.getJobId());
        flowProcessDateFieldDO.setJobId("0");
        flowProcessDateFieldRepository.update(flowProcessDateFieldDO);
    }
}
