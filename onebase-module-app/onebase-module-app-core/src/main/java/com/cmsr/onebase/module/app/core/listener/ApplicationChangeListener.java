package com.cmsr.onebase.module.app.core.listener;

import com.cmsr.onebase.framework.common.event.AppEntityChangeEvent;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.database.version.AppVersionRepository;
import com.mybatisflex.core.tenant.TenantManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationChangeListener {

    @Autowired
    private AppApplicationRepository applicationRepository;

    @Autowired
    private AppVersionRepository versionRepository;

    @Async
    @EventListener
    public void onApplicationEvent(AppEntityChangeEvent event) {
        // for this method ignore conditions
        TenantManager.withoutTenantCondition(() -> {
            ApplicationManager.withoutApplicationIdAndVersionTag(() -> {
                Long applicationId = event.getApplicationId();
                applicationRepository.updateAppTimeByApplicationId(applicationId);
                return true;
            });
        });
    }
}
