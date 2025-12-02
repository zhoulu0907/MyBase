package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowHisTaskMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 历史任务 仓储
 *
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowHisTaskRepository extends ServiceImpl<FlowHisTaskMapper, FlowHisTask> {
}


