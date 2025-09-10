package com.cmsr.onebase.module.flow.core.graph.impl;

import com.cmsr.onebase.module.flow.core.graph.FlowProcessEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 简单的流程图事件服务实现
 * 本地同步处理，不支持分布式事件通知
 *
 * @Author：huangjie
 * @Date：2025/9/5 9:37
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "onebase.flow.event",
        name = "type",
        havingValue = "simple",
        matchIfMissing = true
)
public class SimpleProcessEventService extends FlowProcessEventService {

    @Override
    public void publishProcessAdd(Long processId) {
        log.debug("本地处理流程添加事件，ProcessId: {}", processId);
        onProcessAdd(processId);
    }

    @Override
    public void publishProcessDelete(Long processId) {
        log.debug("本地处理流程删除事件，ProcessId: {}", processId);
        onProcessDelete(processId);
    }

    @Override
    public void publishProcessUpdate(Long processId) {
        log.debug("本地处理流程更新事件，ProcessId: {}", processId);
        onProcessUpdate(processId);
    }

}
