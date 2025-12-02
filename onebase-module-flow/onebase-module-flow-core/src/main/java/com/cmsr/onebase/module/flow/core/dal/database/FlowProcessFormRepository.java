package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessFormDO;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessFormMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowProcessFormTableDef.FLOW_PROCESS_FORM;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessFormRepository extends ServiceImpl<FlowProcessFormMapper, FlowProcessFormDO> {

    public void deleteByProcessId(Long processId) {
        QueryWrapper query = this.query().where(FLOW_PROCESS_FORM.PROCESS_ID.eq(processId));
        super.remove(query);
    }
}
