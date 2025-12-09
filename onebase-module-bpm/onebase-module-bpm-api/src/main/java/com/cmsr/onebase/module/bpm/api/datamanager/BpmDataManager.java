package com.cmsr.onebase.module.bpm.api.datamanager;

/**
 *
 * 数据管理API
 *
 * @author liyang
 * @date 2025-12-08
 */
public interface BpmDataManager {
    /**
     *  备份运行态数据为历史版本
     *  实现备份逻辑 执行update动作。
     *  1、update：把varsionTag为1的数据update为新值（参数`versionTag`）
     *  @param applicationId
     *  @param versionTag
     */
    void moveRuntimeToHistory(Long applicationId, Long versionTag);

    /**
     *  编辑态数据变成运行态数据
     *  执行select 和 insert 动作。
     *   1、select： varsionTag为0的数据
     *   2、insert：把第一步查询出来的数据插入为varsionTag为1
     *   @param applicationId
     *
      */
    void copyEditToRuntime(Long applicationId);
}
