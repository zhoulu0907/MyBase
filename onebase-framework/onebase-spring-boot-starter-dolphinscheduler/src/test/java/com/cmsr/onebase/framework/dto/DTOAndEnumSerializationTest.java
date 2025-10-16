package com.cmsr.onebase.framework.dto;

import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import com.cmsr.onebase.framework.remote.dto.PageInfoDTO;
import com.cmsr.onebase.framework.remote.dto.process.ProcessDefineRespDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleDefineParamDTO;
import com.cmsr.onebase.framework.remote.dto.task.TaskDefinitionRespDTO;
import com.cmsr.onebase.framework.remote.dto.taskinstance.TaskInstanceQueryRespDTO;
import com.cmsr.onebase.framework.remote.dto.instance.ProcessInstanceCreateParamDTO;
import com.cmsr.onebase.framework.remote.enums.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO 与枚举封装性测试：验证序列化/反序列化与取值正确性。
 *
 * 覆盖：
 * - HttpRestResultDTO<T> + PageInfoDTO<T>
 * - ProcessDefineRespDTO、TaskDefinitionRespDTO、TaskInstanceQueryRespDTO、Schedule* DTO
 * - ExecuteTypeEnum、FailureStrategyEnum、WarningTypeEnum、PriorityEnum、TaskDependTypeEnum、ProcessExecutionTypeEnum
 *
 * 直接使用 ObjectMapper 进行序列化/反序列化，无需加载 Spring Boot 上下文。
 *
 * @author matianyu
 * @date 2025-10-16
 */
