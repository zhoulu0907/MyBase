package com.cmsr.onebase.framework.security.runtime;

/**
 * @Author：huangjie
 * @Date：2025/10/18 14:50
 */
public enum OperationEnum {

    CREATE("create", "创建"),

    EDIT("edit", "编辑"),

    DELETE("delete", "删除"),

    IMPORT("import", "导入"),

    EXPORT("export", "导出"),

    SHARE("share", "分享");

    private String code;

    private String label;

    OperationEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }


}
