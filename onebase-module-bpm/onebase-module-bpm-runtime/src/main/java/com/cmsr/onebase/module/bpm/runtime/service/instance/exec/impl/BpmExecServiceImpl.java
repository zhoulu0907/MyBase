package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.impl;

import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentInsRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.bpm.runtime.service.instance.exec.BpmExecService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.exec.context.ExecTaskContext;
import com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy.ExecTaskStrategyManager;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程执行服务接口实现类
 *
 * @author liyang
 * @date 2025-11-23
 */
@Slf4j
@Service
public class BpmExecServiceImpl implements BpmExecService {
    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource(name = "bpmInsService")
    private InsService insService;

    @Resource(name = "bpmUserService")
    private UserService userService;

    @Resource
    private BpmFlowAgentInsRepository agentInsRepository;

    @Resource
    private ExecTaskStrategyManager execTaskStrategyManager;

    private List<ExecTaskContext> buildExecTaskContexts(ExecTaskReqVO reqVO) {
        Long taskId = reqVO.getTaskId();
        // 收集所有需要执行的任务（用户直接权限 + 所有代理人权限）
        List<ExecTaskContext> execTaskContexts = new ArrayList<>();

        List<User> users = userService.getByAssociateds(List.of(taskId),
                BpmUserTypeEnum.APPROVAL.getCode(),
                BpmUserTypeEnum.TRANSFER.getCode(),
                BpmUserTypeEnum.DEPUTE.getCode());

        if (CollectionUtils.isEmpty(users)) {
            return execTaskContexts;
        }

        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 查找当前用户直接拥有的权限
        User directMatchedUser = null;
        for (User user : users) {
            if (user.getProcessedBy().equals(String.valueOf(loginUserId))) {
                // 说明是当前登录用户拥有权限
                directMatchedUser = user;
                break;
            }
        }

        // 查找所有代理人记录（一个代理人可能代理多个被代理人）
        List<BpmFlowAgentInsDO> agentInsList = agentInsRepository.findAllByTaskIdAndAgentId(taskId, String.valueOf(loginUserId));

        // 1. 如果当前用户直接拥有权限，添加一次执行
        if (directMatchedUser != null) {
            execTaskContexts.add(new ExecTaskContext(directMatchedUser, null));
        }

        // 2. 如果是代理人，为每个被代理人添加一次执行
        if (CollectionUtils.isNotEmpty(agentInsList)) {
            for (BpmFlowAgentInsDO agentInsDO : agentInsList) {
                // 跳过已经执行过的代理人
                if (BooleanUtils.toBoolean(agentInsDO.getIsExecutor())) {
                    continue;
                }

                // 查找对应的被代理人User
                for (User user : users) {
                    String processedBy = user.getProcessedBy();
                    Long principalId = agentInsDO.getPrincipalId();

                    if (Objects.equals(processedBy, String.valueOf(principalId))) {
                        // 说明是当前登录用户拥有被代理人权限
                        execTaskContexts.add(new ExecTaskContext(user, agentInsDO));
                        break;
                    }
                }
            }
        }

        return execTaskContexts;
    }

    @Override
    public void execTask(ExecTaskReqVO reqVO) {
        Long taskId = reqVO.getTaskId();

        BpmActionButtonEnum buttonEnum = BpmActionButtonEnum.getByCode(reqVO.getButtonType());
        if (buttonEnum == null) {
            throw exception(ErrorCodeConstants.UNSUPPORT_ACTION_BUTTON_TYPE);
        }

        // 第一次查询task，用于权限检查
        Task task = taskService.getById(taskId);
        if (task == null) {
            throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
        }

        Instance instance = insService.getById(task.getInstanceId());
        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        if (instance.getBusinessId() == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        Long entityDataId = Long.parseLong(instance.getBusinessId());

        // 忽略前端传的entityDataId，使用流程实例绑定的实体数据ID
        if (reqVO.getEntity() != null) {
            reqVO.getEntity().setId(entityDataId);
        }

        String taskNodeCode = task.getNodeCode();
        BaseNodeExtDTO extDTO = BpmUtil.getNodeExtDTOByNodeCode(taskNodeCode, instance.getDefJson());

        if (extDTO == null) {
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
        }

        // 校验实体ID
        if (reqVO.getEntity() != null) {
            Long entityId = MapUtils.getLong(instance.getVariableMap(), BpmConstants.VAR_ENTITY_ID_KEY);

            if (entityId == null) {
                throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
            }

            if (!entityId.equals(reqVO.getEntity().getEntityId())) {
                throw exception(ErrorCodeConstants.INVALID_ENTITY_ID);
            }
        }

        // 构建所有需要执行的任务上下文
        List<ExecTaskContext> execTaskContexts = buildExecTaskContexts(reqVO);

        // 如果没有任何匹配的任务的执行权限，返回权限不足错误
        if (execTaskContexts.isEmpty()) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
        }

        // 循环执行所有匹配的任务（当前用户直接权限 + 所有代理人权限）
        // 注意：第二次及以后需要重新查询task，因为或签任意一个执行完就结束了，会签有一个拒绝也会结束
        for (int i = 0; i < execTaskContexts.size(); i++) {
            ExecTaskContext context = execTaskContexts.get(i);
            boolean isFirstExecution = (i == 0);
            Task currentTask;

            log.info("[execTask][开始执行任务(任务ID: {}, 被代理人ID: {}, 代理人记录ID: {})]",
                    taskId, context.getMatchedUser().getId(), context.getAgentInsDO() != null ? context.getAgentInsDO().getId() : "无");

            if (isFirstExecution) {
                // 第一次执行，使用已经查询过的task
                currentTask = task;
            } else {
                // 第二次及以后，重新查询task，检查任务是否还存在
                currentTask = taskService.getById(taskId);
                if (currentTask == null) {
                    // 任务已不存在（可能已完成或被删除），停止执行
                    break;
                }
            }

            execTaskStrategyManager.execute(context.getMatchedUser(), context.getAgentInsDO(), currentTask, extDTO, reqVO);
        }
    }
}
