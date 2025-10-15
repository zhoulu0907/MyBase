package com.cmsr.onebase.framework.remote.model.process;

/**
 * 工作流发布参数（上线/下线）
 *
 * 对应 DolphinScheduler 3.3.1 的 release 接口请求体。
 */
public class ProcessReleaseParam {

    /** 发布状态：ONLINE 或 OFFLINE */
    private String releaseState;

    public String getReleaseState() {
        return releaseState;
    }

    public void setReleaseState(String releaseState) {
        this.releaseState = releaseState;
    }

    /**
     * 构造上线参数
     */
    public static ProcessReleaseParam online() {
        ProcessReleaseParam p = new ProcessReleaseParam();
        p.setReleaseState("ONLINE");
        return p;
    }

    /**
     * 构造下线参数
     */
    public static ProcessReleaseParam offline() {
        ProcessReleaseParam p = new ProcessReleaseParam();
        p.setReleaseState("OFFLINE");
        return p;
    }
}

