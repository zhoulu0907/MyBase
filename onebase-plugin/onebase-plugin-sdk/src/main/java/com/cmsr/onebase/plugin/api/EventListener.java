package com.cmsr.onebase.plugin.api;

import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.PluginEvent;
import org.pf4j.ExtensionPoint;

/**
 * 事件监听器扩展点
 * <p>
 * 用于监听平台业务事件，如数据创建、更新、删除、流程状态变更等。
 * 可以实现数据同步、业务通知、审计日志等功能。
 * </p>
 *
 * <pre>
 * 使用示例：
 * {@code
 * public class DataSyncListener implements EventListener {
 *     @Override
 *     public String[] eventTypes() {
 *         return new String[]{"DATA_CREATED", "DATA_UPDATED"};
 *     }
 *
 *     @Override
 *     public void onEvent(PluginContext ctx, PluginEvent event) {
 *         syncToExternalSystem(event.getData());
 *     }
 * }
 * }
 * </pre>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public interface EventListener extends ExtensionPoint {

    /**
     * 监听的事件类型列表
     * <p>
     * 平台事件类型包括：
     * - DATA_CREATED: 数据创建
     * - DATA_UPDATED: 数据更新
     * - DATA_DELETED: 数据删除
     * - FLOW_STARTED: 流程启动
     * - FLOW_COMPLETED: 流程完成
     * - FLOW_NODE_APPROVED: 流程节点审批
     * - USER_LOGIN: 用户登录
     * - USER_LOGOUT: 用户登出
     * </p>
     *
     * @return 事件类型数组
     */
    String[] eventTypes();

    /**
     * 监听器描述
     *
     * @return 描述信息
     */
    default String description() {
        return "";
    }

    /**
     * 执行顺序（数值越小越先执行）
     *
     * @return 顺序值，默认100
     */
    default int order() {
        return 100;
    }

    /**
     * 是否异步执行
     * <p>
     * 异步执行不会阻塞主流程，适合耗时操作
     * </p>
     *
     * @return true表示异步执行
     */
    default boolean async() {
        return false;
    }

    /**
     * 处理事件
     *
     * @param ctx   插件上下文
     * @param event 事件对象
     */
    void onEvent(PluginContext ctx, PluginEvent event);
}
