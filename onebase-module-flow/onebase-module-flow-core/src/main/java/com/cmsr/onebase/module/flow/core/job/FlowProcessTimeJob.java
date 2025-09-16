package com.cmsr.onebase.module.flow.core.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Setter
@Slf4j
@Component
public class FlowProcessTimeJob {

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @JobExecutor(name = "flow_process_time_job")
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        Object jobParams = jobArgs.getJobParams();
        log.info("FlowProcessTimeJob execute, command: {}", jobParams);
        if (jobParams == null) {
            return ExecuteResult.failure("参数为空");
        }
        Map<String, Object> jobParamsMap = JsonUtils.parseObject(jobParams.toString(), Map.class);
        Long processId = NumberUtils.toLong(MapUtils.getString(jobParamsMap, "processId"));
        flowProcessExecutor.execute(processId, Collections.emptyMap());
        return ExecuteResult.success("测试成功");
    }

}
