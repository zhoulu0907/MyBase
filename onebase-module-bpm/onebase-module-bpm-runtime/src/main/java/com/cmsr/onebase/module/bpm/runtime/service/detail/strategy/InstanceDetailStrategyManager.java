package com.cmsr.onebase.module.bpm.runtime.service.detail.strategy;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.impl.DefaultInstanceDetailStrategy;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.springframework.stereotype.Component;

import java.util.*;

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
     * 检查用户是否有权限操作流程实例的待办任务
     *
     * @param instanceId 流程实例ID
     * @param loginUserId 登录用户ID
     * @return 待办任务，如果无权限则返回null
     */
    protected Task checkAndGetTodoTask(Long instanceId, Long loginUserId) {
        // 查询该实例的待办任务
        List<Task> tasks = taskService.getByInsId(instanceId);
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }

        List<Long> taskIds = new ArrayList<>();
        Map<Long, Task> taskMap = new HashMap<>();

        for (Task task : tasks) {
            taskIds.add(task.getId());
            taskMap.put(task.getId(), task);
        }

        // 查询任务关联的用户（审批人、转交人、委派人）
        List<User> users = userService.getByAssociateds(
                taskIds,
                BpmUserTypeEnum.APPROVAL.getCode(),
                BpmUserTypeEnum.TRANSFER.getCode(),
                BpmUserTypeEnum.DEPUTE.getCode(),
                BpmUserTypeEnum.AGENT.getCode()
        );

        if (CollectionUtils.isEmpty(users)) {
            return null;
        }

        // 检查当前用户是否有权限
        for (User user : users) {
            if (Objects.equals(user.getProcessedBy(), String.valueOf(loginUserId))) {
                return taskMap.get(user.getAssociated());
            }
        }

        return null;
    }

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
     * @param reqVO 请求VO
     * @param instance 流程实例
     * @param loginUserId 登录用户ID
     */
    @SuppressWarnings("unchecked")
    public void processInstanceDetail(BpmTaskDetailRespVO respVO,
                                      BpmTaskDetailReqVO reqVO,
                                      Instance instance,
                                      Long loginUserId) {

        // 先获取实例当前节点配置
        String nodeCode = instance.getNodeCode();
        InstanceDetailStrategy<?> strategy;

        // 检查权限并获取待办任务
        Task currTask = checkAndGetTodoTask(instance.getId(), loginUserId);

        // 当前无已办，则以历史的已办任务决定字段显隐藏
        if (currTask == null) {
            if (reqVO.getTaskId() != null) {
                log.info("当前无待办任务，尝试从已办中获取任务ID: {}", reqVO.getTaskId());
                // 有指定任务ID，尝试从已办中获取
                FlowHisTask hisTaskQuery = new FlowHisTask();
                hisTaskQuery.setTaskId(reqVO.getTaskId());

                HisTask hisTask = hisTaskService.getOne(hisTaskQuery);
                if (hisTask != null) {
                    nodeCode = hisTask.getNodeCode();
                }
            }
        } else {
            // 以待办为准，获取当前节点编码
            nodeCode = currTask.getNodeCode();
        }

        BaseNodeExtDTO nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(nodeCode, instance.getDefJson());
        strategy = getStrategy(nodeExtDTO.getNodeType());

        // 兜底，todo 有待办、但不支持的节点类型，抛出异常？
        if (strategy == null) {
            strategy = defaultStrategy;
        }

        InstanceDetailStrategy<BaseNodeExtDTO> typedStrategy = (InstanceDetailStrategy<BaseNodeExtDTO>) strategy;
        typedStrategy.fillDetail(respVO, nodeExtDTO, currTask, instance, loginUserId);
    }
}

