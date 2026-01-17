package com.cmsr.onebase.plugin.sandbox.model;

import lombok.Data;

/**
 * 沙箱执行结果
 *
 * @param <T> 结果类型
 */
@Data
public class SandboxResult<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 结果数据
     */
    private T data;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 执行时长（毫秒）
     */
    private long duration;

    /**
     * 是否超时
     */
    private boolean timeout;

    /**
     * 是否安全违规
     */
    private boolean securityViolation;

    /**
     * 创建成功结果
     */
    public static <T> SandboxResult<T> success(T data, long duration) {
        SandboxResult<T> result = new SandboxResult<>();
        result.success = true;
        result.data = data;
        result.duration = duration;
        return result;
    }

    /**
     * 创建失败结果
     */
    private static <T> SandboxResult<T> failure(String error) {
        SandboxResult<T> result = new SandboxResult<>();
        result.success = false;
        result.error = error;
        return result;
    }

    /**
     * 创建错误结果
     */
    public static <T> SandboxResult<T> error(String error) {
        return failure(error);
    }

    /**
     * 创建超时结果
     */
    public static <T> SandboxResult<T> timeout(String error) {
        SandboxResult<T> result = failure(error);
        result.timeout = true;
        return result;
    }

    /**
     * 创建安全违规结果
     */
    public static <T> SandboxResult<T> securityViolation(String error) {
        SandboxResult<T> result = failure(error);
        result.securityViolation = true;
        return result;
    }

}
