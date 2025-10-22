package com.cmsr.onebase.framework.dolphins.exception;

import lombok.Getter;

/**
 * DolphinScheduler 统一异常类
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Getter
public class DolphinSchedulerException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * HTTP 状态码
     */
    private final int httpStatus;

    /**
     * 错误信息
     */
    private final String errorMsg;

    /**
     * 构造方法 - 基于错误码枚举
     *
     * @param errorCode 错误码枚举
     */
    public DolphinSchedulerException(DolphinSchedulerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getMessage();
        this.httpStatus = 0;
    }

    /**
     * 构造方法 - 基于错误码枚举和自定义消息
     *
     * @param errorCode 错误码枚举
     * @param message 自定义错误信息
     */
    public DolphinSchedulerException(DolphinSchedulerErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getCode();
        this.errorMsg = message;
        this.httpStatus = 0;
    }

    /**
     * 构造方法 - 基于错误码枚举、自定义消息和原因
     *
     * @param errorCode 错误码枚举
     * @param message 自定义错误信息
     * @param cause 原因
     */
    public DolphinSchedulerException(DolphinSchedulerErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode.getCode();
        this.errorMsg = message;
        this.httpStatus = 0;
    }

    /**
     * 构造方法 - 基于 HTTP 状态码和错误信息
     *
     * @param httpStatus HTTP 状态码
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     */
    public DolphinSchedulerException(int httpStatus, String errorCode, String errorMsg) {
        super(errorMsg);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 构造方法 - 基于 HTTP 状态码、错误信息和原因
     *
     * @param httpStatus HTTP 状态码
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     * @param cause 原因
     */
    public DolphinSchedulerException(int httpStatus, String errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "DolphinSchedulerException{" +
                "errorCode='" + errorCode + '\'' +
                ", httpStatus=" + httpStatus +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
