package com.cmsr.onebase.plugin.core;

import com.cmsr.onebase.plugin.api.DataProcessor;
import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.api.HttpHandler;

import java.util.Arrays;
import java.util.List;

/**
 * 扩展点常量定义
 * <p>
 * 集中定义所有支持的扩展点接口，便于Maven插件和运行时扫描统一使用。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-15
 */
public class ExtensionPointConstants {

    /**
     * 所有扩展点接口的Class列表
     */
    public static final List<Class<?>> EXTENSION_POINT_CLASSES = Arrays.asList(
            DataProcessor.class,
            EventListener.class,
            HttpHandler.class
    );

    /**
     * 所有扩展点接口的类名列表（用于ASM扫描）
     */
    public static final List<String> EXTENSION_POINT_CLASS_NAMES = Arrays.asList(
            DataProcessor.class.getName(),
            EventListener.class.getName(),
            HttpHandler.class.getName()
    );

    private ExtensionPointConstants() {
        // 工具类，禁止实例化
    }
}
