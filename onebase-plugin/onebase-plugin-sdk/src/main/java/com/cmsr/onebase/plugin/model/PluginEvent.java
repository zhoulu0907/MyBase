package com.cmsr.onebase.plugin.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 插件事件
 * <p>
 * 封装平台发出的事件信息，供事件监听器使用。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public class PluginEvent {

    /**
     * 事件ID（唯一标识）
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件来源（如：entity, workflow, system等）
     */
    private String source;

    /**
     * 事件发生时间
     */
    private LocalDateTime timestamp;

    /**
     * 事件数据
     */
    private Map<String, Object> data;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 操作用户ID
     */
    private Long userId;

    public PluginEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public PluginEvent(String eventType, Map<String, Object> data) {
        this.eventType = eventType;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建实体事件
     *
     * @param eventType  事件类型
     * @param entityCode 实体编码
     * @param entityId   实体ID
     * @param data       事件数据
     * @return 插件事件
     */
    public static PluginEvent entityEvent(String eventType, String entityCode, Long entityId, Map<String, Object> data) {
        PluginEvent event = new PluginEvent(eventType, data);
        event.setSource("entity");
        if (data != null) {
            data.put("entityCode", entityCode);
            data.put("entityId", entityId);
        }
        return event;
    }

    /**
     * 创建流程事件
     *
     * @param eventType    事件类型
     * @param processKey   流程标识
     * @param processId    流程实例ID
     * @param data         事件数据
     * @return 插件事件
     */
    public static PluginEvent workflowEvent(String eventType, String processKey, String processId, Map<String, Object> data) {
        PluginEvent event = new PluginEvent(eventType, data);
        event.setSource("workflow");
        if (data != null) {
            data.put("processKey", processKey);
            data.put("processId", processId);
        }
        return event;
    }

    /**
     * 创建系统事件
     *
     * @param eventType 事件类型
     * @param data      事件数据
     * @return 插件事件
     */
    public static PluginEvent systemEvent(String eventType, Map<String, Object> data) {
        PluginEvent event = new PluginEvent(eventType, data);
        event.setSource("system");
        return event;
    }

    // ==================== 便捷获取数据方法 ====================

    /**
     * 获取事件数据中的字符串值
     *
     * @param key 键
     * @return 字符串值
     */
    public String getString(String key) {
        if (data == null) {
            return null;
        }
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取事件数据中的Long值
     *
     * @param key 键
     * @return Long值
     */
    public Long getLong(String key) {
        if (data == null) {
            return null;
        }
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return null;
    }

    /**
     * 获取事件数据中的对象值
     *
     * @param key 键
     * @param <T> 值类型
     * @return 对象值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (data == null) {
            return null;
        }
        return (T) data.get(key);
    }

    // ==================== Getter/Setter ====================

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
