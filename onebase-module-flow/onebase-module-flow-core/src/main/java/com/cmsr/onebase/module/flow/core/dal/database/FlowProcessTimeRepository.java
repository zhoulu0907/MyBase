package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessTimeRepository extends DataRepository<FlowProcessTimeDO> {

    public FlowProcessTimeRepository() {
        super(FlowProcessTimeDO.class);
    }

    public FlowProcessTimeDO findByProcessId(Long processId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("process_id", processId);
        return findOne(configs);
    }

    public void deleteByProcessId(Long processId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("process_id", processId);
        deleteByConfig(configs);
    }
}
