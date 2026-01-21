package com.cmsr.onebase.plugin.sandbox.config;

import com.cmsr.onebase.plugin.sandbox.model.PluginPermissions;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 沙箱配置属性
 * <p>
 * 通过 YAML 配置文件配置插件沙箱的行为
 * </p>
 * <p>
 * 示例配置：
 * <pre>
 * onebase:
 *   plugin:
 *     sandbox:
 *       enabled: true
 *       default-permissions:
 *         allow-file-read: false
 *         allow-network: false
 *         max-execution-time: 60000
 *         max-threads: 10
 * </pre>
 * </p>
 *
 * @author OneBase Plugin Team
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "onebase.plugin.sandbox")
public class SandboxProperties {

    /**
     * 是否启用沙箱
     */
    private boolean enabled = true;

    /**
     * 全局最大线程数
     */
    private int maxThreads = 100;

    /**
     * 默认权限配置
     */
    private DefaultPermissions defaultPermissions = new DefaultPermissions();

    /**
     * 插件特定权限配置
     */
    private PluginSpecificPermissions pluginPermissions = new PluginSpecificPermissions();

    /**
     * 默认权限配置
     */
    @Data
    public static class DefaultPermissions {
        private boolean allowFileRead = false;
        private boolean allowFileWrite = false;
        private boolean allowFileDelete = false;
        private boolean allowNetwork = false;
        private boolean allowReflection = false;
        private boolean allowCreateClassLoader = false;
        private boolean allowModifyThread = false;
        private boolean allowModifyThreadGroup = false;
        private long maxExecutionTime = 60000;  // 1分钟
        private int maxThreads = 10;

        /**
         * 转换为 PluginPermissions
         */
        public PluginPermissions toPluginPermissions() {
            return PermissionConverter.toPluginPermissions(
                    allowFileRead, allowFileWrite, allowFileDelete,
                    allowNetwork, allowReflection, allowCreateClassLoader,
                    allowModifyThread, allowModifyThreadGroup,
                    maxExecutionTime, maxThreads
            );
        }
    }

    /**
     * 插件特定权限配置
     */
    @Data
    public static class PluginSpecificPermissions extends java.util.HashMap<String, PluginPermissionConfig> {

        private static final long serialVersionUID = 1L;

        /**
         * 获取插件权限配置
         *
         * @param pluginId 插件ID
         * @return 权限配置，如果不存在则返回 null
         */
        public PluginPermissionConfig getPluginConfig(String pluginId) {
            return get(pluginId);
        }

        /**
         * 获取插件权限（转换为 PluginPermissions）
         *
         * @param pluginId 插件ID
         * @return 权限配置
         */
        public PluginPermissions getPluginPermissions(String pluginId) {
            PluginPermissionConfig config = get(pluginId);
            if (config == null) {
                return null;
            }
            return config.toPluginPermissions();
        }
    }

    /**
     * 插件权限配置
     */
    @Data
    public static class PluginPermissionConfig {
        private boolean allowFileRead;
        private boolean allowFileWrite;
        private boolean allowFileDelete;
        private boolean allowNetwork;
        private boolean allowReflection;
        private boolean allowCreateClassLoader;
        private boolean allowModifyThread;
        private boolean allowModifyThreadGroup;
        private long maxExecutionTime;
        private int maxThreads;

        /**
         * 转换为 PluginPermissions
         */
        public PluginPermissions toPluginPermissions() {
            return PermissionConverter.toPluginPermissions(
                    allowFileRead, allowFileWrite, allowFileDelete,
                    allowNetwork, allowReflection, allowCreateClassLoader,
                    allowModifyThread, allowModifyThreadGroup,
                    maxExecutionTime, maxThreads
            );
        }
    }

    /**
     * 权限转换器（消除重复代码）
     */
    private static class PermissionConverter {
        static PluginPermissions toPluginPermissions(
                boolean allowFileRead, boolean allowFileWrite, boolean allowFileDelete,
                boolean allowNetwork, boolean allowReflection, boolean allowCreateClassLoader,
                boolean allowModifyThread, boolean allowModifyThreadGroup,
                long maxExecutionTime, int maxThreads) {
            PluginPermissions permissions = new PluginPermissions();
            permissions.setAllowFileRead(allowFileRead);
            permissions.setAllowFileWrite(allowFileWrite);
            permissions.setAllowFileDelete(allowFileDelete);
            permissions.setAllowNetwork(allowNetwork);
            permissions.setAllowReflection(allowReflection);
            permissions.setAllowCreateClassLoader(allowCreateClassLoader);
            permissions.setAllowModifyThread(allowModifyThread);
            permissions.setAllowModifyThreadGroup(allowModifyThreadGroup);
            permissions.setMaxExecutionTime(maxExecutionTime);
            permissions.setMaxThreads(maxThreads);
            return permissions;
        }
    }
}
