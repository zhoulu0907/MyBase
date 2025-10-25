package com.cmsr.onebase.framework.ds.model.common;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;

    private String msg;

    private T data;

    private Boolean success;

    private Boolean failed;

}
