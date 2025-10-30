package com.cmsr.onebase.module.app.api.security.bo;

/**
 * @Author：huangjie
 * @Date：2025/9/23 11:22
 * 权限范围枚举
 */
public enum DataPermissionLevel {

    /**
     * 本人
     */
    SELF("self", "本人"),

    /**
     * 本人及下属员工
     */
    SELF_AND_SUBORDINATES("selfAndSubordinates", "本人及下属员工"),

    /**
     * 当前员工所在主部门
     */
    MAIN_DEPARTMENT("mainDepartment", "当前员工所在主部门"),

    /**
     * 当前员工所在主部门及下级部门
     */
    MAIN_DEPARTMENT_AND_SUBS("mainDepartmentAndSubs", "当前员工所在主部门及下级部门"),

    /**
     * 指定部门
     */
    SPECIFIED_DEPARTMENT("specifiedDepartment", "指定部门"),

    /**
     * 指定人员
     */
    SPECIFIED_PERSON("specifiedPerson", "指定人员"),

    /**
     * 未知
     */
    UNKNOW("unknown", "未知");;


    private final String code;
    private final String label;

    DataPermissionLevel(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static DataPermissionLevel fromCode(String scopeLevel) {
        for (DataPermissionLevel value : DataPermissionLevel.values()) {
            if (value.code.equals(scopeLevel)) {
                return value;
            }
        }
        return UNKNOW;
    }
    

}
