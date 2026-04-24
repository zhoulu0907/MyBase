package com.cmsr.onebase.module.app.core.enums.custombutton;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CustomButtonActionTypeEnum {

    UPDATE_FORM("UPDATE_FORM", "修改当前表单"),
    CREATE_RELATED_RECORD("CREATE_RELATED_RECORD", "新建关联表单"),
    TRIGGER_FLOW("TRIGGER_FLOW", "执行自动化流"),
    OPEN_PAGE("OPEN_PAGE", "打开页面");

    private final String code;
    private final String desc;

    public static boolean exists(String code) {
        return Arrays.stream(values()).anyMatch(v -> v.code.equalsIgnoreCase(code));
    }
}
