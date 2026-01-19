package com.cmsr.onebase.plugin.build.redis;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.plugin.core.constant.PluginRedisConstants;
import com.cmsr.onebase.plugin.core.message.PluginCommandMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 插件命令Redis发布器
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Component
@Slf4j
public class PluginCommandPublisher {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发布插件启用命令
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param tenantId 租户ID
     * @param packageFileId 包文件ID
     */
    public void publishEnableCommand(String pluginId, String pluginVersion, Long tenantId, Long packageFileId) {
        PluginCommandMessage message = PluginCommandMessage.builder()
                .command(PluginCommandMessage.PluginCommand.ENABLE)
                .pluginId(pluginId)
                .pluginVersion(pluginVersion)
                .tenantId(tenantId)
                .packageFileId(packageFileId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message);
    }

    /**
     * 发布插件禁用命令
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param tenantId 租户ID
     */
    public void publishDisableCommand(String pluginId, String pluginVersion, Long tenantId) {
        PluginCommandMessage message = PluginCommandMessage.builder()
                .command(PluginCommandMessage.PluginCommand.DISABLE)
                .pluginId(pluginId)
                .pluginVersion(pluginVersion)
                .tenantId(tenantId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message);
    }

    /**
     * 发布插件重载命令
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param tenantId 租户ID
     * @param packageFileId 包文件ID
     */
    public void publishReloadCommand(String pluginId, String pluginVersion, Long tenantId, Long packageFileId) {
        PluginCommandMessage message = PluginCommandMessage.builder()
                .command(PluginCommandMessage.PluginCommand.RELOAD)
                .pluginId(pluginId)
                .pluginVersion(pluginVersion)
                .tenantId(tenantId)
                .packageFileId(packageFileId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message);
    }

    /**
     * 发布插件删除命令
     *
     * @param pluginId 插件ID
     * @param pluginVersion 插件版本
     * @param tenantId 租户ID
     */
    public void publishDeleteCommand(String pluginId, String pluginVersion, Long tenantId) {
        PluginCommandMessage message = PluginCommandMessage.builder()
                .command(PluginCommandMessage.PluginCommand.DELETE)
                .pluginId(pluginId)
                .pluginVersion(pluginVersion)
                .tenantId(tenantId)
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message);
    }

    /**
     * 发布消息到Redis Channel
     *
     * @param message 消息内容
     */
    private void publish(PluginCommandMessage message) {
        String jsonMessage = JsonUtils.toJsonString(message);
        log.info("发布插件命令消息: channel={}, message={}", PluginRedisConstants.PLUGIN_COMMAND_CHANNEL, jsonMessage);
        stringRedisTemplate.convertAndSend(PluginRedisConstants.PLUGIN_COMMAND_CHANNEL, jsonMessage);
    }

}
