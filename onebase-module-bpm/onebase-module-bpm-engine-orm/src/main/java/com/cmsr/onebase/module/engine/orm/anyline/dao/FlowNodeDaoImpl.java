package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowNodeRepository;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.dao.FlowNodeDao;
import org.dromara.warm.flow.core.utils.CollUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * WarmFlow 节点 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode> implements FlowNodeDao<FlowNode> {

    @Resource
    private FlowNodeRepository flowNodeRepository;

    @Override
    public DataRepository<FlowNode> getRepository() {
        return flowNodeRepository;
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        ConfigStore configStore = new DefaultConfigStore();

        if (CollUtil.isNotEmpty(nodeCodes)) {
            configStore.in(FlowNode.NODE_CODE, nodeCodes);
        }

        configStore.eq(FlowNode.DEFINITION_ID, definitionId);

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowNode.DEFINITION_ID, defIds);

        return (int) getRepository().deleteByConfig(configStore);
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }
}