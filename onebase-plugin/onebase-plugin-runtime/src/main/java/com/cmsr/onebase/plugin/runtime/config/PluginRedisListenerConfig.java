package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.core.constant.PluginRedisConstants;
import com.cmsr.onebase.plugin.runtime.listener.PluginCommandSubscriber;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis消息监听配置
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Configuration
public class PluginRedisListenerConfig {

    @Resource
    private PluginCommandSubscriber pluginCommandSubscriber;

    /**
     * 配置Redis消息监听容器
     *
     * @param connectionFactory Redis连接工厂
     * @return 消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer pluginRedisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 订阅插件命令频道
        container.addMessageListener(pluginCommandSubscriber,
                new ChannelTopic(PluginRedisConstants.PLUGIN_COMMAND_CHANNEL));

        return container;
    }

}
