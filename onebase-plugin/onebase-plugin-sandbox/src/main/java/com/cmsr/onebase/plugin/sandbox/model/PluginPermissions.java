package com.cmsr.onebase.plugin.sandbox.model;

import lombok.Data;

/**
 * 插件权限配置
 * <p>
 * 用于控制插件在沙箱中的权限范围
 * </p>
 */
@Data
public class PluginPermissions {

    /**
     * 是否允许文件读取
     */
    private boolean allowFileRead = false;

    /**
     * 是否允许文件写入
     */
    private boolean allowFileWrite = false;

    /**
     * 是否允许文件删除
     */
    private boolean allowFileDelete = false;

    /**
     * 是否允许网络操作
     */
    private boolean allowNetwork = false;

    /**
     * 是否允许反射
     */
    private boolean allowReflection = false;

    /**
     * 是否允许创建类加载器
     */
    private boolean allowCreateClassLoader = false;

    /**
     * 是否允许修改线程
     */
    private boolean allowModifyThread = false;

    /**
     * 是否允许修改线程组
     */
    private boolean allowModifyThreadGroup = false;

    /**
     * 最大执行时间（毫秒）
     * 默认 60 秒
     */
    private long maxExecutionTime = 60000;

    /**
     * 最大线程数
     * 默认 10
     */
    private int maxThreads = 10;

    /**
     * 创建严格权限配置（拒绝所有）
     */
    public static PluginPermissions strict() {
        return new PluginPermissions();
    }

    /**
     * 创建宽松权限配置（允许所有）
     */
    public static PluginPermissions permissive() {
        PluginPermissions permissions = new PluginPermissions();
        permissions.setAllowFileRead(true);
        permissions.setAllowFileWrite(true);
        permissions.setAllowFileDelete(true);
        permissions.setAllowNetwork(true);
        permissions.setAllowReflection(true);
        permissions.setAllowCreateClassLoader(true);
        permissions.setAllowModifyThread(true);
        permissions.setAllowModifyThreadGroup(true);
        return permissions;
    }

    /**
     * 创建默认权限配置
     */
    public static PluginPermissions defaultPermissions() {
        return new PluginPermissions();
    }
}
