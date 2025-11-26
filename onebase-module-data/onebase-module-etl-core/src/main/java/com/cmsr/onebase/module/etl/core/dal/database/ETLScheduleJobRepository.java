package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLSchecheduJobMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLScheduleJobRepository extends BaseAppRepository<ETLSchecheduJobMapper, ETLScheduleJobDO> {


    public void deleteByWorkflow(String workflowUuid) {
        this.updateChain()
                .eq(ETLScheduleJobDO::getWorkflowUuid, workflowUuid)
                .remove();
    }

    public ETLScheduleJobDO findByApplicationAndWorkflow(Long applicationId, String workflowUuid) {
        QueryWrapper queryWrapper = query()
                .eq(ETLScheduleJobDO::getApplicationId, applicationId)
                .eq(ETLScheduleJobDO::getWorkflowUuid, workflowUuid);
        return getOne(queryWrapper);
    }

    public void removeJobId(String workflowUuid) {
        updateChain()
                .set(ETLScheduleJobDO::getJobId, null)
                .where(ETLScheduleJobDO::getWorkflowUuid).eq(workflowUuid)
                .update();
    }
}
