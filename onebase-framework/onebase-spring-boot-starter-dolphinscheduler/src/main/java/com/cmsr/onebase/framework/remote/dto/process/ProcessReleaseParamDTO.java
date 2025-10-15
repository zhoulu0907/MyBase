package com.cmsr.onebase.framework.remote.dto.process;

import lombok.Data;

/**
 * 工作流发布参数（上线/下线） DTO
 */
@Data
public class ProcessReleaseParamDTO {
    /** 发布状态：ONLINE 或 OFFLINE */
    private String releaseState;

    public String getReleaseState() { return releaseState; }
    public void setReleaseState(String releaseState) { this.releaseState = releaseState; }

    public static ProcessReleaseParamDTO online() {
        ProcessReleaseParamDTO p = new ProcessReleaseParamDTO();
        p.setReleaseState("ONLINE");
        return p;
    }

    public static ProcessReleaseParamDTO offline() {
        ProcessReleaseParamDTO p = new ProcessReleaseParamDTO();
        p.setReleaseState("OFFLINE");
        return p;
    }
}
