package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlSchecheduJobMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.etl.core.dal.dataobject.table.EtlScheduleJobTableDef.ETL_SCHEDULE_JOB;

@Slf4j
@Repository
public class EtlScheduleJobRepository extends BaseAppRepository<EtlSchecheduJobMapper, EtlScheduleJobDO> {


    public void deleteByWorkflow(String workflowUuid) {
        this.updateChain()
                .where(ETL_SCHEDULE_JOB.WORKFLOW_UUID.eq(workflowUuid))
                .remove();
    }

    public EtlScheduleJobDO findByApplicationAndWorkflow(Long applicationId, String workflowUuid) {
        QueryWrapper queryWrapper = query()
                .where(ETL_SCHEDULE_JOB.APPLICATION_ID.eq(applicationId))
                .where(ETL_SCHEDULE_JOB.WORKFLOW_UUID.eq(workflowUuid));
        return getOne(queryWrapper);
    }

    public void removeJobId(String workflowUuid) {
        this.updateChain()
                .set(ETL_SCHEDULE_JOB.JOB_ID, null)
                .where(ETL_SCHEDULE_JOB.WORKFLOW_UUID.eq(workflowUuid))
                .update();
    }

    public List<EtlScheduleJobDO> findAllOnlineJobByApplication(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .where(ETL_SCHEDULE_JOB.APPLICATION_ID.eq(applicationId))
                .where(ETL_SCHEDULE_JOB.JOB_ID.isNotNull());
        return this.getMapper().selectListByQuery(queryWrapper);
    }
}
