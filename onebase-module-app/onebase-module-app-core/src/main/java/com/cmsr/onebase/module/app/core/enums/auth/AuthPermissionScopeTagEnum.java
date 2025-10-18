package com.cmsr.onebase.module.app.core.enums.auth;

/**
 * @Author：huangjie
 * @Date：2025/9/23 11:22
 * 权限范围枚举
 */
public enum AuthPermissionScopeTagEnum {
    /**
     * 全部数据
     */
    ALL_DATA("allData", "全部数据"),
    
    /**
     * 本人提交（默认勾选）
     */
    OWN_SUBMIT("ownSubmit", "本人提交（默认勾选）"),
    
    /**
     * 本部门提交
     */
    DEPARTMENT_SUBMIT("departmentSubmit", "本部门提交"),
    
    /**
     * 下级部门提交
     */
    SUBORDINATE_DEPARTMENT_SUBMIT("subordinateDepartmentSubmit", "下级部门提交"),
    
    /**
     * 自定义条件
     */
    CUSTOM_CONDITION("customCondition", "自定义条件");

    private final String code;
    private final String label;

    AuthPermissionScopeTagEnum(String code, String label) {
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
