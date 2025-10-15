package com.cmsr.onebase.framework.remote.dto.process;

import com.cmsr.onebase.framework.remote.enums.ProcessExecutionTypeEnum;
import java.util.List;

/**
 * 工作流定义创建/更新请求参数 DTO
 *
 * 对应 DolphinScheduler 3.3.1 的 workflow-definition 接口字段。
 *
 * @author matianyu
 * @date 2025-10-15
 */
public class ProcessDefineParamDTO {

    /** workflow 名称 */
    private String name;
    /** 画布位置列表 */
    private List<TaskLocationDTO> locations;
    /** 任务定义列表 */
    private List<TaskDefinitionDTO> taskDefinitionJson;
    /** 任务依赖关系列表 */
    private List<TaskRelationDTO> taskRelationJson;
    /** 租户编码 */
    private String tenantCode;
    /** 流程描述 */
    private String description;
    /** 执行类型 */
    private ProcessExecutionTypeEnum executionType;
    /** 全局参数 */
    private List<ParameterDTO> globalParams;
    /** 超时（字符串形式，保持与 DS 接口一致） */
    private String timeout;

    public String getName() { return name; }
    public List<TaskLocationDTO> getLocations() { return locations; }
    public List<TaskDefinitionDTO> getTaskDefinitionJson() { return taskDefinitionJson; }
    public List<TaskRelationDTO> getTaskRelationJson() { return taskRelationJson; }
    public String getTenantCode() { return tenantCode; }
    public String getDescription() { return description; }
    public ProcessExecutionTypeEnum getExecutionType() { return executionType; }
    public List<ParameterDTO> getGlobalParams() { return globalParams; }
    public String getTimeout() { return timeout; }

    public void setName(String name) { this.name = name; }
    public void setLocations(List<TaskLocationDTO> locations) { this.locations = locations; }
    public void setTaskDefinitionJson(List<TaskDefinitionDTO> taskDefinitionJson) { this.taskDefinitionJson = taskDefinitionJson; }
    public void setTaskRelationJson(List<TaskRelationDTO> taskRelationJson) { this.taskRelationJson = taskRelationJson; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public void setDescription(String description) { this.description = description; }
    public void setExecutionType(ProcessExecutionTypeEnum executionType) { this.executionType = executionType; }
    public void setGlobalParams(List<ParameterDTO> globalParams) { this.globalParams = globalParams; }
    public void setTimeout(String timeout) { this.timeout = timeout; }
}
