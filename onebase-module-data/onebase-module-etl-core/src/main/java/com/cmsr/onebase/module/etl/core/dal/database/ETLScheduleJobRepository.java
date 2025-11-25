package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.mybatis.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLSchecheduJobMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLScheduleJobRepository extends BaseAppRepository<ETLSchecheduJobMapper, ETLScheduleJobDO> {


    public void deleteByWorkflowId(Long workflowId) {
        QueryWrapper queryWrapper = query().eq(ETLScheduleJobDO::getWorkflowId, workflowId);
        remove(queryWrapper);
    }

    public ETLScheduleJobDO findByApplicationIdAndWorkflowId(Long applicationId, Long workflowId) {
        QueryWrapper queryWrapper = query()
                .eq(ETLScheduleJobDO::getApplicationId, applicationId)
                .eq(ETLScheduleJobDO::getWorkflowId, workflowId);
        return getOne(queryWrapper);
    }

    public void removeJobId(Long workflowId) {
        updateChain()
                .set(ETLScheduleJobDO::getJobId, null)
                .where(ETLScheduleJobDO::getWorkflowId).eq(workflowId)
                .update();
    }
}
