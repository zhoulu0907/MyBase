package com.cmsr.job.schedule;


import com.cmsr.datasource.server.DatasourceServer;
import com.cmsr.utils.CommonBeanFactory;
import com.cmsr.utils.LogUtil;
import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;


@Component
public class CheckDsStatusJob implements Job {
    @Resource
    private DatasourceServer datasourceServer;

    public CheckDsStatusJob() {
        datasourceServer = (DatasourceServer) CommonBeanFactory.getBean(DatasourceServer.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LogUtil.info("Begin to check ds status...");
        datasourceServer.updateDatasourceStatus();
    }

}
