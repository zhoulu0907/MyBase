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
    SELF("本人", "self"),
    
    /**
     * 本人及下属员工
     */
    SELF_AND_SUBORDINATES("本人及下属员工", "selfAndSubordinates"),
    
    /**
     * 当前员工所在主部门
     */
    MAIN_DEPARTMENT("当前员工所在主部门", "mainDepartment"),
    
    /**
     * 当前员工所在主部门及下级部门
     */
    MAIN_DEPARTMENT_AND_SUBS("当前员工所在主部门及下级部门", "mainDepartmentAndSubs"),
    
    /**
     * 指定部门
     */
    SPECIFIED_DEPARTMENT("指定部门", "specifiedDepartment"),
    
    /**
     * 指定人员
     */
    SPECIFIED_PERSON("指定人员", "specifiedPerson"),
    
    /**
     * 当前人员身份信息
     */
    IDENTITY_INFO("当前人员身份信息", "identityInfo"),
    
    /**
     * 全部
     */
    ALL("全部", "all");
    
    private final String label;
    private final String value;
    
    AuthPermissionScopeEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getValue() {
        return value;
    }
}
