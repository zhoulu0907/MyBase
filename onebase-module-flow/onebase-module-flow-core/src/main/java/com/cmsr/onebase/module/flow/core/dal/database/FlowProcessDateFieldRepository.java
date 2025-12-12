package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessDateFieldMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessDateFieldTableDef.FLOW_PROCESS_DATE_FIELD;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessDateFieldRepository extends ServiceImpl<FlowProcessDateFieldMapper, FlowProcessDateFieldDO> {


    public FlowProcessDateFieldDO findByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_DATE_FIELD.PROCESS_ID.eq(processId));
        return getOne(query);
    }

    public void deleteByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_DATE_FIELD.PROCESS_ID.eq(processId));
        super.remove(query);
    }

    public void updateJobStatus(String status, List<Long> ids) {
        updateChain().set(FLOW_PROCESS_DATE_FIELD.JOB_STATUS, status)
                .where(FLOW_PROCESS_DATE_FIELD.ID.in(ids))
                .update();
    }
}
