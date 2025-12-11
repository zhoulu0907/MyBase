package com.cmsr.job.schedule;

import com.fit2cloud.quartz.anno.QuartzScheduled;
import com.cmsr.datasource.server.DatasourceServer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class Schedular {

    @Resource
    private DatasourceServer datasourceServer;

    @QuartzScheduled(cron = "0 0/3 * * * ?")
    public void updateStopJobStatus() {
        datasourceServer.updateStopJobStatus();
    }

}
