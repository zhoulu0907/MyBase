package com.cmsr.onebase.module.app.api.security.bo;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/23 11:22
 * 权限范围枚举
 */
public enum DataPermissionTag {
    /**
     * 全部数据
     */
    ALL_DATA("allData", "全部数据"),

    /**
     * 本人提交
     */
    OWN_SUBMIT("ownSubmit", "本人提交"),

    /**
     * 本部门提交
     */
    DEPARTMENT_SUBMIT("departmentSubmit", "本部门提交"),

    /**
     * 下级部门提交
     */
    SUB_DEPARTMENT_SUBMIT("subDepartmentSubmit", "下级部门提交"),

    /**
     * 自定义条件
     */
    CUSTOM_CONDITION("customCondition", "自定义条件"),

    UNKNOW("unknown", "未知");

    private final String code;
    private final String label;

    DataPermissionTag(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static List<DataPermissionTag> createTags(List<String> scopeTags) {
        return scopeTags.stream().map(tag -> {
            for (DataPermissionTag value : DataPermissionTag.values()) {
                if (value.code.equals(tag)) {
                    return value;
                }
            }
            return UNKNOW;
        }).toList();
    }

}
