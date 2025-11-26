package com.cmsr.onebase.framework.ds.exception;

import org.slf4j.helpers.MessageFormatter;

public class DolphinschedulerException extends RuntimeException {

    public DolphinschedulerException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public DolphinschedulerException(String message, Throwable ex) {
        super(message, ex);
    }

    public static DolphinschedulerException of(String message, Object... args) {
        return new DolphinschedulerException(message, args);
    }

    public static DolphinschedulerException of(String message, Throwable ex) {
        return new DolphinschedulerException(message, ex);
    }
}
