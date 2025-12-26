package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 终端的枚举
 *
 */
@RequiredArgsConstructor
@Getter
public enum TerminalEnum implements ArrayValuable<String> {

    UNKNOWN("unknown", "未知"), // 目的：在无法解析到 terminal 时，使用它
    WECHAT_MINI_PROGRAM("wechat_mini_program", "微信小程序"),
    PC("pc", "PC端"),
    MOBILE("mobile", "手机"),
    ;

    public static final String[] ARRAYS = Arrays.stream(values()).map(TerminalEnum::getTerminal).toArray(String[]::new);

    /**
     * 终端
     */
    private final String terminal;
    /**
     * 终端名
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }
}
