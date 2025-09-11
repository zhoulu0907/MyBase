package com.cmsr.onebase.module.flow.build.event;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:31
 */

public interface FlowProcessEventPublisher {

    void publishProcessUpdate(Long processId);

    void publishProcessDelete(Long processId);

}