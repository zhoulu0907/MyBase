package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowTaskMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 待办任务 仓储
 *
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowTaskRepository extends ServiceImpl<FlowTaskMapper, FlowTask> {

}