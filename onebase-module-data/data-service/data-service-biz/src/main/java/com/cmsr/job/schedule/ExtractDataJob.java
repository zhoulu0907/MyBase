package com.cmsr.job.schedule;


import com.cmsr.datasource.manage.DatasourceSyncManage;
import com.cmsr.utils.CommonBeanFactory;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class ExtractDataJob extends DeScheduleJob {
    private DatasourceSyncManage datasourceSyncManage;

    public ExtractDataJob() {
        datasourceSyncManage = (DatasourceSyncManage) CommonBeanFactory.getBean(DatasourceSyncManage.class);
    }

    @Override
    void businessExecute(JobExecutionContext context) {
        datasourceSyncManage.extractData(datasetTableId, taskId, context);
    }

}
