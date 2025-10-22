package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowExecutionLogDO;
import com.cmsr.onebase.module.flow.core.vo.PageExecutionLogReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowExecutionLogRepository extends DataRepository<FlowExecutionLogDO> {


    public FlowExecutionLogRepository() {
        super(FlowExecutionLogDO.class);
    }

    public FlowExecutionLogDO findByExecutionUuid(String executionUuid) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("execution_uuid", executionUuid);
        return findOne(configs);
    }

    public PageResult<FlowExecutionLogDO> findPageByQuery(PageExecutionLogReqVO reqVO) {
        ConfigStore configs = new DefaultConfigStore();
        if (reqVO.getApplicationId() != null) {
            configs.eq("application_id", reqVO.getApplicationId());
        }
        if (reqVO.getProcessId() != null) {
            configs.eq("process_id", reqVO.getProcessId());
        }
        configs.order(BaseDO.UPDATE_TIME, Order.TYPE.DESC);
        return this.findPageWithConditions(configs, reqVO.getPageNo(), reqVO.getPageSize());
    }

    public Map<String, Integer> statisticTodyByApplicationId(Long applicationId) {
        ConfigStore configs = new DefaultConfigStore();
        return null;
    }
}
