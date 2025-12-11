package com.cmsr.exception;

/**
 * 数据异常类
 */
public class DEException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public DEException(String message) {
        super(message);
    }
    
    public DEException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DEException(Throwable cause) {
        super(cause);
    }
    
    public static DEException throwException(String message) {
        return new DEException(message);
    }
} 