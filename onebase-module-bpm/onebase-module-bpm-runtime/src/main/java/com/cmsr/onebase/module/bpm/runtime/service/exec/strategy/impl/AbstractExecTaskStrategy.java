package com.cmsr.onebase.module.bpm.runtime.service.exec.strategy.impl;

import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.runtime.service.exec.strategy.ExecTaskStrategy;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * 抽象策略基类
 *
 * @author liyang
 * @date 2025-11-03
 */
@Service
public abstract class AbstractExecTaskStrategy<T extends BaseNodeExtDTO> implements ExecTaskStrategy<T> {
    @Resource
    protected TaskService taskService;

    @Resource
    protected DataMethodApi dataMethodApi;

    @Resource
    protected BpmFlowInsBizExtRepository insBizExtRepository;
}


