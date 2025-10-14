package com.cmsr.onebase.module.flow.core.event;

import lombok.Setter;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author：huangjie
 * @Date：2025/10/10 11:18
 */
public class RocketMQSlotManager implements InitializingBean {

    private final int MAX_INSTANCES = 100;

    private final long EXPIRATION_THRESHOLD_MS = 90 * 1000;

    @Setter
    private RedissonClient redissonClient;

    @Setter
    private String slotKey;

    private RMap<Integer, Long> slotMap;

    private AtomicInteger acquiredSlot = new AtomicInteger(-1);

    @Override
    public void afterPropertiesSet() {
        this.slotMap = redissonClient.getMap(slotKey);
        acquireSlot();
        startHeartbeat();
    }

    public int getSlot() {
        return acquiredSlot.get();
    }

    private void acquireSlot() {
        for (int i = 1; i < MAX_INSTANCES; i++) {
            Long storedTimestamp = slotMap.get(i);
            if (storedTimestamp == null || (System.currentTimeMillis() - storedTimestamp) > EXPIRATION_THRESHOLD_MS) {
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
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

}
