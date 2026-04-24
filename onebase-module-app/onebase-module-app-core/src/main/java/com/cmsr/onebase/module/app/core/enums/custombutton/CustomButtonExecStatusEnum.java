package com.cmsr.onebase.module.app.core.enums.custombutton;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomButtonExecStatusEnum {

    RUNNING("RUNNING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String code;
}
