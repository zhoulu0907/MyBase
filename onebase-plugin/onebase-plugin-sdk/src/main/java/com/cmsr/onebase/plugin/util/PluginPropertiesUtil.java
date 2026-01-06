package com.cmsr.onebase.plugin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 插件属性工具类
 * <p>
 * 提供读取 plugin.properties 文件的工具方法，
 * 用于获取插件 ID 和版本信息。
 * </p>
 * <p>
 * 此工具类在所有模式下都可用：
 * <ul>
 * <li>Dev 模式：从 classpath 读取</li>
 * <li>Staging/Prod 模式：从插件 ZIP 的 ClassLoader 读取</li>
 * </ul>
 * </p>
 *
 * @author OneBase Team
 * @date 2026-01-06
 */
public class PluginPropertiesUtil {

    private static final Logger log = LoggerFactory.getLogger(PluginPropertiesUtil.class);

    private static final String PLUGIN_PROPERTIES_FILE = "plugin.properties";
    private static final String PROPERTY_PLUGIN_ID = "plugin.id";
    private static final String PROPERTY_PLUGIN_VERSION = "plugin.version";

    /**
     * 读取插件 ID
     * <p>
     * 通过调用类的 ClassLoader 读取 plugin.properties 文件。
     * </p>
     *
     * @param callerClass 调用类（用于获取 ClassLoader）
     * @return 插件 ID
     * @throws IllegalStateException 如果无法读取或属性不存在
     */
    public static String getPluginId(Class<?> callerClass) {
        Properties props = loadProperties(callerClass);
        String pluginId = props.getProperty(PROPERTY_PLUGIN_ID);

        if (pluginId == null || pluginId.trim().isEmpty()) {
            throw new IllegalStateException("plugin.id not found in plugin.properties");
        }

        return pluginId.trim();
    }

    /**
     * 读取插件版本
     * <p>
     * 通过调用类的 ClassLoader 读取 plugin.properties 文件。
     * </p>
     *
     * @param callerClass 调用类（用于获取 ClassLoader）
     * @return 插件版本
     * @throws IllegalStateException 如果无法读取或属性不存在
     */
    public static String getPluginVersion(Class<?> callerClass) {
        Properties props = loadProperties(callerClass);
        String version = props.getProperty(PROPERTY_PLUGIN_VERSION);

        if (version == null || version.trim().isEmpty()) {
            throw new IllegalStateException("plugin.version not found in plugin.properties");
        }

        return version.trim();
    }

    /**
     * 读取插件属性
     * <p>
     * 通过调用类的 ClassLoader 读取 plugin.properties 文件。
     * </p>
     *
     * @param callerClass 调用类（用于获取 ClassLoader）
     * @return Properties 对象
     * @throws IllegalStateException 如果无法读取文件
     */
    public static Properties loadProperties(Class<?> callerClass) {
        try {
            // 通过调用类的 ClassLoader 读取 plugin.properties
            // Dev 模式：从 classpath 读取
            // Staging/Prod 模式：从插件 ZIP 的 ClassLoader 读取
            InputStream is = callerClass.getClassLoader()
                    .getResourceAsStream(PLUGIN_PROPERTIES_FILE);

            if (is == null) {
                throw new IllegalStateException(
                        "Cannot find " + PLUGIN_PROPERTIES_FILE + " in classpath");
            }

            Properties props = new Properties();
            props.load(is);

            log.debug("Loaded plugin properties from ClassLoader: {}",
                    callerClass.getClassLoader());

            return props;

        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + PLUGIN_PROPERTIES_FILE, e);
        }
    }
}
