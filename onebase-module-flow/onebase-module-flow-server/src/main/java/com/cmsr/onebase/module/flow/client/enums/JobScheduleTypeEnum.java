package com.cmsr.onebase.module.flow.client.enums;

/**
 * @Author：huangjie
 * @Date：2025/9/3 10:13
 */
public enum JobScheduleTypeEnum {

    NONE("NONE"),
    FIX_RATE("FIX_RATE"),
    CRON("CRON");

    private String value;

    JobScheduleTypeEnum(String value) {
        this.value = value;
    }

}
