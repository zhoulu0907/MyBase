package com.cmsr.onebase.module.bpm.core.service;

import org.dromara.warm.flow.core.entity.Definition;

/**
 * BPM流程定义扩展服务
 *
 * 供build和runtime模块使用
 *
 *
 * @author liyang
 * @date 2025-10-27
 */
public interface BpmEngineDefExtService {
    /**
     * 根据表单路径查询流程定义（查询最新的）
     *
     * @param formPath 表单路径（业务ID）
     * @return Definition 返回最新的流程定义，如果不存在返回null
     */
    Definition getByFormPath(String formPath);

    /**
     * 根据表单路径查询流程定义（根据状态查询）
     *
     * @param formPath 表单路径（业务ID）
     * @param isPublish 状态（0：草稿，1：已发布）
     * @return Definition 返回指定状态的流程定义，如果不存在返回null
     */
    Definition getByFormPathAndStatus(String formPath, Integer isPublish);
}