public class DTOAndEnumSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 验证 HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> 的序列化/反序列化
     */
    @Test
    void httpWrapper_with_page_process_define_resp_should_round_trip() throws Exception {
        ProcessDefineRespDTO row = new ProcessDefineRespDTO();
        row.setCode(1234567890123L);
        row.setName("UT-流程-乌龙-818");
        row.setDescription("UT-说明-甲乙");
        row.setVersion(8);

        PageInfoDTO<ProcessDefineRespDTO> page = new PageInfoDTO<>();
        page.setTotal(88);
        page.setPageNo(6);
        page.setPageSize(3);
        List<ProcessDefineRespDTO> records = new ArrayList<>();
        records.add(row);
        page.setRecords(records);

        HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> wrapper = new HttpRestResultDTO<>();
        wrapper.setCode(0);
        wrapper.setMsg("success-UT-龙眼");
        wrapper.setData(page);

        String json = objectMapper.writeValueAsString(wrapper);
        HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>> back = objectMapper.readValue(
                json, new TypeReference<HttpRestResultDTO<PageInfoDTO<ProcessDefineRespDTO>>>() {}
        );

        Assertions.assertThat(back.getCode()).isEqualTo(0);
        Assertions.assertThat(back.getMsg()).isEqualTo("success-UT-龙眼");
        Assertions.assertThat(back.getData().getRecords().get(0).getName()).isEqualTo("UT-流程-乌龙-818");
        Assertions.assertThat(back.getData().getTotal()).isEqualTo(88);
    }

    /**
     * 验证包含多个枚举字段的 DTO（ProcessInstanceCreateParamDTO）序列化/反序列化
     */
    @Test
    void process_instance_create_param_enum_fields_should_round_trip() throws Exception {
        ProcessInstanceCreateParamDTO dto = new ProcessInstanceCreateParamDTO();
        dto.setFailureStrategy(FailureStrategyEnum.CONTINUE);
        dto.setProcessDefinitionCode(445566L);
        dto.setProcessInstancePriority(PriorityEnum.HIGH);
        dto.setScheduleTime("2025-10-16 09:10:11");
        dto.setWarningGroupId(778899L);
        dto.setWarningType(WarningTypeEnum.SUCCESS);
        dto.setDryRun(1);
        dto.setEnvironmentCode("env-独特-乙");
        dto.setExecType(ExecuteTypeEnum.RE_RUN);
        dto.setExpectedParallelismNumber("5");
        dto.setRunMode("RUN_ONLY-独特");
        dto.setStartNodeList("[1101]");
        dto.setStartParams("{\\\"x\\\":1}");
        dto.setTaskDependType(TaskDependTypeEnum.TASK_PRE);
        dto.setWorkerGroup("wg-独特-贰");

        String json = objectMapper.writeValueAsString(dto);
        // 校验枚举名字符串
        Assertions.assertThat(json).contains("CONTINUE");
        Assertions.assertThat(json).contains("HIGH");
        Assertions.assertThat(json).contains("SUCCESS");
        Assertions.assertThat(json).contains("RE_RUN");
        Assertions.assertThat(json).contains("TASK_PRE");

        ProcessInstanceCreateParamDTO back = objectMapper.readValue(json, ProcessInstanceCreateParamDTO.class);
        Assertions.assertThat(back.getFailureStrategy()).isEqualTo(FailureStrategyEnum.CONTINUE);
        Assertions.assertThat(back.getProcessInstancePriority()).isEqualTo(PriorityEnum.HIGH);
        Assertions.assertThat(back.getWarningType()).isEqualTo(WarningTypeEnum.SUCCESS);
        Assertions.assertThat(back.getExecType()).isEqualTo(ExecuteTypeEnum.RE_RUN);
        Assertions.assertThat(back.getTaskDependType()).isEqualTo(TaskDependTypeEnum.TASK_PRE);
    }

    /**
     * 验证 ScheduleDefineParamDTO、ScheduleDTO 的序列化/反序列化
     */
    @Test
    void schedule_define_param_should_round_trip() throws Exception {
        ScheduleDTO schedule = new ScheduleDTO();
        schedule.setStartTime("2025-10-16 00:00:00");
        schedule.setEndTime("2025-12-31 23:59:59");
        schedule.setCrontab("0 0/7 * * * ?");

        ScheduleDefineParamDTO param = new ScheduleDefineParamDTO();
        param.setSchedule(schedule);
        param.setFailureStrategy(FailureStrategyEnum.END);
        param.setWarningType(WarningTypeEnum.ALL);
        param.setProcessInstancePriority(PriorityEnum.MEDIUM);
        param.setWorkflowDefinitionCode(909090L);

        String json = objectMapper.writeValueAsString(param);
        Assertions.assertThat(json).contains("ALL");
        Assertions.assertThat(json).contains("MEDIUM");

        ScheduleDefineParamDTO back = objectMapper.readValue(json, ScheduleDefineParamDTO.class);
        Assertions.assertThat(back.getSchedule().getCrontab()).isEqualTo("0 0/7 * * * ?");
        Assertions.assertThat(back.getWarningType()).isEqualTo(WarningTypeEnum.ALL);
        Assertions.assertThat(back.getProcessInstancePriority()).isEqualTo(PriorityEnum.MEDIUM);
        Assertions.assertThat(back.getWorkflowDefinitionCode()).isEqualTo(909090L);
    }

    /**
     * 验证任务定义与任务实例简要 DTO 的序列化/反序列化
     */
    @Test
    void task_and_task_instance_simple_dto_round_trip() throws Exception {
        TaskDefinitionRespDTO t = new TaskDefinitionRespDTO();
        t.setCode(80801L);
        t.setVersion(3);
        t.setName("TD-独特-桂皮");
        t.setDescription("香料");
        t.setTaskType("SHELL");

        TaskInstanceQueryRespDTO ti = new TaskInstanceQueryRespDTO();
        ti.setId(60601L);
        ti.setProcessInstanceId(70701L);
        ti.setName("TI-独特-肉桂");
        ti.setState("SUCCESS");

        String tj = objectMapper.writeValueAsString(t);
        String tij = objectMapper.writeValueAsString(ti);

        TaskDefinitionRespDTO tBack = objectMapper.readValue(tj, TaskDefinitionRespDTO.class);
        TaskInstanceQueryRespDTO tiBack = objectMapper.readValue(tij, TaskInstanceQueryRespDTO.class);

        Assertions.assertThat(tBack.getName()).isEqualTo("TD-独特-桂皮");
        Assertions.assertThat(tiBack.getState()).isEqualTo("SUCCESS");
    }

    /**
     * 单纯校验所有相关枚举常量存在且可用
     */
    @Test
    void all_enums_should_have_expected_values() {
        Assertions.assertThat(ExecuteTypeEnum.valueOf("RE_RUN")).isEqualTo(ExecuteTypeEnum.RE_RUN);
        Assertions.assertThat(ExecuteTypeEnum.values()).contains(ExecuteTypeEnum.PAUSE, ExecuteTypeEnum.STOP);

        Assertions.assertThat(FailureStrategyEnum.valueOf("END")).isEqualTo(FailureStrategyEnum.END);
        Assertions.assertThat(FailureStrategyEnum.values()).contains(FailureStrategyEnum.CONTINUE);

        Assertions.assertThat(WarningTypeEnum.valueOf("GLOBAL")).isEqualTo(WarningTypeEnum.GLOBAL);
        Assertions.assertThat(WarningTypeEnum.values()).contains(WarningTypeEnum.NONE, WarningTypeEnum.ALL);

        Assertions.assertThat(PriorityEnum.valueOf("LOWEST")).isEqualTo(PriorityEnum.LOWEST);
        Assertions.assertThat(PriorityEnum.values()).contains(PriorityEnum.HIGHEST, PriorityEnum.MEDIUM);

        Assertions.assertThat(TaskDependTypeEnum.valueOf("TASK_ONLY")).isEqualTo(TaskDependTypeEnum.TASK_ONLY);
        Assertions.assertThat(TaskDependTypeEnum.values()).contains(TaskDependTypeEnum.TASK_PRE, TaskDependTypeEnum.TASK_POST);

        Assertions.assertThat(ProcessExecutionTypeEnum.valueOf("PARALLEL")).isEqualTo(ProcessExecutionTypeEnum.PARALLEL);
        Assertions.assertThat(ProcessExecutionTypeEnum.values()).contains(
                ProcessExecutionTypeEnum.SERIAL_WAIT, ProcessExecutionTypeEnum.SERIAL_DISCARD, ProcessExecutionTypeEnum.SERIAL_PRIORITY
        );
    }
}
