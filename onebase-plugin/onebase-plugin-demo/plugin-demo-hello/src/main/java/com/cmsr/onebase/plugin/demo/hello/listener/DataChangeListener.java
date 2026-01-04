package com.cmsr.onebase.plugin.demo.hello.listener;

import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.PluginEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 数据变更事件监听器示例
 * <p>
 * 演示如何实现事件监听器扩展点，监听数据的创建、更新、删除事件
 * </p>
 *
 * @author OneBase Team
 * @date 2025-12-18
 */
public class DataChangeListener implements EventListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String[] eventTypes() {
        return new String[]{"DATA_CREATED", "DATA_UPDATED", "DATA_DELETED"};
    }

    @Override
    public int order() {
        // 较高优先级，确保在其他监听器之前执行
        return 10;
    }

    @Override
    public void onEvent(PluginContext ctx, PluginEvent event) {
        String userId = ctx.getUserId();
        String tenantId = ctx.getTenantId();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String eventType = event.getEventType();
        Map<String, Object> data = event.getData();

        // 记录审计日志
        System.out.printf("[%s] [审计日志] 租户=%s, 用户=%s, 事件=%s%n",
                timestamp, tenantId, userId, eventType);

        // 根据事件类型执行不同的业务逻辑
        switch (eventType) {
            case "DATA_CREATED" -> handleDataCreated(data, userId);
            case "DATA_UPDATED" -> handleDataUpdated(data, userId);
            case "DATA_DELETED" -> handleDataDeleted(data, userId);
        }
    }

    private void handleDataCreated(Map<String, Object> data, String userId) {
        System.out.println("  -> 数据创建: " + data);
        // 这里可以实现：
        // 1. 发送通知给相关人员
        // 2. 同步数据到其他系统
        // 3. 触发工作流
    }

    private void handleDataUpdated(Map<String, Object> data, String userId) {
        System.out.println("  -> 数据更新: " + data);
        // 这里可以实现：
        // 1. 记录变更历史
        // 2. 检查数据合规性
        // 3. 同步更新到缓存
    }

    private void handleDataDeleted(Map<String, Object> data, String userId) {
        System.out.println("  -> 数据删除: " + data);
        // 这里可以实现：
        // 1. 软删除转存到归档表
        // 2. 清理关联数据
        // 3. 发送删除通知
    }
}
