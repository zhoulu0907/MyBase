package com.cmsr.onebase.module.bpm.core.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author：gaoqi
 * @Date：2026/2/3 11:00
 */
public enum InstanceEnum {

    BPMT_ITLE("bpmTitle", "流程标题 "),
    INITIATOR_ID("initiatorId", "发起人ID"),
    INITIATOR_DEPT_ID("initiatorDeptId", "发起部门ID"),
    SUBMIT_TIME("submitTime", "发起时间"),
    CREATE_TIME("createTime", "创建时间"),
    UPDATE_TIME("updateTime", "更新时间");

    private final String code;
    private final String desc;

    InstanceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    public static InstanceEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (InstanceEnum value : InstanceEnum.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid InstanceEnum code: " + code);
    }
}
