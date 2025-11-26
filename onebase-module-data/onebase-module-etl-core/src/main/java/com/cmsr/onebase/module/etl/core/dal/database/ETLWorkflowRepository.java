package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLWorkflowMapper;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.WorkflowBriefVO;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.etl.core.dal.dataobject.table.EtlScheduleJobTableDef.ETL_SCHEDULE_JOB;
import static com.cmsr.onebase.module.etl.core.dal.dataobject.table.EtlWorkflowTableDef.ETL_WORKFLOW;

@Slf4j
@Repository
public class ETLWorkflowRepository extends BaseAppRepository<ETLWorkflowMapper, ETLWorkflowDO> {

    public PageResult<WorkflowBriefVO> getWorkflowPage(WorkflowPageReqVO pageReqVO) {
        String scheduleStrategy = pageReqVO.getScheduleStrategy();
        boolean filterByScheduleStrategy = StringUtils.isNotBlank(scheduleStrategy) && !StringUtils.equals(ScheduleType.ALL.getValue(), scheduleStrategy);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ETL_WORKFLOW.ID,
                        ETL_WORKFLOW.APPLICATION_ID,
                        ETL_WORKFLOW.SCHEDULE_STRATEGY,
                        ETL_WORKFLOW.WORKFLOW_NAME.as("flow_name"),
                        ETL_WORKFLOW.IS_ENABLED.as("enable_status"),
                        ETL_SCHEDULE_JOB.JOB_STATUS.as("is_sync_done"),
                        ETL_SCHEDULE_JOB.LAST_JOB_TIME
                )
                .from(ETLWorkflowDO.class)

                .leftJoin(ETLScheduleJobDO.class).on(ETLWorkflowDO::getId, ETLScheduleJobDO::getWorkflowId)

                .where(ETLWorkflowDO::getApplicationId).eq(pageReqVO.getApplicationId())
                .like(ETLWorkflowDO::getWorkflowName, pageReqVO.getFlowName(), StringUtils.isNotBlank(pageReqVO.getFlowName()))
                .eq(ETLWorkflowDO::getScheduleStrategy, scheduleStrategy, filterByScheduleStrategy)
                .eq(ETLWorkflowDO::getIsEnabled, pageReqVO.getEnableStatus(), pageReqVO.getEnableStatus() != null)

                .orderBy(ETLWorkflowDO::getUpdateTime, false)
                .orderBy(ETLWorkflowDO::getCreateTime, false);
        Page<WorkflowBriefVO> pageResult = getMapper().paginateAs(pageReqVO.getPageNo(), pageReqVO.getPageSize(), queryWrapper, WorkflowBriefVO.class);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
