package com.cmsr.onebase.module.flow.core.handler;

import com.cmsr.onebase.module.flow.core.config.FlowEnableCondition;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/11/1 18:27
 */
@Slf4j
@Setter
@Service
@Conditional(FlowEnableCondition.class)
public class FlowChangeHandler implements ApplicationRunner, MessageListener<FlowChangeEvent> {

    @Autowired
    private FlowProcessManager flowProcessManager;

    @Autowired
    private RedissonClient redissonClient;

    // 缓存已经处理过的版本，避免不停的加载和更新
    private Cache<Long, Long> versionCache = CacheBuilder.newBuilder()
            .expireAfterWrite(FlowUtils.VERSION_TIMEOUT_MINUTES * 2, TimeUnit.MINUTES).build();


    private volatile boolean inited = false;

    /**
     * 注意！！千万不要修改为 InitializingBean，一定要用ApplicationRunner，必须等Spring扫描完LiteFlow的组件后，才能加载组件！！
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            RMapCache<Long, FlowChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
            mapCache.forEach((k, v) -> {
                versionCache.put(k, v.getVersion());
            });
            flowProcessManager.initAllProcess();
            RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
            topic.addListener(FlowChangeEvent.class, this);
            inited = true;
        } catch (Exception e) {
            log.error("初始化异常：{}", e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(CharSequence channel, FlowChangeEvent msg) {
        log.info("更新自动化工作流版本：{}", msg);
        Long applicationId = msg.getApplicationId();
        Long version = msg.getVersion();
        try {
            if (FlowChangeEvent.UPDATE_EVENT.equals(msg.getEventType())) {
                handleUpdateEvent(applicationId, version);
            } else if (FlowChangeEvent.DELETE_EVENT.equals(msg.getEventType())) {
                handleDeleteEvent(applicationId, version);
            }
        } catch (Exception e) {
            log.error("更新版本异常：{}", msg, e);
        }
    }

    private void handleUpdateEvent(Long applicationId, Long rVersion) throws Exception {
        synchronized (this) {
            Long localVersion = versionCache.getIfPresent(applicationId);
            if (localVersion == null || localVersion < rVersion) {
                log.info("更新应用自动化工作流：{}", applicationId);
                flowProcessManager.onApplicationChange(applicationId, true);
                //
                versionCache.put(applicationId, rVersion);
            }
        }
    }

    private void handleDeleteEvent(Long applicationId, Long rVersion) throws Exception {
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


    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 1000)
    private void updateCacheTask() {
        if (!inited) {
            return;
        }
        RMapCache<Long, FlowChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
        mapCache.forEach((applicationId, flowChangeEvent) -> {
            try {
                if (FlowChangeEvent.UPDATE_EVENT.equals(flowChangeEvent.getEventType())) {
                    handleUpdateEvent(applicationId, flowChangeEvent.getVersion());
                } else if (FlowChangeEvent.DELETE_EVENT.equals(flowChangeEvent.getEventType())) {
                    handleDeleteEvent(applicationId, flowChangeEvent.getVersion());
                }
            } catch (Exception e) {
                log.error("更新版本异常：{}", e.getMessage(), e);
            }
        });
    }

    @Scheduled(initialDelay = 120 * 1000, fixedDelay = 300 * 1000)
    private void updateTimeJob() {
        if (!inited) {
            return;
        }
        flowProcessManager.checkTimeJob();
    }

}
