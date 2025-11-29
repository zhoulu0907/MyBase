package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowDefinitionMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 流程定义 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowDefinitionRepository extends ServiceImpl<FlowDefinitionMapper, FlowDefinition> {

}


