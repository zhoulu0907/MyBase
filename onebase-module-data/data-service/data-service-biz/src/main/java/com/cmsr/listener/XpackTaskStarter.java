package com.cmsr.listener;

import com.cmsr.job.schedule.DeDataFillingTaskExecutor;
import com.cmsr.job.schedule.DeTaskExecutor;
//import com.cmsr.license.utils.LicenseUtil;
import com.cmsr.utils.LogUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 4)
public class XpackTaskStarter implements ApplicationRunner {

    @Resource
    private DeTaskExecutor deTaskExecutor;

    @Resource
    private DeDataFillingTaskExecutor deDataFillingTaskExecutor;

    @Override
    public void run(ApplicationArguments args) {
        try {
            //LicenseUtil.validate();
            deTaskExecutor.init();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e.getCause());
        }
        try {
            //LicenseUtil.validate();
            deDataFillingTaskExecutor.init();
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e.getCause());
        }
    }
}
