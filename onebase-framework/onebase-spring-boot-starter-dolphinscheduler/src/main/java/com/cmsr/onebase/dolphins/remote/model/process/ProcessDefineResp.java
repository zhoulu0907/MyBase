package com.cmsr.onebase.dolphins.remote.model.process;

/**
 * 流程定义简要信息
 */
public class ProcessDefineResp {
    private Long code;
    private String name;
    private String description;
    private Integer version;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

