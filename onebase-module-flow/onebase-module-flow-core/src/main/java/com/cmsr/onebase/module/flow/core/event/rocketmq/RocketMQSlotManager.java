package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import lombok.Setter;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author：huangjie
 * @Date：2025/10/10 11:18
 */
@Component
@Conditional(FlowRuntimeCondition.class)
public class RocketMQSlotManager implements InitializingBean {

    private final String MAP_KEY = "flow:process:consumer:group";

    private final int MAX_INSTANCES = 100;

    private final long expirationThresholdMs = 120 * 1000;

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    private RMap<Integer, Long> slotMap;

    private AtomicInteger acquiredSlot = new AtomicInteger(-1);

    @Override
    public void afterPropertiesSet() {
        this.slotMap = redissonClient.getMap(MAP_KEY);
        acquireSlot();
        startHeartbeat();
    }

    public int getSlot() {
        return acquiredSlot.get();
    }

    private void acquireSlot() {
        for (int i = 1; i < MAX_INSTANCES; i++) {
            Long storedTimestamp = slotMap.get(i);
            if (storedTimestamp == null || (System.currentTimeMillis() - storedTimestamp) > expirationThresholdMs) {
                long currentTimestamp = System.currentTimeMillis();
                boolean acquired;
                if (storedTimestamp == null) {
                    acquired = slotMap.fastPutIfAbsent(i, currentTimestamp);
                } else {
                    acquired = slotMap.replace(i, storedTimestamp, currentTimestamp);
                }
                if (acquired) {
                    acquiredSlot.set(i);
                    return;
                }
            }
        }
        throw new RuntimeException("No available slots");
    }

    private void startHeartbeat() {
        Thread heartbeatThread = new Thread(() -> {
            while (true) {
                int slot = acquiredSlot.get();
                slotMap.put(slot, System.currentTimeMillis());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        });
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

}
