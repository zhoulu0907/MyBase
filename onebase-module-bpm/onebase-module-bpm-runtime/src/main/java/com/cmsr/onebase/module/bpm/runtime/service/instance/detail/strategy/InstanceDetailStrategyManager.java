package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.impl.DefaultInstanceDetailStrategy;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 流程实例详情策略管理器
 *
 * 负责选择对应的策略来处理流程实例详情获取逻辑
 * 用于待办、已办点击详情时的处理
 *
 * @author liyang
 * @date 2025-11-04
 */
@Slf4j
@Component
public class InstanceDetailStrategyManager {

    @Resource
    private List<InstanceDetailStrategy<?>> strategies;

    @Resource
    private TaskService taskService;

    @Resource
    private HisTaskService hisTaskService;

    @Resource
    private UserService userService;

    @Resource
    private DefaultInstanceDetailStrategy defaultStrategy;

    /**
     * 根据业务节点类型获取策略
     *
     * @param bizNodeType 业务节点类型
     * @return 策略实例，如果不存在返回null
     */
    public InstanceDetailStrategy<?> getStrategy(String bizNodeType) {
        for (InstanceDetailStrategy<?> strategy : strategies) {
            if (strategy.supports(bizNodeType)) {
                return strategy;
            }
        }

        log.warn("未找到流程实例详情策略，节点类型: {}", bizNodeType);
        return null;
    }

    /**
     * 处理流程实例详情（统一入口，直接填充VO）
     *
     * @param respVO 详情VO
     * @param instance 流程实例
     * @param loginUserId 登录用户ID
     */
    @SuppressWarnings("unchecked")
    public void processInstanceDetail(BpmTaskDetailRespVO respVO,
                                      BaseNodeExtDTO nodeExtDTO,
                                      Instance instance,
                                      Long loginUserId,
                                      boolean isTodo) {
        InstanceDetailStrategy<?> strategy;

        // 无节点信息，使用默认策略，如我的创建里，只返回详情视图
        if (nodeExtDTO == null) {
            strategy = defaultStrategy;
        } else {
            // 根据节点类型获取策略
            strategy = getStrategy(nodeExtDTO.getNodeType());

            // 兜底，todo 有对应的待办、但不支持的节点类型，抛出异常？
            if (strategy == null) {
                strategy = defaultStrategy;
            }
        }

        InstanceDetailStrategy<BaseNodeExtDTO> typedStrategy = (InstanceDetailStrategy<BaseNodeExtDTO>) strategy;
        typedStrategy.fillDetail(respVO, nodeExtDTO, instance, loginUserId, isTodo);
    }
}

