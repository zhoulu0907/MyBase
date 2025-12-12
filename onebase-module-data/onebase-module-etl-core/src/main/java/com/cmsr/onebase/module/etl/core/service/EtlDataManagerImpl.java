package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.module.etl.api.EtlDataManager;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlScheduleJobDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class EtlDataManagerImpl implements EtlDataManager {
    @Autowired
    private EtlExecutionLogRepository executionLogRepository;

    @Autowired
    private EtlWorkflowTableRepository workflowTableRepository;

    @Autowired
    private EtlTableRepository tableRepository;

    @Autowired
    private EtlSchemaRepository schemaRepository;

    @Autowired
    private EtlCatalogRepository catalogRepository;

    @Autowired
    private EtlDatasourceRepository datasourceRepository;

    @Autowired
    private EtlScheduleJobRepository scheduleJobRepository;

    @Autowired
    private EtlWorkflowRepository workflowRepository;

    @Value("${onebase.scheduler.etl-project}")
    private Long etlProjectCode;

    @Autowired
    private DolphinSchedulerClient dolphinSchedulerClient;

    @Override
    public void offlineAllByApplication(Long applicationId) {
        List<EtlScheduleJobDO> scheduleJobDOList = scheduleJobRepository.findAllOnlineJobByApplication(applicationId);
        if (CollectionUtils.isEmpty(scheduleJobDOList)) {
            return;
        }
        for (EtlScheduleJobDO scheduleJobDO : scheduleJobDOList) {
            String workflowUuid = scheduleJobDO.getWorkflowUuid();
            String jobIdStr = scheduleJobDO.getJobId();
            Long jobId = NumberUtils.toLong(jobIdStr);
            dolphinSchedulerClient.purgeWorkflow(etlProjectCode, jobId);
            scheduleJobRepository.removeJobId(workflowUuid);
        }
    }

    @Override
    public void deleteAllApplicationData(Long applicationId) {
        List<EtlScheduleJobDO> scheduleJobDOList = scheduleJobRepository.findAllOnlineJobByApplication(applicationId);
        if (CollectionUtils.isNotEmpty(scheduleJobDOList)) {
            List<String> workflowUuids = scheduleJobDOList.stream().map(EtlScheduleJobDO::getWorkflowUuid).toList();
            throw new IllegalStateException("存在任务未下线, " + StringUtils.join(workflowUuids, ","));
        }
        executionLogRepository.deleteAllApplicationData(applicationId);
        workflowTableRepository.deleteAllApplicationData(applicationId);
        tableRepository.deleteAllApplicationData(applicationId);
        schemaRepository.deleteAllApplicationData(applicationId);
        catalogRepository.deleteAllApplicationData(applicationId);
        datasourceRepository.deleteAllApplicationData(applicationId);
        scheduleJobRepository.deleteAllApplicationData(applicationId);
        workflowRepository.deleteAllApplicationData(applicationId);
    }
}
