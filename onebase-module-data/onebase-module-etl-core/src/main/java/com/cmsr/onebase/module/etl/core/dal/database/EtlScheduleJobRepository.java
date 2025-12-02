package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlSchecheduJobMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EtlScheduleJobRepository extends BaseAppRepository<EtlSchecheduJobMapper, EtlScheduleJobDO> {


    public void deleteByWorkflow(String workflowUuid) {
        this.updateChain()
                .eq(EtlScheduleJobDO::getWorkflowUuid, workflowUuid)
                .remove();
    }

    public EtlScheduleJobDO findByApplicationAndWorkflow(Long applicationId, String workflowUuid) {
        QueryWrapper queryWrapper = query()
                .eq(EtlScheduleJobDO::getApplicationId, applicationId)
                .eq(EtlScheduleJobDO::getWorkflowUuid, workflowUuid);
        return getOne(queryWrapper);
    }

    public void removeJobId(String workflowUuid) {
        updateChain()
                .set(EtlScheduleJobDO::getJobId, null)
                .where(EtlScheduleJobDO::getWorkflowUuid).eq(workflowUuid)
                .update();
    }
}
