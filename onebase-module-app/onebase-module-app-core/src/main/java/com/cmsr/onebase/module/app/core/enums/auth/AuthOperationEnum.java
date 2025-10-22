package com.cmsr.onebase.module.app.core.enums.auth;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/15 16:16
 */
public enum AuthOperationEnum {

    CREATE("create", "创建"),

    EDIT("edit", "编辑"),

    DELETE("delete", "删除"),

    IMPORT("import", "导入"),

    EXPORT("export", "导出"),

    SHARE("share", "分享");

    private String operation;

    private String label;

    private Data data;

    AuthOperationEnum(String operation, String label) {
        this.operation = operation;
        this.label = label;
        this.data = new Data(operation, label);
    }

    public static List<String> getOperations() {
        return List.of(
                CREATE.operation,
                EDIT.operation,
                DELETE.operation,
                IMPORT.operation,
                EXPORT.operation,
                SHARE.operation
        );
    }

    public static List<Data> getDataList() {
        return List.of(
                CREATE.data,
                EDIT.data,
                DELETE.data,
                IMPORT.data,
                EXPORT.data,
                SHARE.data
        );
    }

    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        private String operation;
        private String label;
    }
}
