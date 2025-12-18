package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessDateFieldMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessDateFieldTableDef.FLOW_PROCESS_DATE_FIELD;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessDateFieldRepository extends BaseAppRepository<FlowProcessDateFieldMapper, FlowProcessDateFieldDO> {

    public FlowProcessDateFieldDO findByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_DATE_FIELD.PROCESS_ID.eq(processId));
        return getMapper().selectOneByQuery(query);
    }

    public void deleteByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_DATE_FIELD.PROCESS_ID.eq(processId));
        super.getMapper().deleteByQuery(query);
    }

    public void updateJobStatusByAppId(String status, Long applicationId) {
        FlowProcessDateFieldDO flowProcessDateFieldDO = UpdateEntity.of(FlowProcessDateFieldDO.class);
        flowProcessDateFieldDO.setJobStatus(status);
        QueryWrapper query = this.query().where(FLOW_PROCESS_DATE_FIELD.APPLICATION_ID.eq(applicationId));
        super.getMapper().updateByQuery(flowProcessDateFieldDO, query);
    }

}
