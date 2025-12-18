package com.cmsr.onebase.module.flow.core.enums;

/**
 * @Author：huangjie
 * @Date：2025/11/3 12:57
 */
public enum FlowJobStatusEnum {

    /**
     * 未部署或需要重新部署
     */
    INIT("init"),

    /**
     * 已部署
     */
    DEPLOYED("deployed");

    private final String status;

    FlowJobStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static boolean isDeployed(String jobStatus) {
        return DEPLOYED.status.equalsIgnoreCase(jobStatus);
    }

}
