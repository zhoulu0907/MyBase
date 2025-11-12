package com.cmsr.onebase.module.bpm.runtime.service.exec.strategy;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 策略管理器
 *
 * 负责：
 * 1) 选择对应策略
 * 2) 组装执行上下文
 * 3) 触发执行
 *
 * @author liyang
 * @date 2025-11-03
 */
@Component
public class ExecTaskStrategyManager {

    @Resource
    private List<ExecTaskStrategy<?>> strategies;

    /**
     * 根据业务节点类型获取策略
     *
     * @param bizNodeType 业务节点类型
     * @return 策略实例，如果不存在返回null
     */
    public ExecTaskStrategy<?> getStrategy(String bizNodeType) {
        for (ExecTaskStrategy<?> strategy : strategies) {
            if (strategy.supports(bizNodeType)) {
                return strategy;
            }
        }
        return null;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     * @param extDTO 节点扩展信息
     * @param reqVO 请求参数
     */
    @SuppressWarnings("unchecked")
    public void execute(Task task, BaseNodeExtDTO extDTO, ExecTaskReqVO reqVO) {
        ExecTaskStrategy<?> strategy = getStrategy(extDTO.getNodeType());
        if (strategy == null) {
            throw new IllegalStateException("No ExecTaskStrategy for bizNodeType: " + extDTO.getNodeType());
        }

        // 由于策略使用泛型，需要强制转换，但这是安全的，因为我们已经通过 supports 方法验证了类型匹配
        ((ExecTaskStrategy<BaseNodeExtDTO>) strategy).execute(task, extDTO, reqVO);
    }
}


