package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowNodeRepository;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowNodeDao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * WarmFlow 流程定义 DAO 实现
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
        // todo 待处理
        return null;
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        // todo 待处理
        return 0;
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }
}