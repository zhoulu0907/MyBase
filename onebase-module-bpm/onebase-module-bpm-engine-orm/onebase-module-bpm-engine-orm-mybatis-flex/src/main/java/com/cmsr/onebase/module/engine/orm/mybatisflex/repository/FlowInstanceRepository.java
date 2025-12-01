package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowInstanceMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 流程实例 仓储
 *
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowInstanceRepository extends ServiceImpl<FlowInstanceMapper, FlowInstance> {

}