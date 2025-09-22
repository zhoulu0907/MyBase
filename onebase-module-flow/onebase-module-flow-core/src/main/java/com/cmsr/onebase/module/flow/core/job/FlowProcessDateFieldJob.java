package com.cmsr.onebase.module.flow.core.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.enums.JsonGraphConstant;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.data.StartTimeNodeData;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowProcessDateFieldJob {

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @JobExecutor(name = "flow_process_date_field_job")
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        Object jobParams = jobArgs.getJobParams();
        log.info("FlowProcessTimeJob execute, command: {}", jobParams);
        if (jobParams == null) {
            return ExecuteResult.failure("参数为空");
        }
        Map<String, Object> jobParamsMap = JsonUtils.parseObject(jobParams.toString(), Map.class);
        Long processId = NumberUtils.toLong(MapUtils.getString(jobParamsMap, JsonGraphConstant.PROCESS_ID));
        StartTimeNodeData startTimeNodeData = graphFlowCache.getStartTimeNodeData(processId);
        // 检查当前时间是否在设定的时间范围内
        if (!isCurrentTimeInRange(startTimeNodeData)) {
            log.info("当前时间不在设定的时间范围内，跳过执行");
            return ExecuteResult.success("当前时间不在设定的时间范围内，跳过执行");
        } else {
            flowProcessExecutor.execute(processId, Collections.emptyMap());
            return ExecuteResult.success("执行成功");
        }
    }

    /**
     * 判断当前时间是否在开始和结束时间范围内
     *
     * @param startTimeNodeData 时间节点数据
     * @return 如果在时间范围内返回true，否则返回false
     */
    private boolean isCurrentTimeInRange(StartTimeNodeData startTimeNodeData) {
        LocalDateTime startLocalDateTime = startTimeNodeData.getStartLocalDateTime();
        LocalDateTime endLocalDateTime = startTimeNodeData.getEndLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        // 如果开始时间为空，则没有开始时间限制
        if (startLocalDateTime != null && now.isBefore(startLocalDateTime)) {
            return false;
        }

        // 如果结束时间为空，则没有结束时间限制
        if (endLocalDateTime != null && now.isAfter(endLocalDateTime)) {
            return false;
        }

        return true;
    }
}
