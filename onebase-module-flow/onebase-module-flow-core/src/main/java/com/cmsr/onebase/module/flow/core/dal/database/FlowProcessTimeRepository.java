package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessTimeMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessTimeTableDef.FLOW_PROCESS_TIME;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessTimeRepository extends BaseAppRepository<FlowProcessTimeMapper, FlowProcessTimeDO> {


    public FlowProcessTimeDO findByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_TIME.PROCESS_ID.eq(processId));
        return getMapper().selectOneByQuery(query);
    }

    public void deleteByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_TIME.PROCESS_ID.eq(processId));
        super.getMapper().deleteByQuery(query);
    }

    public void updateJobStatusByAppId(String status, Long applicationId) {
        FlowProcessTimeDO flowProcessTimeDO = UpdateEntity.of(FlowProcessTimeDO.class);
        flowProcessTimeDO.setJobStatus(status);
        QueryWrapper query = this.query().where(FLOW_PROCESS_TIME.APPLICATION_ID.eq(applicationId));
        super.getMapper().updateByQuery(flowProcessTimeDO, query);
    }

    public List<FlowProcessTimeDO> findByAppIdAndProcessIdsNotIn(Long applicationId, Set<Long> processIds) {
        QueryWrapper query = this.query()
                .where(FLOW_PROCESS_TIME.APPLICATION_ID.eq(applicationId))
                .and(FLOW_PROCESS_TIME.PROCESS_ID.notIn(processIds));
        return getMapper().selectListByQuery(query);
    }
}
