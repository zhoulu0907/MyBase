package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowExecutionLogDO;
import com.cmsr.onebase.module.flow.core.vo.PageExecutionLogReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
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

    public Map<String, Object> statisticTodyByApplicationId(Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("application_id", applicationId);
        configs.ge("start_time", LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
        configs.le("start_time", LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999));
        int total = 0;
        {
            DataSet dataSet = this.querys("""
                            select execution_result as result, count(*) as counts 
                            from flow_execution_log
                            """, configs,
                    "group by execution_result");

            for (DataRow dataRow : dataSet) {
                String key = dataRow.getString("result");
                int value = dataRow.getInt("counts");
                result.put(key, value);
                total += value;
            }
            result.put("total", total);
        }
        {
            DataSet dataSet = this.querys("""
                            select execution_result as result, count(*) as counts 
                            from flow_execution_log
                            """, configs,
                    "group by execution_result");
        }
        return null;
    }
}
