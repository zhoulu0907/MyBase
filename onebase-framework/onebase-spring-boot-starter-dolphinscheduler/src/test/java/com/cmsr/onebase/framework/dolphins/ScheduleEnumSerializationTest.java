package com.cmsr.onebase.framework.dolphins;

import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.ReleaseStateEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WarningTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WorkflowInstancePriorityEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.model.ScheduleDTO;
import com.cmsr.onebase.framework.dolphins.dto.schedule.request.ScheduleCreateRequestDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Schedule 枚举类型序列化和反序列化测试
 *
 * 验证:
 * 1. 枚举类型能正确序列化为字符串
 * 2. 字符串能正确反序列化为枚举类型
 * 3. Jackson与DolphinScheduler API的兼容性
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Slf4j
public class ScheduleEnumSerializationTest {

    private final ObjectMapper objectMapper;

    public ScheduleEnumSerializationTest() {
        this.objectMapper = new ObjectMapper();
        // 配置与RetrofitConfig相同的日期格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void testEnumSerialization() throws Exception {
        ScheduleCreateRequestDTO req = new ScheduleCreateRequestDTO();
        req.setWorkflowDefinitionCode(123456789L);
        req.setCrontab("0 0 12 * * ?");
        req.setStartTime("2025-10-17 00:00:00");
        req.setEndTime("2026-10-17 00:00:00");
        req.setTimezoneId("Asia/Shanghai");
        req.setFailureStrategy(FailureStrategyEnum.CONTINUE);
        req.setReleaseState(ReleaseStateEnum.OFFLINE);
        req.setWarningType(WarningTypeEnum.FAILURE);
        req.setWorkflowInstancePriority(WorkflowInstancePriorityEnum.HIGH);

        String json = objectMapper.writeValueAsString(req);
        log.info("序列化结果:\n{}", json);

        Assertions.assertTrue(json.contains("\"CONTINUE\""), "应包含 CONTINUE 字符串");
        Assertions.assertTrue(json.contains("\"OFFLINE\""), "应包含 OFFLINE 字符串");
        Assertions.assertTrue(json.contains("\"FAILURE\""), "应包含 FAILURE 字符串");
        Assertions.assertTrue(json.contains("\"HIGH\""), "应包含 HIGH 字符串");

        log.info("✓ 枚举类型序列化测试通过");
    }

    @Test
    public void testEnumDeserialization() throws Exception {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"workflowDefinitionCode\": 123456789,\n" +
                "  \"crontab\": \"0 0 12 * * ?\",\n" +
                "  \"startTime\": \"2025-10-17 00:00:00\",\n" +
                "  \"endTime\": \"2026-10-17 00:00:00\",\n" +
                "  \"timezoneId\": \"Asia/Shanghai\",\n" +
                "  \"failureStrategy\": \"CONTINUE\",\n" +
                "  \"warningType\": \"FAILURE\",\n" +
                "  \"releaseState\": \"OFFLINE\",\n" +
                "  \"workflowInstancePriority\": \"HIGH\"\n" +
                "}";

        ScheduleDTO dto = objectMapper.readValue(json, ScheduleDTO.class);

        Assertions.assertEquals(FailureStrategyEnum.CONTINUE, dto.getFailureStrategy(), "failureStrategy 应为 CONTINUE");
        Assertions.assertEquals(WarningTypeEnum.FAILURE, dto.getWarningType(), "warningType 应为 FAILURE");
        Assertions.assertEquals(ReleaseStateEnum.OFFLINE, dto.getReleaseState(), "releaseState 应为 OFFLINE");
        Assertions.assertEquals(WorkflowInstancePriorityEnum.HIGH, dto.getWorkflowInstancePriority(), "workflowInstancePriority 应为 HIGH");

        log.info("✓ 枚举类型反序列化测试通过");
        log.info("  failureStrategy: {}", dto.getFailureStrategy());
        log.info("  warningType: {}", dto.getWarningType());
        log.info("  releaseState: {}", dto.getReleaseState());
        log.info("  workflowInstancePriority: {}", dto.getWorkflowInstancePriority());
    }

    @Test
    public void testAllEnumValues() {
        log.info("测试所有枚举类型的值:");

        log.info("FailureStrategyEnum: {}", (Object[]) FailureStrategyEnum.values());
        Assertions.assertEquals(2, FailureStrategyEnum.values().length, "FailureStrategyEnum 应有2个值");

        log.info("WarningTypeEnum: {}", (Object[]) WarningTypeEnum.values());
        Assertions.assertEquals(4, WarningTypeEnum.values().length, "WarningTypeEnum 应有4个值");

        log.info("ReleaseStateEnum: {}", (Object[]) ReleaseStateEnum.values());
        Assertions.assertEquals(2, ReleaseStateEnum.values().length, "ReleaseStateEnum 应有2个值");

        log.info("WorkflowInstancePriorityEnum: {}", (Object[]) WorkflowInstancePriorityEnum.values());
        Assertions.assertEquals(5, WorkflowInstancePriorityEnum.values().length, "WorkflowInstancePriorityEnum 应有5个值");

        log.info("✓ 所有枚举类型值测试通过");
    }
}
