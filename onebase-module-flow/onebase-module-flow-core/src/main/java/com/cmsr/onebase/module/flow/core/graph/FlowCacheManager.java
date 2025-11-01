package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.handler.FlowCacheHandler;
import com.cmsr.onebase.module.flow.core.handler.FlowJobHandler;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 * @Author：huangjie
 * @Date：2025/11/1 18:27
 */
@Slf4j
@Service
@Conditional(FlowRuntimeCondition.class)
public class FlowCacheManager implements ApplicationRunner, Runnable {

    @Setter
    @Autowired
    private FlowProcessCache flowProcessCache;

    @Setter
    @Autowired
    private FlowCacheHandler flowCacheHandler;

    @Setter
    @Autowired
    private FlowJobHandler flowJobHandler;

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Setter
    @Autowired
    private TaskScheduler taskScheduler;

    private HashMap<Long, Long> applicationVersionCache = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RMap<Long, Long> rMap = redissonClient.getMap(FlowUtils.REDIS_APPLICATION_VERSION_KEY);
        rMap.forEach((k, v) -> {
            applicationVersionCache.put(k, v);
        });
        flowCacheHandler.initAllProcess();
        initRedisVersionKey();
        taskScheduler.scheduleAtFixedRate(this, Duration.of(5, ChronoUnit.SECONDS));
    }


    private void initRedisVersionKey() {
        RMap<Long, Long> rMap = redissonClient.getMap(FlowUtils.REDIS_APPLICATION_VERSION_KEY);
        flowProcessCache.getAllApplicationId().forEach(applicationId -> {
            Long version = rMap.get(applicationId);
            if (version == null) {
                rMap.putIfAbsent(applicationId, 0L);
            }
        });
    }


    @Override
    public void run() {
        RMap<Long, Long> rMap = redissonClient.getMap(FlowUtils.REDIS_APPLICATION_VERSION_KEY);
        rMap.forEach((applicationId, version) -> {
            try {
                checkApplicationForUpdate(applicationId, version);
            } catch (Exception e) {
                log.error("更新版本异常：{}", e.getMessage(), e);
            }
        });
        flowProcessCache.getAllApplicationId().forEach(applicationId -> {
            try {
                checkApplicationForDelete(applicationId);
            } catch (Exception e) {
                log.error("删除应用异常：{}", e.getMessage(), e);
            }
        });
    }


    private void checkApplicationForUpdate(Long applicationId, Long version) throws Exception {
        Long cachedVersion = applicationVersionCache.get(applicationId);
        // 版本不一致
        if (cachedVersion == null || !cachedVersion.equals(version)) {
            flowCacheHandler.onApplicationChange(applicationId);
            applicationVersionCache.put(applicationId, version);
            flowJobHandler.onApplicationChange(applicationId);
        }
    }

    private void checkApplicationForDelete(Long applicationId) throws Exception {
        RMap<Long, Long> rMap = redissonClient.getMap(FlowUtils.REDIS_APPLICATION_VERSION_KEY);
        if (!rMap.containsKey(applicationId)) {
            flowCacheHandler.onApplicationDelete(applicationId);
            applicationVersionCache.remove(applicationId);
            flowJobHandler.onApplicationDelete(applicationId);
        }
    }
}
