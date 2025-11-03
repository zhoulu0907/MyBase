package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.etl.WorkflowPageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLWorkflowRepository extends DataRepository<ETLWorkflowDO> {

    public ETLWorkflowRepository() {
        super(ETLWorkflowDO.class);
    }

    public PageResult<ETLWorkflowDO> getWorkflowPage(WorkflowPageReqVO pageReqVO) {
        ConfigStore cs = new DefaultConfigStore();
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            cs.like("workflow_name", pageReqVO.getName());
        }
        String scheduleStrategy = pageReqVO.getScheduleStrategy();
        if (StringUtils.isNotBlank(scheduleStrategy) && !StringUtils.equals(ScheduleType.ALL.getValue(), scheduleStrategy)) {
            cs.eq("schedule_strategy", scheduleStrategy);
        }
        if (pageReqVO.getEnabled() != null) {
            cs.eq("is_enabled", pageReqVO.getEnabled() ? 1 : 0);
        }
        cs.order("create_time", Order.TYPE.DESC);
        cs.order("update_time", Order.TYPE.DESC);
        return findPageWithConditions(cs, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    public ETLWorkflowDO findOneByNameFilterById(String flowName, Long workflowId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("workflow_name", flowName);
        if (workflowId != null) {
            cs.ne("id", workflowId);
        }
        return findOne(cs);
    }
}
