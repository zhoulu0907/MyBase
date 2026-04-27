package com.cmsr.onebase.module.flow.api;

/**
 * @Author：huangjie
 * @Date：2025/12/8 16:05
 */
public interface FlowDataManager {

    // 实现备份逻辑
    // 执行update动作。
    // 1、update：把versionTag为1的数据update为新值（参数`versionTag`）
    void moveRuntimeToHistory(Long applicationId, Long versionTag);

    // 实现发布逻辑
    // 执行select 和 insert 动作。
    // 1、select： versionTag为0的数据
    // 2、insert：把第一步查询出来的数据插入为versionTag为1
    void copyEditToRuntime(Long applicationId);

    void deleteRuntimeData(Long applicationId);

    void updateRuntimeData(Long applicationId);

    void deleteAllApplicationData(Long applicationId);

    void deleteApplicationVersionData(Long applicationId, Long versionId);
}
