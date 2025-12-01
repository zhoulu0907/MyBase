package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowNode;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowNodeMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 流程节点 仓储
 *
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowNodeRepository extends ServiceImpl<FlowNodeMapper, FlowNode> {

}


