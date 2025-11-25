package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.mybatis.BaseTenantRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLExecutionLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLExecutionLogRepository extends BaseTenantRepository<ETLExecutionLogMapper, ETLExecutionLogDO> {


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
