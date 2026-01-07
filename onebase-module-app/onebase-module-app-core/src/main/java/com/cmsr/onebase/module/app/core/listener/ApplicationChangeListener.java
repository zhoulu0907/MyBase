package com.cmsr.onebase.module.app.core.listener;

import com.cmsr.onebase.framework.common.event.AppEntityChangeEvent;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationChangeListener {

    @Autowired
    private AppApplicationRepository applicationRepository;

    @Async
    @EventListener
    public void onApplicationEvent(AppEntityChangeEvent event) {
        // for this method ignore conditions
        try {
            TenantManager.withoutTenantCondition(() -> {
                Long applicationId = event.getApplicationId();
                applicationRepository.updateAppTimeByApplicationId(applicationId);
            });
        } catch (Exception e) {
            log.warn("update app time error: {}", e.getMessage());
        }
    }
}
