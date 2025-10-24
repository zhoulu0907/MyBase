package com.cmsr.onebase.module.flow.core.enums;

/**
 * 执行结果枚举类
 *
 * @Author：huangjie
 * @Date：2025/10/22 15:46
 */
public enum ExecutionResultEnum {

    /**
     * 执行成功
     */
    SUCCESS("success"),

    /**
     * 执行失败
     */
    FAILED("failed");

    private final String code;

    ExecutionResultEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}