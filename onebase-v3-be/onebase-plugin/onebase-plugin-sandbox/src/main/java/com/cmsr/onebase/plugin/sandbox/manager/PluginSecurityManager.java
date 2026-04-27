package com.cmsr.onebase.plugin.sandbox.manager;

import com.cmsr.onebase.plugin.sandbox.model.PluginPermissions;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketPermission;
import java.security.Permission;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件安全管理器
 * <p>
 * 通过自定义 SecurityManager 实现插件代码的权限控制。
 * 只对插件代码进行权限检查，宿主代码不受影响。
 * </p>
 * <p>
 * 使用方式：
 * <pre>
 * securityManager.enterSandbox("plugin-id");
 * try {
 *     // 插件代码
 * } finally {
 *     securityManager.exitSandbox();
 * }
 * </pre>
 * </p>
 *
 * @author OneBase Plugin Team
 * @since 1.0.0
 */
@Slf4j
public class PluginSecurityManager extends SecurityManager {

    /**
     * 线程本地变量：标识是否在插件沙箱中
     */
    private final ThreadLocal<Boolean> inPlugin = ThreadLocal.withInitial(() -> false);

    /**
     * 线程本地变量：当前插件ID
     */
    private final ThreadLocal<String> currentPluginId = new ThreadLocal<>();

    /**
     * 插件权限配置
     */
    private final Map<String, PluginPermissions> pluginPermissions = new ConcurrentHashMap<>();

    /**
     * 进入插件沙箱
     *
     * @param pluginId 插件ID
     */
    public void enterSandbox(String pluginId) {
        log.debug("插件 {} 进入沙箱", pluginId);
        inPlugin.set(true);
        currentPluginId.set(pluginId);
    }

    /**
     * 退出插件沙箱
     */
    public void exitSandbox() {
        String pluginId = currentPluginId.get();
        log.debug("插件 {} 退出沙箱", pluginId);
        inPlugin.set(false);
        currentPluginId.remove();
    }

    /**
     * 检查当前是否在插件沙箱中
     *
     * @return true 如果在沙箱中
     */
    public boolean isInSandbox() {
        return inPlugin.get();
    }

    /**
     * 获取当前插件ID
     *
     * @return 当前插件ID，如果不在沙箱中则返回 null
     */
    public String getCurrentPluginId() {
        return currentPluginId.get();
    }

    @Override
    public void checkPermission(Permission perm) {
        // 只检查插件代码的权限
        if (!isInSandbox()) {
            return; // 宿主代码不做限制
        }

        String pluginId = getCurrentPluginId();
        PluginPermissions permissions = pluginPermissions.get(pluginId);

        if (permissions == null) {
            // 使用默认权限配置
            permissions = PluginPermissions.defaultPermissions();
            pluginPermissions.put(pluginId, permissions);
        }

        // 根据权限类型进行检查
        checkPermissionByType(perm, permissions, pluginId);
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        checkPermission(perm);
    }

    /**
     * 根据权限类型进行检查
     *
     * @param perm        权限对象
     * @param permissions 权限配置
     * @param pluginId    插件ID
     */
    private void checkPermissionByType(Permission perm, PluginPermissions permissions, String pluginId) {
        String permClass = perm.getClass().getName();
        String permName = perm.getName();

        log.debug("插件 {} 请求权限: {} ({})", pluginId, permClass, permName);

        // 文件权限检查
        if (perm instanceof java.io.FilePermission) {
            checkFilePermission((java.io.FilePermission) perm, permissions, pluginId);
            return;
        }

        // 网络权限检查
        if (perm instanceof SocketPermission) {
            checkNetworkPermission(perm, permissions, pluginId);
            return;
        }

        // 反射权限检查
        if (perm instanceof java.lang.reflect.ReflectPermission) {
            checkReflectionPermission(perm, permissions, pluginId);
            return;
        }

        // 运行时权限检查
        if (perm instanceof RuntimePermission) {
            checkRuntimePermission((RuntimePermission) perm, permissions, pluginId);
            return;
        }

        // 其他权限默认拒绝
        throw new SecurityException(String.format(
                "插件 %s 禁止此操作: %s (%s)",
                pluginId, permClass, permName
        ));
    }

