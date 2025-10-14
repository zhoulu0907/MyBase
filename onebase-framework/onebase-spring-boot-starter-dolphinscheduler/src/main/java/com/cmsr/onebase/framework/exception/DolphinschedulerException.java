package com.cmsr.onebase.framework.exception;

public class DolphinschedulerException extends RuntimeException {
    public DolphinschedulerException(String message, Object... args) {
        super(String.format(message, args));
    }

    public DolphinschedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DolphinschedulerException of(String message, Object... args) {
        return new DolphinschedulerException(message, args);
    }

    public static DolphinschedulerException of(String message, Throwable cause) {
        return new DolphinschedulerException(message, cause);
    }
}
