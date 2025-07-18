package com.cmsr.onebase.framework.common.anyline.web;

public class BizException extends RuntimeException{
    private final String statusCode;

    public BizException(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public BizException(StatusCode statusCode) {
        super(statusCode.getDesc());
        this.statusCode = statusCode.getCode();
    }


    public static BizException of(String statusCode, String errMessage){
        return new BizException(statusCode, errMessage);
    }

    public String getStatusCode(){
        return this.statusCode;
    }

    public String getMessage(){
        return super.getMessage();
    }
}
