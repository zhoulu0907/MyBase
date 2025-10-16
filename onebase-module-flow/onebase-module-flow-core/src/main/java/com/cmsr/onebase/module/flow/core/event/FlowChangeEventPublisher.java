package com.cmsr.onebase.module.flow.core.event;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:31
 */

public interface FlowChangeEventPublisher {

    void publishApplicationUpdate(Long applicationId);

    void publishApplicationDelete(Long applicationId);

}