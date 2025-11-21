package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import org.dromara.warm.flow.core.entity.Instance;

/**
 * 流程实例详情策略接口
 *
 * 用于处理不同节点类型的流程实例详情获取逻辑（按钮配置、字段权限等）
 * 用于待办、已办点击详情时的处理
 *
 * @author liyang
 * @date 2025-11-04
 */
public interface InstanceDetailStrategy<T extends BaseNodeExtDTO> {

    /**
     * 当前策略是否支持该业务节点类型
     *
     * @param bizNodeType 节点业务类型编码
     * @return 是否支持
     */
    boolean supports(String bizNodeType);

    /**
     * 填充流程实例详情到VO
     *
     * @param vo 详情VO
     * @param extDTO 节点扩展信息
     * @param task 任务（可能为null）
     * @param instance 流程实例
     * @param loginUserId 登录用户ID
     */
    void fillDetail(BpmTaskDetailRespVO vo, T extDTO, Instance instance, Long loginUserId, boolean isTodo);
}

