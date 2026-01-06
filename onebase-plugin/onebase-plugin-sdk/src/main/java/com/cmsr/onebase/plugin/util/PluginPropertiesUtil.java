package com.cmsr.onebase.plugin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
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
     * PF4J 开发模式常量
     */
    private static final String PF4J_MODE_DEVELOPMENT = "development";
    private static final String PF4J_MODE_PROPERTY = "pf4j.mode";

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
            throw new IllegalStateException("在 plugin.properties 中未找到 plugin.id");
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
            throw new IllegalStateException("在 plugin.properties 中未找到 plugin.version");
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
        InputStream is = null;
        try {
            // 1. 尝试基于 CodeSource 精准定位资源 (适用于开发模式下的目录结构)
            // 无论 pf4j.mode 是否设置，只要 CodeSource 是目录就尝试加载
            // 这解决了 Bean 初始化顺序问题（某些 Bean 在 pf4j.mode 设置前就需要加载配置）
            is = loadFromCodeSource(callerClass);

            // 2. 若 CodeSource 未找到，回退到标准 ClassLoader 加载 (生产模式)
            if (is == null) {
                is = callerClass.getClassLoader().getResourceAsStream(PLUGIN_PROPERTIES_FILE);
            }

            if (is == null) {
                throw new IllegalStateException("无法在 classpath 中找到 " + PLUGIN_PROPERTIES_FILE);
            }

            Properties props = new Properties();
            props.load(is);

            log.debug("成功加载插件属性文件: {}", callerClass.getName());
            return props;

        } catch (IOException e) {
            throw new IllegalStateException("读取 " + PLUGIN_PROPERTIES_FILE + " 失败", e);
        } finally {
            closeQuietly(is);
        }
    }

    /**
     * 判断是否为开发模式
     *
     * @return true 如果是开发模式
     */
    private static boolean isDevelopmentMode() {
        String mode = System.getProperty(PF4J_MODE_PROPERTY, "");
        return PF4J_MODE_DEVELOPMENT.equalsIgnoreCase(mode);
    }

    /**
     * 开发模式下基于 CodeSource 加载配置文件
     * <p>
     * 解决开发模式下 Classpath 污染导致 ClassLoader 无法正确定位资源的问题。
     * 通过 CodeSource 获取类所在的物理目录，直接读取同目录下的配置文件。
     * </p>
     *
     * @param callerClass 调用类
     * @return InputStream，如果无法定位则返回 null
     */
    private static InputStream loadFromCodeSource(Class<?> callerClass) {
        try {
            CodeSource codeSource = callerClass.getProtectionDomain().getCodeSource();
            if (codeSource == null || codeSource.getLocation() == null) {
                return null;
            }

            URL location = codeSource.getLocation();
            File codeDir = new File(location.toURI());

            // 仅当 CodeSource 指向目录时有效（开发模式下的 target/classes）
            if (!codeDir.isDirectory()) {
                return null;
            }

            File propFile = new File(codeDir, PLUGIN_PROPERTIES_FILE);
            if (propFile.exists()) {
                log.info("开发模式：基于 CodeSource 精准定位配置文件: {}", propFile.getAbsolutePath());
                return new FileInputStream(propFile);
            }
        } catch (URISyntaxException | IOException e) {
            log.error("开发模式下 CodeSource 定位资源失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 静默关闭流
     *
     * @param is InputStream
     */
    private static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.error("关闭时发生异常", e);
            }
        }
    }
}
