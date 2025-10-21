package com.cmsr.onebase.module.app.core.enums.auth;

/**
 * @Author：huangjie
 * @Date：2025/9/23 11:22
 * 权限范围枚举
 */
public enum AuthPermissionScopeEnum {
    
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

    ;

    private final String code;
    private final String label;

    AuthPermissionScopeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
    

}
