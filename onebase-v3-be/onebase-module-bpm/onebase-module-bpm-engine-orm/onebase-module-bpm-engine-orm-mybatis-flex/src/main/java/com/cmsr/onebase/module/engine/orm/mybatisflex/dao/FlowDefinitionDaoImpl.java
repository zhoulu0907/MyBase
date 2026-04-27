package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowDefinitionMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao;

import java.util.List;

/**
 * WarmFlow 流程定义 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinitionMapper, FlowDefinition> implements FlowDefinitionDao<FlowDefinition> {
    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Override
    public ServiceImpl<FlowDefinitionMapper, FlowDefinition> getRepository() {
        return flowDefinitionRepository;
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowDefinition::getFlowCode, flowCodeList);

        return getRepository().list(queryWrapper);
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowDefinition::getId, ids);

        getRepository().update(new FlowDefinition().setIsPublish(publishStatus), queryWrapper);
    }

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }
}