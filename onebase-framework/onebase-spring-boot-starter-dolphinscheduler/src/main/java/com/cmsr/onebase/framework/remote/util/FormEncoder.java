package com.cmsr.onebase.framework.remote.util;

import com.cmsr.onebase.framework.remote.dto.process.ProcessDefineParamDTO;
import com.cmsr.onebase.framework.remote.dto.schedule.ScheduleDefineParamDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单编码工具：将 DTO 转为 x-www-form-urlencoded 的键值对。
 */
public final class FormEncoder {

    private FormEncoder() {}

    public static Map<String, String> encode(ProcessDefineParamDTO p, ObjectMapper mapper) {
        Map<String, String> form = new HashMap<>();
        if (p == null) {
            return form;
        }
        put(form, "name", p.getName());
        putJson(form, "locations", p.getLocations(), mapper);
        putJson(form, "taskDefinitionJson", p.getTaskDefinitionJson(), mapper);
        putJson(form, "taskRelationJson", p.getTaskRelationJson(), mapper);
        put(form, "tenantCode", p.getTenantCode());
        put(form, "description", p.getDescription());
        if (p.getExecutionType() != null) {
            form.put("executionType", p.getExecutionType().name());
        }
        putJson(form, "globalParams", p.getGlobalParams(), mapper);
        put(form, "timeout", p.getTimeout());
        return form;
    }

    public static Map<String, String> encode(ScheduleDefineParamDTO p, ObjectMapper mapper) {
        Map<String, String> form = new HashMap<>();
        if (p == null) {
            return form;
        }
        putJson(form, "schedule", p.getSchedule(), mapper);
        if (p.getFailureStrategy() != null) {
            form.put("failureStrategy", p.getFailureStrategy().name());
        }
        if (p.getWarningType() != null) {
            form.put("warningType", p.getWarningType().name());
        }
        if (p.getProcessInstancePriority() != null) {
            form.put("processInstancePriority", p.getProcessInstancePriority().name());
        }
        put(form, "warningGroupId", p.getWarningGroupId());
        put(form, "workerGroup", p.getWorkerGroup());
        put(form, "environmentCode", p.getEnvironmentCode());
        if (p.getWorkflowDefinitionCode() != null) {
            form.put("workflowDefinitionCode", String.valueOf(p.getWorkflowDefinitionCode()));
        }
        return form;
    }

    private static void put(Map<String, String> form, String key, String val) {
        if (val != null) {
            form.put(key, val);
        }
    }

    private static void putJson(Map<String, String> form, String key, Object value, ObjectMapper mapper) {
        if (value == null) return;
        try {
            form.put(key, mapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("序列化表单字段失败: " + key, e);
        }
    }
}
