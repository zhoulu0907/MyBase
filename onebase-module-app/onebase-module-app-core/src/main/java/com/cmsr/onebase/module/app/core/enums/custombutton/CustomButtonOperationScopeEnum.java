package com.cmsr.onebase.module.app.core.enums.custombutton;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CustomButtonOperationScopeEnum {

    SINGLE("single", "单条操作"),
    BATCH("batch", "批量操作");

    private final String code;
    private final String desc;

    public static boolean exists(String code) {
        return Arrays.stream(values()).anyMatch(v -> v.code.equalsIgnoreCase(code));
    }
}
