package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLExecutionLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLExecutionLogRepository extends ServiceImpl<ETLExecutionLogMapper, ETLExecutionLogDO> {

    private DataRepository<ETLExecutionLogDO> dataRepository;

    @Autowired
    private AnylineService<ETLExecutionLogDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository = new DataRepository<>(ETLExecutionLogDO.class);
        dataRepository.setAnylineService(anylineService);
    }


    public void deleteByWorkflowId(Long workflowId) {
        QueryWrapper queryWrapper = query()
                .eq(ETLExecutionLogDO::getWorkflowId, workflowId);
        getMapper().deleteByQuery(queryWrapper);
    }

    public PageResult<ETLExecutionLogDO> queryPage(Long applicationId, Long workflowId, Integer pageNo, Integer pageSize) {
        QueryWrapper queryWrapper = query()
                .eq(ETLExecutionLogDO::getApplicationId, applicationId)
                .eq(ETLExecutionLogDO::getWorkflowId, workflowId, workflowId != null)
                .orderBy(ETLExecutionLogDO::getUpdateTime, false)
                .orderBy(ETLExecutionLogDO::getCreateTime, false);
        Page<ETLExecutionLogDO> pageResult = getMapper().paginate(pageNo, pageSize, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
