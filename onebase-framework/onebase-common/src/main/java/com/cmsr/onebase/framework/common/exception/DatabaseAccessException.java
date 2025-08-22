package com.cmsr.onebase.framework.common.exception;

public class DatabaseAccessException extends RuntimeException {

    private final Integer code;

    public DatabaseAccessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public DatabaseAccessException(ErrorCode errorCode, Exception ex) {
        super(errorCode.getMsg(), ex);
        this.code = errorCode.getCode();
    }

    public Integer getCode() {
        return code;
    }


}
