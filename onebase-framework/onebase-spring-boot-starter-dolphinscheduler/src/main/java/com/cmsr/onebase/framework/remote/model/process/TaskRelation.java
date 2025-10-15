package com.cmsr.onebase.framework.remote.model.process;

/**
 * 任务依赖关系 DTO（仅数据传输，无业务逻辑）
 */
public class TaskRelation {

    private String name = "";
    private Long preTaskCode = 0L;
    private Integer preTaskVersion = 0;
    private Long postTaskCode;
    private Integer postTaskVersion = 0;
    private Integer conditionType = 0;
    private Integer conditionParams;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getPreTaskCode() { return preTaskCode; }
    public void setPreTaskCode(Long preTaskCode) { this.preTaskCode = preTaskCode; }
    public Integer getPreTaskVersion() { return preTaskVersion; }
    public void setPreTaskVersion(Integer preTaskVersion) { this.preTaskVersion = preTaskVersion; }
    public Long getPostTaskCode() { return postTaskCode; }
    public void setPostTaskCode(Long postTaskCode) { this.postTaskCode = postTaskCode; }
    public Integer getPostTaskVersion() { return postTaskVersion; }
    public void setPostTaskVersion(Integer postTaskVersion) { this.postTaskVersion = postTaskVersion; }
    public Integer getConditionType() { return conditionType; }
    public void setConditionType(Integer conditionType) { this.conditionType = conditionType; }
    public Integer getConditionParams() { return conditionParams; }
    public void setConditionParams(Integer conditionParams) { this.conditionParams = conditionParams; }
}

