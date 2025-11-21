package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLWorkflowMapper;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLWorkflowRepository extends ServiceImpl<ETLWorkflowMapper, ETLWorkflowDO> {

    private DataRepository<ETLWorkflowDO> dataRepository;

    @Autowired
    private AnylineService<ETLWorkflowDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository = new DataRepository<>(ETLWorkflowDO.class);
        dataRepository.setAnylineService(anylineService);
    }

    public PageResult<ETLWorkflowDO> getWorkflowPage(WorkflowPageReqVO pageReqVO) {
        String scheduleStrategy = pageReqVO.getScheduleStrategy();
        boolean filterByScheduleStrategy = StringUtils.isNotBlank(scheduleStrategy) && !StringUtils.equals(ScheduleType.ALL.getValue(), scheduleStrategy);
        QueryWrapper queryWrapper = query()
                .eq(ETLWorkflowDO::getApplicationId, pageReqVO.getApplicationId())
                .like(ETLWorkflowDO::getWorkflowName, pageReqVO.getFlowName(), StringUtils.isNotBlank(pageReqVO.getFlowName()))
                .eq(ETLWorkflowDO::getScheduleStrategy, scheduleStrategy, filterByScheduleStrategy)
                .eq(ETLWorkflowDO::getIsEnabled, pageReqVO.getEnableStatus(), pageReqVO.getEnableStatus() != null)
                .orderBy(ETLWorkflowDO::getUpdateTime, false)
                .orderBy(ETLWorkflowDO::getCreateTime, false);
        Page<ETLWorkflowDO> pageResult = getMapper().paginate(pageReqVO.getPageNo(), pageReqVO.getPageSize(), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
