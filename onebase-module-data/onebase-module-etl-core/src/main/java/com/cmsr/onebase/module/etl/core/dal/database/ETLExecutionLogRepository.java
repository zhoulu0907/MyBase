package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlExecutionLogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlExecutionLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EtlExecutionLogRepository extends BaseAppRepository<EtlExecutionLogMapper, EtlExecutionLogDO> {


    public void deleteByWorkflow(Long workflowId) {
        QueryWrapper queryWrapper = query()
                .eq(EtlExecutionLogDO::getWorkflowId, workflowId);
        getMapper().deleteByQuery(queryWrapper);
    }

    public PageResult<EtlExecutionLogDO> queryPage(Long applicationId, Long workflowId, Integer pageNo, Integer pageSize) {
        QueryWrapper queryWrapper = query()
                .eq(EtlExecutionLogDO::getApplicationId, applicationId)
                .eq(EtlExecutionLogDO::getWorkflowId, workflowId, workflowId != null)
                .orderBy(EtlExecutionLogDO::getUpdateTime, false)
                .orderBy(EtlExecutionLogDO::getCreateTime, false);
        Page<EtlExecutionLogDO> pageResult = getMapper().paginate(pageNo, pageSize, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
