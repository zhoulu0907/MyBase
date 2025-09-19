package com.cmsr.onebase.module.flow.api;

/**
 * @Author：huangjie
 * @Date：2025/9/19 14:22
 */
public interface FlowProcessPublishApi {

    void onlineApplicationFlowProcess(Long applicationId);

    void offlineApplicationFlowProcess(Long applicationId);
}
