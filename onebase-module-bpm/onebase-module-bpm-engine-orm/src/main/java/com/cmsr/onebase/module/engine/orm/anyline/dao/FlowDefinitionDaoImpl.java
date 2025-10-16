package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowDefinitionRepository;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao;

import java.util.List;

/**
 * WarmFlow 流程定义 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinition> implements FlowDefinitionDao<FlowDefinition> {

    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Override
    public FlowDefinitionRepository getRepository() {
        return flowDefinitionRepository;
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowDefinition.FLOW_CODE, flowCodeList);
        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowDefinition.ID, ids);
        DataRow row = new DataRow();
        row.put(FlowDefinition.IS_PUBLISH, publishStatus);
        getRepository().updateByConfig(row, configStore);
    }

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }
}