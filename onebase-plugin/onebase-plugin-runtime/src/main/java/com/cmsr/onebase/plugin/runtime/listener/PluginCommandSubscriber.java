package com.cmsr.onebase.plugin.runtime.listener;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.plugin.core.constant.PluginRedisConstants;
import com.cmsr.onebase.plugin.core.message.PluginCommandMessage;
import com.cmsr.onebase.plugin.runtime.loader.PluginFileManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 插件命令Redis订阅器
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Component
@Slf4j
public class PluginCommandSubscriber implements MessageListener {

    @Resource
    private PluginFileManager pluginFileManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.info("收到插件命令消息: channel={}, body={}", channel, body);

        if (!PluginRedisConstants.PLUGIN_COMMAND_CHANNEL.equals(channel)) {
            return;
        }

        try {
            PluginCommandMessage commandMessage = JsonUtils.parseObject(body, PluginCommandMessage.class);
            if (commandMessage == null) {
                log.warn("解析插件命令消息失败: {}", body);
                return;
            }

            handleCommand(commandMessage);
        } catch (Exception e) {
            log.error("处理插件命令消息异常: {}", body, e);
        }
    }

    /**
     * 处理插件命令
     *
     * @param message 命令消息
     */
    private void handleCommand(PluginCommandMessage message) {
        switch (message.getCommand()) {
            case UPLOAD -> handleUploadCommand(message);
            case ENABLE -> handleEnableCommand(message);
            case DISABLE -> handleDisableCommand(message);
            case RELOAD -> handleReloadCommand(message);
            default -> log.warn("未知的插件命令: {}", message.getCommand());
        }
    }

    /**
     * 处理上传命令
     * 当调用上传接口时，接收消息并下载解压插件到指定目录
     */
    private void handleUploadCommand(PluginCommandMessage message) {
        log.info("处理插件上传命令: pluginId={}, version={}, tenantId={}",
                message.getPluginId(), message.getPluginVersion(), message.getTenantId());

        try {
            // 下载并解压插件（支持前后端分离存储）
            pluginFileManager.downloadAndExtractPlugin(
                    message.getPluginId(),
                    message.getPluginVersion(),
                    message.getPackageFileId(),
                    message.getPackages()
            );

            log.info("插件上传处理成功: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion());
        } catch (Exception e) {
            log.error("插件上传处理失败: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion(), e);
        }
    }

    /**
     * 处理启用命令
     */
    private void handleEnableCommand(PluginCommandMessage message) {
        log.info("处理插件启用命令: pluginId={}, version={}, tenantId={}",
                message.getPluginId(), message.getPluginVersion(), message.getTenantId());

        try {
            // 1. 下载并解压插件
            pluginFileManager.downloadAndExtractPlugin(
                    message.getPluginId(),
                    message.getPluginVersion(),
                    message.getPackageFileId()
            );

            // 2. 加载插件（调用PluginManager）
            pluginFileManager.loadPlugin(message.getPluginId(), message.getPluginVersion());

            log.info("插件启用成功: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion());
        } catch (Exception e) {
            log.error("插件启用失败: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion(), e);
        }
    }

    /**
     * 处理禁用命令
     */
    private void handleDisableCommand(PluginCommandMessage message) {
        log.info("处理插件禁用命令: pluginId={}, version={}, tenantId={}",
                message.getPluginId(), message.getPluginVersion(), message.getTenantId());

        try {
            // 1. 卸载插件
            pluginFileManager.unloadPlugin(message.getPluginId(), message.getPluginVersion());

            // 2. 可选：清理本地文件
            // pluginFileManager.cleanupPluginFiles(message.getPluginId(), message.getPluginVersion());

            log.info("插件禁用成功: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion());
        } catch (Exception e) {
            log.error("插件禁用失败: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion(), e);
        }
    }

    /**
     * 处理重载命令
     */
    private void handleReloadCommand(PluginCommandMessage message) {
        log.info("处理插件重载命令: pluginId={}, version={}, tenantId={}",
                message.getPluginId(), message.getPluginVersion(), message.getTenantId());

        try {
            // 1. 先卸载
            pluginFileManager.unloadPlugin(message.getPluginId(), message.getPluginVersion());

            // 2. 重新下载并解压
            pluginFileManager.downloadAndExtractPlugin(
                    message.getPluginId(),
                    message.getPluginVersion(),
                    message.getPackageFileId()
            );

            // 3. 重新加载
            pluginFileManager.loadPlugin(message.getPluginId(), message.getPluginVersion());

            log.info("插件重载成功: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion());
        } catch (Exception e) {
            log.error("插件重载失败: pluginId={}, version={}", message.getPluginId(), message.getPluginVersion(), e);
        }
    }

}
