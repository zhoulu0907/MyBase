package com.cmsr.onebase.module.flow.core.handler;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessManager;
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
@Setter
@Service
@Conditional(FlowRuntimeCondition.class)
public class FlowChangeHandler implements ApplicationRunner, MessageListener<FlowChangeEvent> {

    @Autowired
    private FlowProcessManager flowProcessManager;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TaskScheduler taskScheduler;

    // 缓存已经处理过的版本，避免不停的加载和更新
    private Cache<Long, Long> versionCache = CacheBuilder.newBuilder()
            .expireAfterWrite(FlowUtils.VERSION_TIMEOUT_MINUTES * 2, TimeUnit.MINUTES).build();


    private class UpdateCacheTask implements Runnable {
        @Override
        public void run() {
            RMapCache<Long, FlowChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
            mapCache.forEach((applicationId, flowChangeEvent) -> {
                try {
                    if (FlowChangeEvent.UPDATE_EVENT.equals(flowChangeEvent.getEventType())) {
                        onApplicationUpdate(applicationId, flowChangeEvent.getVersion());
                    } else if (FlowChangeEvent.DELETE_EVENT.equals(flowChangeEvent.getEventType())) {
                        onApplicationDelete(applicationId, flowChangeEvent.getVersion());
                    }
                } catch (Exception e) {
                    log.error("更新版本异常：{}", e.getMessage(), e);
                }
            });
        }
    }

    private class UpdateTimeJob implements Runnable {
        @Override
        public void run() {
            flowProcessManager.checkTimeJob();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RMapCache<Long, FlowChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
        try {
            mapCache.forEach((k, v) -> {
                versionCache.put(k, v.getVersion());
            });
        } catch (Exception ignored) {
        }
        flowProcessManager.initAllProcess();
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        topic.addListener(FlowChangeEvent.class, this);
        //60秒把缓存中的数据更新做处理
        taskScheduler.scheduleWithFixedDelay(new UpdateCacheTask(), Duration.of(60, ChronoUnit.SECONDS));
        //300秒更新一次时间任务，避免任务上线失败
        taskScheduler.scheduleWithFixedDelay(new UpdateTimeJob(), Duration.of(300, ChronoUnit.SECONDS));
    }

    @Override
    public void onMessage(CharSequence channel, FlowChangeEvent msg) {
        log.info("更新自动化工作流版本：{}", msg);
        Long applicationId = msg.getApplicationId();
        Long version = msg.getVersion();
        try {
            if (FlowChangeEvent.UPDATE_EVENT.equals(msg.getEventType())) {
                onApplicationUpdate(applicationId, version);
            } else if (FlowChangeEvent.DELETE_EVENT.equals(msg.getEventType())) {
                onApplicationDelete(applicationId, version);
            }
        } catch (Exception e) {
            log.error("更新版本异常：{}", msg, e);
        }
    }

    private void onApplicationUpdate(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            Long localVersion = versionCache.getIfPresent(applicationId);
            if (localVersion == null || localVersion < rVersion) {
                log.info("更新应用自动化工作流：{}", applicationId);
                flowProcessManager.onApplicationChange(applicationId);
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
                flowProcessManager.onApplicationDelete(applicationId);
                //
                versionCache.put(applicationId, rVersion);
            }
        }
    }


}
