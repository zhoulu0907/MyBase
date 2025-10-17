package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.enums.RocketMQConstants;
import com.cmsr.onebase.module.flow.core.event.RocketMQSlotManager;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;

/**
 * @Author：huangjie
 * @Date：2025/10/11 10:35
 */
public class RocketMQSlotManagerTest {


    @org.junit.jupiter.api.Test
    void acquireSlot() throws Exception {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.0.104.38:6379")
                .setConnectionPoolSize(1)
                .setConnectionMinimumIdleSize(1);
        RedissonConnectionFactory redisConnectionFactory = new RedissonConnectionFactory(config);
        redisConnectionFactory.afterPropertiesSet();
        RedissonClient redissonClient = Redisson.create(config);

        RocketMQSlotManager slotManager = new RocketMQSlotManager();
        slotManager.setSlotKey(RocketMQConstants.CHANGE_EVENTS_CONSUMER_GROUP_SLOT);
        slotManager.setRedissonClient(redissonClient);
        long slot = slotManager.getSlot();
        System.out.println(slot);
    }


}