package com.cmsr.onebase.framework.remote.dto;
import lombok.Data;

/** 通用 HTTP 响应包装 DTO */
@Data
public class HttpRestResultDTO<T> {
    private Integer code;
    private String msg;
    private T data;
}

