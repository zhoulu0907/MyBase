package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLExecutionLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLExecutionLogRepository extends BaseAppRepository<ETLExecutionLogMapper, ETLExecutionLogDO> {


    public void deleteByWorkflow(String workflowUuid) {
        QueryWrapper queryWrapper = query()
                .eq(ETLExecutionLogDO::getWorkflowUuid, workflowUuid);
        getMapper().deleteByQuery(queryWrapper);
    }

    public PageResult<ETLExecutionLogDO> queryPage(Long applicationId, String workflowUuid, Integer pageNo, Integer pageSize) {
        QueryWrapper queryWrapper = query()
                .eq(ETLExecutionLogDO::getApplicationId, applicationId)
                .eq(ETLExecutionLogDO::getWorkflowUuid, workflowUuid, StringUtils::isNotBlank)
                .orderBy(ETLExecutionLogDO::getUpdateTime, false)
                .orderBy(ETLExecutionLogDO::getCreateTime, false);
        Page<ETLExecutionLogDO> pageResult = getMapper().paginate(pageNo, pageSize, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
