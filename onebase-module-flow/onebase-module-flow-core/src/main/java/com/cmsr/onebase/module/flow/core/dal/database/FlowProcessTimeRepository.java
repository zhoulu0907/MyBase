package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessTimeMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessTimeTableDef.FLOW_PROCESS_TIME;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessTimeRepository extends ServiceImpl<FlowProcessTimeMapper, FlowProcessTimeDO> {


    public FlowProcessTimeDO findByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_TIME.PROCESS_ID.eq(processId));
        return getOne(query);
    }

    public void deleteByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_TIME.PROCESS_ID.eq(processId));
        super.remove(query);
    }

}