    /**
     * 检查文件权限
     *
     * @param perm        文件权限
     * @param permissions 权限配置
     * @param pluginId    插件ID
     */
    private void checkFilePermission(java.io.FilePermission perm,
                                      PluginPermissions permissions,
                                      String pluginId) {
        String actions = perm.getActions();
        String path = perm.getName();

        log.debug("插件 {} 文件操作: {} ({})", pluginId, path, actions);

        if (actions.contains("read") && !permissions.isAllowFileRead()) {
            throw securityException(pluginId, "读文件", path);
        }
        if (actions.contains("write") && !permissions.isAllowFileWrite()) {
            throw securityException(pluginId, "写文件", path);
        }
        if (actions.contains("delete") && !permissions.isAllowFileDelete()) {
            throw securityException(pluginId, "删除文件", path);
        }
    }

    /**
     * 创建安全异常
     */
    private SecurityException securityException(String pluginId, String operation, String target) {
        return new SecurityException(
                String.format("插件 %s 禁止%s: %s", pluginId, operation, target)
        );
    }

    /**
     * 创建安全异常
     */
    private SecurityException securityException(String pluginId, String message) {
        return new SecurityException(String.format("插件 %s %s", pluginId, message));
    }

    /**
     * 检查网络权限
     *
     * @param perm        网络权限
     * @param permissions 权限配置
     * @param pluginId    插件ID
     */
    private void checkNetworkPermission(Permission perm,
                                         PluginPermissions permissions,
                                         String pluginId) {
        if (!permissions.isAllowNetwork()) {
            throw securityException(pluginId, "禁止网络操作");
        }
    }

    /**
     * 检查反射权限
     *
     * @param perm        反射权限
     * @param permissions 权限配置
     * @param pluginId    插件ID
     */
    private void checkReflectionPermission(Permission perm,
                                           PluginPermissions permissions,
                                           String pluginId) {
        if (!permissions.isAllowReflection()) {
            throw securityException(pluginId, "禁止反射操作");
        }
    }

    /**
     * 检查运行时权限
     *
     * @param perm        运行时权限
     * @param permissions 权限配置
     * @param pluginId    插件ID
     */
    private void checkRuntimePermission(RuntimePermission perm,
                                       PluginPermissions permissions,
                                       String pluginId) {
        String name = perm.getName();

        log.debug("插件 {} 运行时权限: {}", pluginId, name);

        if ("createClassLoader".equals(name) && !permissions.isAllowCreateClassLoader()) {
            throw securityException(pluginId, "禁止创建类加载器");
        }
        if ("modifyThread".equals(name) && !permissions.isAllowModifyThread()) {
            throw securityException(pluginId, "禁止修改线程");
        }
        if ("modifyThreadGroup".equals(name) && !permissions.isAllowModifyThreadGroup()) {
            throw securityException(pluginId, "禁止修改线程组");
        }
        // 禁止设置安全管理器（防止插件绕过安全检查）
        if ("setSecurityManager".equals(name)) {
            throw securityException(pluginId, "禁止设置安全管理器");
        }
    }

    /**
     * 配置插件权限
     *
     * @param pluginId    插件ID
     * @param permissions 权限配置
     */
    public void configurePlugin(String pluginId, PluginPermissions permissions) {
        log.info("配置插件权限: pluginId={}, permissions={}", pluginId, permissions);
        pluginPermissions.put(pluginId, permissions);
    }

    /**
     * 移除插件配置
     *
     * @param pluginId 插件ID
     */
    public void removePlugin(String pluginId) {
        log.info("移除插件权限配置: pluginId={}", pluginId);
        pluginPermissions.remove(pluginId);
    }

    /**
     * 获取插件权限配置
     *
     * @param pluginId 插件ID
     * @return 权限配置，如果不存在则返回默认配置
     */
    public PluginPermissions getPermissions(String pluginId) {
        return pluginPermissions.getOrDefault(pluginId, PluginPermissions.defaultPermissions());
    }
}
