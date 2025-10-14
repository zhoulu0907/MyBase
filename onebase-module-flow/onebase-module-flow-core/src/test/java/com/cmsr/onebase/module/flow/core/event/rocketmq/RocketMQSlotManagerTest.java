package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.event.RocketMQConstants;
import com.cmsr.onebase.module.flow.core.event.RocketMQSlotManager;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author：huangjie
 * @Date：2025/10/11 10:35
 */
public class RocketMQSlotManagerTest {

    @org.junit.jupiter.api.Test
    void queryMap() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.0.104.38:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RMap<Object, Object> map = redissonClient.getMap(RocketMQConstants.EVENT_TOPIC_SLOT);
        for (Object key : map.keySet()) {
            Long value = (Long) map.get(key);
            //System.out.println(key + ":" + value);
            Instant instant = Instant.ofEpochMilli(value);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            System.out.println(key + ":" + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    @org.junit.jupiter.api.Test
    void acquireSlot() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.0.104.38:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RocketMQSlotManager slotManager = new RocketMQSlotManager();
        slotManager.setRedissonClient(redissonClient);
        slotManager.afterPropertiesSet();
        int slot = slotManager.getSlot();
        System.out.println(slot);
    }


}