package com.cmsr.onebase.framework.remote.dto;

/** 通用 HTTP 响应包装 DTO */
public class HttpRestResultDTO<T> {
    private Integer code;
    private String msg;
    private T data;

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}

