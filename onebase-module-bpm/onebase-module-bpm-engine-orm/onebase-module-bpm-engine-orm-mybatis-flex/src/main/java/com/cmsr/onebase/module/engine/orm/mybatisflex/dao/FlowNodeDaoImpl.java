package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowNodeMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowNodeRepository;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
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
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNodeMapper, FlowNode> implements FlowNodeDao<FlowNode> {
    @Resource
    private FlowNodeRepository flowNodeRepository;

    @Override
    public ServiceImpl<FlowNodeMapper, FlowNode> getRepository() {
        return flowNodeRepository;
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowNode::getNodeCode, nodeCodes, CollUtil.isNotEmpty(nodeCodes));
        queryWrapper.eq(FlowNode::getDefinitionId, definitionId);

        return getRepository().list(queryWrapper);
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowNode::getDefinitionId, defIds);

        return getRepository().remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }
}