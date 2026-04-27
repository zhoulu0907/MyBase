package com.cmsr.onebase.module.app.core.enums.custombutton;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomButtonStatusEnum {

    ENABLE("ENABLE"),
    DISABLE("DISABLE");

    private final String code;
}
