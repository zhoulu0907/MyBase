package com.cmsr.onebase.module.app.core.enums.protocol.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName GlobalData
 * @Description 全局数据，包含当前用户、应用、主题等信息
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalData {

    /**
     * 当前用户信息
     */
    private Map<String, Object> currentUser;

    /**
     * 当前应用信息
     */
    private Map<String, Object> currentApp;

    /**
     * 当前主题信息
     */
    private Map<String, Object> currentTheme;
}
