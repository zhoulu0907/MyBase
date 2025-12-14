package com.cmsr.onebase.plugin.context;

import com.cmsr.onebase.plugin.service.DataService;
import com.cmsr.onebase.plugin.service.FileService;
import com.cmsr.onebase.plugin.service.UserService;

import java.util.Map;

/**
 * 插件运行时上下文
 * <p>
 * 提供插件运行时所需的上下文信息和平台服务访问能力。
 * 包括当前租户、用户、请求信息以及平台服务代理。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public interface PluginContext {

    // ==================== 身份信息 ====================

    /**
     * 获取当前插件ID
     *
     * @return 插件ID
     */
    String getPluginId();

    /**
     * 获取当前租户ID
     *
     * @return 租户ID
     */
    String getTenantId();

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    String getUserId();

    /**
     * 获取请求追踪ID
     *
     * @return 追踪ID，用于链路追踪
     */
    String getTraceId();

    // ==================== 请求信息 ====================

    /**
     * 获取请求参数（Query参数）
     *
     * @return 参数Map
     */
    Map<String, Object> getRequestParams();

    /**
     * 获取请求体（POST Body）
     *
     * @return 请求体Map
     */
    Map<String, Object> getRequestBody();

    /**
     * 获取请求头
     *
     * @param name 头名称
     * @return 头值
     */
    String getHeader(String name);

    // ==================== 上下文属性 ====================

    /**
     * 获取上下文属性
     *
     * @param key 属性键
     * @return 属性值
     */
    Object getAttribute(String key);

    /**
     * 设置上下文属性
     *
     * @param key   属性键
     * @param value 属性值
     */
    void setAttribute(String key, Object value);

    /**
     * 获取所有上下文属性
     *
     * @return 属性Map
     */
    Map<String, Object> getAttributes();

    // ==================== 平台服务 ====================

    /**
     * 获取平台服务
     * <p>
     * 支持的服务类型：
     * <ul>
     *     <li>{@link DataService} - 数据操作服务</li>
     *     <li>{@link UserService} - 用户信息服务</li>
     *     <li>{@link FileService} - 文件操作服务</li>
     * </ul>
     * </p>
     *
     * @param serviceClass 服务接口Class
     * @param <T>          服务类型
     * @return 服务实例
     */
    <T> T getService(Class<T> serviceClass);

    /**
     * 获取数据服务（快捷方法）
     *
     * @return 数据服务
     */
    default DataService getDataService() {
        return getService(DataService.class);
    }

    /**
     * 获取用户服务（快捷方法）
     *
     * @return 用户服务
     */
    default UserService getUserService() {
        return getService(UserService.class);
    }

    /**
     * 获取文件服务（快捷方法）
     *
     * @return 文件服务
     */
    default FileService getFileService() {
        return getService(FileService.class);
    }
}
