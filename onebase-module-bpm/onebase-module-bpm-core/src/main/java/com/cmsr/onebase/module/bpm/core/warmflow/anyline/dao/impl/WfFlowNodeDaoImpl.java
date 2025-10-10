package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowNodeRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowNode;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowNodeDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * WarmFlow 流程定义 DAO 实现 V2
 * 使用 WfFlowDefinitionDov2 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class WfFlowNodeDaoImpl implements FlowNodeDao<WfFlowNode> {

    @Resource
    private FlowNodeRepository flowNodeRepository;


    @Override
    public List<WfFlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return null;
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        return 0;
    }

    @Override
    public WfFlowNode newEntity() {
        return null;
    }

    @Override
    public WfFlowNode selectById(Serializable id) {
        return null;
    }

    @Override
    public List<WfFlowNode> selectByIds(Collection<? extends Serializable> ids) {
        return null;
    }

    @Override
    public Page<WfFlowNode> selectPage(WfFlowNode entity, Page<WfFlowNode> page) {
        return null;
    }

    @Override
    public List<WfFlowNode> selectList(WfFlowNode entity, WarmQuery<WfFlowNode> query) {
        return null;
    }

    @Override
    public long selectCount(WfFlowNode entity) {
        return 0;
    }

    @Override
    public int save(WfFlowNode entity) {
        return 0;
    }

    @Override
    public int updateById(WfFlowNode entity) {
        return 0;
    }

    @Override
    public int delete(WfFlowNode entity) {
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
        return 0;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return 0;
    }

    @Override
    public void saveBatch(List<WfFlowNode> list) {

    }

    @Override
    public void updateBatch(List<WfFlowNode> list) {

    }
}