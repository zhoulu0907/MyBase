package com.cmsr.onebase.framework.remote.model.process;

import java.util.List;

/**
 * 工作流定义创建/更新请求参数（仅数据传输，无业务逻辑）
 *
 * 对应 DolphinScheduler 3.3.1 的 workflow-definition 接口字段：
 * name、locations、taskDefinitionJson、taskRelationJson、tenantCode、description、executionType、globalParams、timeout。
 */
public class ProcessDefineParam {

    /** workflow 名称 */
    private String name;
    /** 画布位置列表 */
    private List<TaskLocation> locations;
    /** 任务定义列表 */
    private List<TaskDefinition> taskDefinitionJson;
    /** 任务依赖关系列表 */
    private List<TaskRelation> taskRelationJson;
    /** 租户编码 */
    private String tenantCode;
    /** 流程描述 */
    private String description;
    /** 执行类型：PARALLEL/SERIAL_WAIT/SERIAL_DISCARD/SERIAL_PRIORITY */
    private String executionType;
    /** 全局参数 */
    private List<Parameter> globalParams;
    /** 超时（字符串形式，保持与 DS 接口一致） */
    private String timeout;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<TaskLocation> getLocations() { return locations; }
    public void setLocations(List<TaskLocation> locations) { this.locations = locations; }
    public List<TaskDefinition> getTaskDefinitionJson() { return taskDefinitionJson; }
    public void setTaskDefinitionJson(List<TaskDefinition> taskDefinitionJson) { this.taskDefinitionJson = taskDefinitionJson; }
    public List<TaskRelation> getTaskRelationJson() { return taskRelationJson; }
    public void setTaskRelationJson(List<TaskRelation> taskRelationJson) { this.taskRelationJson = taskRelationJson; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getExecutionType() { return executionType; }
    public void setExecutionType(String executionType) { this.executionType = executionType; }
    public List<Parameter> getGlobalParams() { return globalParams; }
    public void setGlobalParams(List<Parameter> globalParams) { this.globalParams = globalParams; }
    public String getTimeout() { return timeout; }
    public void setTimeout(String timeout) { this.timeout = timeout; }
}
