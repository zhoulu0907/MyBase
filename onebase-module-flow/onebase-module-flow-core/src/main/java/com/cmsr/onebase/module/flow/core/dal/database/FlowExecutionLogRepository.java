package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowExecutionLogDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowExecutionLogMapper;
import com.cmsr.onebase.module.flow.core.vo.PageExecutionLogReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowExecutionLogTableDef.FLOW_EXECUTION_LOG;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowExecutionLogRepository extends BaseAppRepository<FlowExecutionLogMapper, FlowExecutionLogDO> {


    public FlowExecutionLogDO findByExecutionUuid(String executionUuid) {
        QueryWrapper query = this.query().where(FLOW_EXECUTION_LOG.EXECUTION_UUID.eq(executionUuid));
        return getOne(query);
    }

    public PageResult<FlowExecutionLogDO> findPageByQuery(PageExecutionLogReqVO reqVO) {
        QueryWrapper query = this.query()
                .where(FLOW_EXECUTION_LOG.APPLICATION_ID.eq(reqVO.getApplicationId()).when(reqVO.getApplicationId() != null))
                .where(FLOW_EXECUTION_LOG.PROCESS_ID.eq(reqVO.getProcessId()).when(reqVO.getProcessId() != null))
                .orderBy(FLOW_EXECUTION_LOG.UPDATE_TIME, false);
        Page page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<FlowExecutionLogDO> pageData = this.page(page, query);
        return new PageResult(pageData.getRecords(), pageData.getTotalRow());
    }

    public Map<String, Object> statisticByApplicationId(LocalDateTime day, Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        //
        QueryCondition filterCondition = QueryCondition.createEmpty()
                .and(FLOW_EXECUTION_LOG.APPLICATION_ID.eq(applicationId))
                .and(FLOW_EXECUTION_LOG.START_TIME.ge(day.withHour(0).withMinute(0).withSecond(0).withNano(0)))
                .and(FLOW_EXECUTION_LOG.START_TIME.le(day.withHour(23).withMinute(59).withSecond(59).withNano(999999999)));
        {
            QueryWrapper queryWrapper = this.query()
                    .select(
                            FLOW_EXECUTION_LOG.EXECUTION_RESULT.as("result"),
                            QueryMethods.count(FLOW_EXECUTION_LOG.ID).as("counts")
                    )
                    .where(filterCondition)
                    .groupBy(FLOW_EXECUTION_LOG.EXECUTION_RESULT);
            int total = 0;
            List<Row> dataSet = this.listAs(queryWrapper, Row.class);
            for (Row dataRow : dataSet) {
                String key = dataRow.getString("result");
                long value = dataRow.getLong("counts");
                result.put(key, value);
                total += value;
            }
            result.put("total", total);
        }
        {
            QueryWrapper queryWrapper = this.query()
                    .select(
                            QueryMethods.avg(FLOW_EXECUTION_LOG.DURATION_TIME).as("avgs")
                    )
                    .where(filterCondition);
            Long avgs = this.getObjAs(queryWrapper, Long.class);
            result.put("avgs", avgs);
        }
        return result;
    }
}
