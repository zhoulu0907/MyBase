package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.framework.orm.repo.WarmFlowBaseBizRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowDefinitionMapper;
import org.springframework.stereotype.Repository;

/**
 * 流程定义 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowDefinitionRepository extends WarmFlowBaseBizRepository<FlowDefinitionMapper, FlowDefinition> {

}


