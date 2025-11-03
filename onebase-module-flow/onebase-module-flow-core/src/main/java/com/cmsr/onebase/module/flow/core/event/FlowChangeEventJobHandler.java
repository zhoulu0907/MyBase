package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.enums.RocketMQConstants;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.job.JobClient;
import com.cmsr.onebase.module.flow.core.job.JobCreateRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:19
 */
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowChangeEventJobHandler implements MessageListener, ApplicationRunner, DisposableBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.CHANGE_EVENTS_TOPIC;

    @Setter
    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Setter
    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Setter
    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Setter
    @Autowired
    private JobClient jobClient;

    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        String consumerGroup = RocketMQConstants.CHANGE_EVENTS_CONSUMER_GROUP_JOB;
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
            FlowChangeEvent event = FlowChangeEvent.decode(messageView.getBody());
            Long applicationId = event.getApplicationId();
            if (event.getType().equals(FlowChangeEvent.UPDATE)) {
                onApplicationChange(applicationId);
            }
            if (event.getType().equals(FlowChangeEvent.DELETE)) {
                onApplicationDelete(applicationId);
            }
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
        }
    }

    public void onApplicationDelete(Long applicationId) {
        deleteJob(applicationId);
    }

    public void onApplicationChange(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus());
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            startJob(flowProcessDO);
        }
    }

    public void startJob(FlowProcessDO flowProcessDO) {
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
        JobCreateRequest jobCreateRequest = consumerSettingParams(startTimeNodeData);
        jobCreateRequest.setApplicationId(flowProcessDO.getApplicationId());
        jobCreateRequest.setProcessId(flowProcessDO.getId());
        jobCreateRequest.setProcessName(flowProcessDO.getProcessName());
        String jobId = jobClient.startJob(jobCreateRequest);
        if (flowProcessTimeDO == null) {
            flowProcessTimeDO = new FlowProcessTimeDO();
            flowProcessTimeDO.setProcessId(flowProcessDO.getId());
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeRepository.insert(flowProcessTimeDO);
        } else {
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeRepository.update(flowProcessTimeDO);
        }
    }


    private JobCreateRequest consumerSettingParams(StartTimeNodeData startTimeNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setStartTime(startTimeNodeData.getStartTime().trim());
        jobCreateRequest.setEndTime(startTimeNodeData.getEndTime().trim());
        jobCreateRequest.setCrontab(startTimeNodeData.createCronExpression().trim());
        return jobCreateRequest;
    }

    private void startDateFieldJob(FlowProcessDO flowProcessDO) {
        JsonGraph jsonGraph = JsonGraphBuilder.build(flowProcessDO.getProcessDefinition());
        StartDateFieldNodeData startDateFieldNodeData = (StartDateFieldNodeData) jsonGraph.getStartNode().getData();
        FlowProcessDateFieldDO flowProcessDateFieldDO = flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId());
        JobCreateRequest jobCreateRequest = consumerSettingParams(startDateFieldNodeData);
        jobCreateRequest.setApplicationId(flowProcessDO.getApplicationId());
        jobCreateRequest.setProcessId(flowProcessDO.getId());
        jobCreateRequest.setProcessName(flowProcessDO.getProcessName());
        String jobId = jobClient.startJob(jobCreateRequest);
        if (flowProcessDateFieldDO == null) {
            flowProcessDateFieldDO = new FlowProcessDateFieldDO();
            flowProcessDateFieldDO.setProcessId(flowProcessDO.getId());
            flowProcessDateFieldDO.setJobId(jobId);
            flowProcessDateFieldRepository.insert(flowProcessDateFieldDO);
        } else {
            flowProcessDateFieldDO.setJobId(jobId);
            flowProcessDateFieldRepository.update(flowProcessDateFieldDO);
        }
    }

    private JobCreateRequest consumerSettingParams(StartDateFieldNodeData startDateFieldNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setStartTime(LocalDateTime.now().format(JobClient.DATETIME_FORMATTER));
        jobCreateRequest.setEndTime("2050-12-30 23:59:59");
        jobCreateRequest.setCrontab(startDateFieldNodeData.createCronExpression());
        return jobCreateRequest;
    }

    public void deleteJob(Long applicationId) {
        jobClient.deleteJob(applicationId);
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }
}
