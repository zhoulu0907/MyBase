package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLSchecheduJobMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLScheduleJobRepository extends ServiceImpl<ETLSchecheduJobMapper, ETLScheduleJobDO> {

    private DataRepository<ETLScheduleJobDO> dataRepository;

    @Autowired
    private AnylineService<ETLScheduleJobDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository = new DataRepository<>(ETLScheduleJobDO.class);
        dataRepository.setAnylineService(anylineService);
    }

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
