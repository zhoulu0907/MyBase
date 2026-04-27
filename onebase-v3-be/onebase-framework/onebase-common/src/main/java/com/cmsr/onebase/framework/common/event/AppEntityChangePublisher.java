package com.cmsr.onebase.framework.common.event;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AppEntityChangePublisher implements ApplicationContextAware {

    private static volatile AppEntityChangePublisher INSTANCE;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public static AppEntityChangePublisher getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("AppEntityChangePublisher not initialized yet");
        }
        return INSTANCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (AppEntityChangePublisher.class) {
            if (INSTANCE == null) {
                INSTANCE = this;
            }
        }
    }

    public static void publishEvent() {
        if (ApplicationManager.isIgnoreApplicationCondition()) {
            return;
        }
        getInstance().applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(ApplicationManager.getApplicationId())
                        .build()
        );
    }

    public static void publishEvent(Long applicationId) {
        getInstance().applicationEventPublisher.publishEvent(
                AppEntityChangeEvent.builder()
                        .applicationId(applicationId)
                        .build()
        );
    }

}
