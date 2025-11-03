package com.cmsr.onebase.module.flow.core.enums;

/**
 * @Author：huangjie
 * @Date：2025/11/3 12:57
 */
public enum FlowJobStatusEnum {

    NEED_DEPLOY("need_deploy", "未部署或需要重新部署"),
    DEPLOYED("deployed", "已部署");

    private final String status;
    private final String name;

    FlowJobStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public static FlowJobStatusEnum getByStatus(String status) {
        for (FlowJobStatusEnum value : FlowJobStatusEnum.values()) {
            if (value.status.equalsIgnoreCase(status)) {
                return value;
            }
        }
        return NEED_DEPLOY;
    }

    public static boolean isDeployed(String jobStatus) {
        return DEPLOYED.status.equalsIgnoreCase(jobStatus);
    }

}
