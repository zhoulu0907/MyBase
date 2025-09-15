package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessFormDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:37
 */
@Repository
public class FlowProcessFormRepository extends DataRepository<FlowProcessFormDO> {

    public FlowProcessFormRepository() {
        super(FlowProcessFormDO.class);
    }

    public FlowProcessFormDO findByProcessId(Long processId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("process_id", processId);
        return findOne(configs);
    }

    public List<FlowProcessFormDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_id", pageId);
        return findAllByConfig(configs);
    }
}
