package com.cmsr.onebase.module.flow.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerFormDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/29 14:38
 */
@Repository
public class FlowProcessTriggerFormRepository extends DataRepository<FlowProcessTriggerFormDO> {

    public FlowProcessTriggerFormRepository() {
        super(FlowProcessTriggerFormDO.class);
    }

    public FlowProcessTriggerFormDO findByProcessId(Long processId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("process_id", processId);
        return findOne(configs);
    }
    
    /**
     * 根据页面ID查询表单触发配置
     * @param pageId 页面ID
     * @return 表单触发配置列表
     */
    public List<FlowProcessTriggerFormDO> findByPageId(Long pageId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq("page_id", pageId);
        return findAllByConfig(configs);
    }
}
