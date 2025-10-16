package com.cmsr.onebase.framework.core;

/**
 * DolphinScheduler 客户端异常
 *
 * @author matianyu
 * @date 2025-10-15
 */
public class DolphinSchedulerClientException extends RuntimeException {
    public DolphinSchedulerClientException(String message) {
        super(message);
    }
    public DolphinSchedulerClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
