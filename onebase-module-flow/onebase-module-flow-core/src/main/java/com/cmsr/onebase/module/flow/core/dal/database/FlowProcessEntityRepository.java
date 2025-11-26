package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessEntityDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessEntityMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessEntityTableDef.FLOW_PROCESS_ENTITY;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessEntityRepository extends ServiceImpl<FlowProcessEntityMapper, FlowProcessEntityDO> {

    public FlowProcessEntityDO findByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_ENTITY.PROCESS_ID.eq(processId));
        return getOne(query);
    }

    public void deleteByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_ENTITY.PROCESS_ID.eq(processId));
        remove(query);
    }

}
