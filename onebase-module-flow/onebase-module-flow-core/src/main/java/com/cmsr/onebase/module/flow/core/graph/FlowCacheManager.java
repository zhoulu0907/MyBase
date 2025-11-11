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

    // 缓存已经处理过的版本，避免不停的加载和更新
    private Cache<Long, Long> versionCache = CacheBuilder.newBuilder()
            .expireAfterWrite(FlowUtils.VERSION_TIMEOUT_MINUTES * 2, TimeUnit.MINUTES).build();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RMapCache<Long, ChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
        mapCache.forEach((k, v) -> {
            versionCache.put(k, v.getVersion());
        });
        flowCacheHandler.initAllProcess();
        flowJobHandler.initAllProcess();
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        topic.addListener(ChangeEvent.class, this);
        taskScheduler.scheduleWithFixedDelay(this, Duration.of(60, ChronoUnit.SECONDS));
    }


    @Override
    public void onMessage(CharSequence channel, ChangeEvent msg) {
        log.info("更新自动化工作流版本：{}", msg);
        Long applicationId = msg.getApplicationId();
        Long version = msg.getVersion();
        try {
            if (ChangeEvent.UPDATE_EVENT.equals(msg.getEventType())) {
                onApplicationUpdate(applicationId, version);
            } else if (ChangeEvent.DELETE_EVENT.equals(msg.getEventType())) {
                onApplicationDelete(applicationId, version);
            }
        } catch (Exception e) {
            log.error("更新版本异常：{}", msg, e);
        }
    }

    @Override
    public void run() {
        RMapCache<Long, ChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
        mapCache.forEach((applicationId, changeEvent) -> {
            try {
                if (ChangeEvent.UPDATE_EVENT.equals(changeEvent.getEventType())) {
                    onApplicationUpdate(applicationId, changeEvent.getVersion());
                } else if (ChangeEvent.DELETE_EVENT.equals(changeEvent.getEventType())) {
                    onApplicationDelete(applicationId, changeEvent.getVersion());
                }
            } catch (Exception e) {
                log.error("更新版本异常：{}", e.getMessage(), e);
            }
        });
    }

    private void onApplicationUpdate(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            Long localVersion = versionCache.getIfPresent(applicationId);
            if (localVersion == null || localVersion < rVersion) {
                log.info("更新应用自动化工作流：{}", applicationId);
                flowCacheHandler.onApplicationChange(applicationId);
                flowJobHandler.onApplicationChange(applicationId);
                //
                versionCache.put(applicationId, rVersion);
            }
        }
    }

    private void onApplicationDelete(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            Long localVersion = versionCache.getIfPresent(applicationId);
            if (localVersion == null || localVersion < rVersion) {
                log.info("删除应用自动化工作流：{}", applicationId);
                flowCacheHandler.onApplicationDelete(applicationId);
                flowJobHandler.onApplicationDelete(applicationId);
                //
                versionCache.put(applicationId, rVersion);
            }
        }
    }

}
