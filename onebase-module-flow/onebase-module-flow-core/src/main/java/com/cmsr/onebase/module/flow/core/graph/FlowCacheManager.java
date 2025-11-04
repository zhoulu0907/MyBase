package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.handler.FlowCacheHandler;
import com.cmsr.onebase.module.flow.core.handler.FlowJobHandler;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/11/1 18:27
 */
@Slf4j
@Service
@Conditional(FlowRuntimeCondition.class)
public class FlowCacheManager implements ApplicationRunner, Runnable, MessageListener<ChangeEvent> {

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

    // 缓存已经处理过的版本，避免不同的加载和更新
    private Cache<Long, Long> versionCache = CacheBuilder.newBuilder()
            .expireAfterWrite(FlowUtils.VERSION_TIMEOUT_HOUR, TimeUnit.HOURS).build();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RMapCache<Long, Long> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CACHE_KEY);
        mapCache.forEach((k, v) -> {
            versionCache.put(k, v);
        });
        flowCacheHandler.initAllProcess();
        flowJobHandler.initAllProcess();
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        topic.addListener(ChangeEvent.class, this);
        taskScheduler.scheduleAtFixedRate(this, Duration.of(60, ChronoUnit.SECONDS));
    }


    @Override
    public void onMessage(CharSequence channel, ChangeEvent msg) {
        log.info("更新自动化工作流版本：{}", msg);
        Long applicationId = msg.getApplicationId();
        Long version = msg.getVersion();
        try {
            synchronized (this) {
                switch (msg.getEventType()) {
                    case ChangeEvent.UPDATE_EVENT:
                        onApplicationUpdate(applicationId, version);
                        break;
                    case ChangeEvent.DELETE_EVENT:
                        onApplicationDelete(applicationId);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("更新版本异常：{}", msg, e);
        }
    }

    @Override
    public void run() {
        RMapCache<Long, Long> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CACHE_KEY);
        mapCache.forEach((applicationId, rVersion) -> {
            try {
                // 版本大于0，则更新
                if (rVersion != null && rVersion >= 0) {
                    checkApplicationForUpdate(applicationId, rVersion);
                }
                // 版本小于0的，则删除
                if (rVersion != null && rVersion < 0) {
                    checkApplicationForDelete(applicationId, rVersion);
                }
            } catch (Exception e) {
                log.error("更新版本异常：{}", e.getMessage(), e);
            }
        });
        // 检查自己的缓存，可能有需要删除的，清理缓存，避免内存溢出
        flowProcessCache.getAllApplicationId().forEach(applicationId -> {
            try {
                Long rVersion = mapCache.get(applicationId);
                checkApplicationForDelete(applicationId, rVersion);
            } catch (Exception e) {
                log.error("删除应用异常：{}", e.getMessage(), e);
            }
        });
    }


    private void checkApplicationForUpdate(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            Long localVersion = versionCache.getIfPresent(applicationId);
            // 版本不一致
            if (localVersion == null || !localVersion.equals(rVersion)) {
                onApplicationUpdate(applicationId, rVersion);
            }
        }
    }

    private void onApplicationUpdate(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            log.info("更新应用自动化工作流：{}", applicationId);
            flowCacheHandler.onApplicationChange(applicationId);
            flowJobHandler.onApplicationChange(applicationId);
            versionCache.put(applicationId, rVersion);
        }
    }

    private void checkApplicationForDelete(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            if (rVersion != null && rVersion.equals(-1L)) {
                onApplicationDelete(applicationId);
            }
        }
    }

    private void onApplicationDelete(Long applicationId) throws Exception {
        synchronized (this) {
            log.info("删除应用自动化工作流：{}", applicationId);
            flowCacheHandler.onApplicationDelete(applicationId);
            flowJobHandler.onApplicationDelete(applicationId);
            versionCache.invalidate(applicationId);
        }
    }

}
