package com.cmsr.onebase.module.bpm.core.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author：gaoqi
 * @Date：2026/2/3 11:00
 */
public enum PreNodeEnum {

    APPROVAL_RESULT("approvalResult", "审批结果 "),
    APPROVER_ID("approverId", "审批人ID"),
    APPROVAL_TIME("approvalTime", "审批时间"),
    APPROVER_DEPT_ID("approverDeptId", "审批人部门ID");

    private final String code;
    private final String desc;

    PreNodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PreNodeEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (PreNodeEnum value : PreNodeEnum.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid PreNodeEnum code: " + code);
    }
}
