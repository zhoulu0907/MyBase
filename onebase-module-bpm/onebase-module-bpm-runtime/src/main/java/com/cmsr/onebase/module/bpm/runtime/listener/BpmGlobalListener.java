package com.cmsr.onebase.module.bpm.runtime.listener;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentDO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.listener.GlobalListener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liyang
 */
@Slf4j
@Component
public class BpmGlobalListener implements GlobalListener {
    @Resource
    private TaskService taskService;

    @Resource
    private InsService insService;

    @Resource
    private NodeService nodeService;

    @Resource
    private UserService userService;

    @Resource
    private BpmFlowAgentRepository agentRepository;

    @Resource
    private BpmCcNodeListener ccNodeListener;

    @Override
    public void start(ListenerVariable listenerVariable) {
        // 获取节点ext信息

        String ext = listenerVariable.getNode().getExt();
        log.info("开始启动流程，节点ext信息：{}", ext);
    }

    public void assignment(ListenerVariable listenerVariable) {
        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        List<Task> nextTasks = listenerVariable.getNextTasks();
        Instance instance = listenerVariable.getInstance();
        Task currTask = listenerVariable.getTask();
        FlowParams flowParams = listenerVariable.getFlowParams();
        Map<String, Object> flowVariable = listenerVariable.getVariable();

        log.info("开始分派任务，节点ext信息：{}", ext);

        for (Task nextTask : nextTasks) {
            BaseNodeExtDTO nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(nextTask.getNodeCode(), listenerVariable.getInstance().getDefJson());

            if (nodeExtDTO == null) {
                log.warn("节点ext信息为空 nodeCode: {} nodeName: {}", nextTask.getNodeCode(), nextTask.getNodeName());
                continue;
            }

            // todo：需要指定办理人，则直接覆盖办理人

            // 如果是发起节点，则把发起人添加到办理人
            if (Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.INITIATION.getCode())) {
                nextTask.setPermissionList(List.of(instance.getCreateBy()));
                continue;
            }

            // 处理下一个抄送节点，只保留系统用户权限
            if (Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.CC.getCode())) {
                List<String> ccPermissionList = nextTask.getPermissionList();

                // 存入当前节点的变量值里
                flowVariable.put(BpmConstants.VAR_CC_USERS_KEY + "_" + nextTask.getNodeCode(),
                        JsonUtils.toJsonString(ccPermissionList));

                // 清空权限人，使用系统用户执行
                nextTask.setPermissionList(new ArrayList<>());
            }
        }

        // 处理未操作的用户
        handleUnOperatorUsersOnAssignment(listenerVariable);
    }

    public void finish(ListenerVariable listenerVariable) {
        Node currNode = listenerVariable.getNode();
        Map<String, Object> flowVariable = listenerVariable.getVariable();

        // 获取节点ext信息
        String ext = currNode.getExt();
        log.info("完成任务，节点ext信息：{}", ext);

        // 判断下，如果是最后一个节点就直接结束
        Instance instance = listenerVariable.getInstance();

        if (CollectionUtils.isEmpty(taskService.getByInsId(instance.getId()))) {
            // 结束流程
            String status = BpmBusinessStatusEnum.APPROVED.getCode();
            // 更新流程状态为已通过
            instance.setFlowStatus(status);
            insService.updateById(instance);
        }

        BaseNodeExtDTO nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(currNode.getNodeCode(), listenerVariable.getInstance().getDefJson());

        if (nodeExtDTO == null) {
            return;
        }

        if (Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.CC.getCode())) {
            ccNodeListener.handleFinish(listenerVariable);
        }

        // 抄送给未处理的用户
        if (Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.APPROVER.getCode())
                || Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.EXECUTOR.getCode())) {
            Task currTask = listenerVariable.getTask();

            ccNodeListener.handleCcUsers(currTask, flowVariable);
        }

        if (flowVariable != null) {
            // 清理代理人信息
            flowVariable.remove("agentId");
        }
    }

    /**
     * 创建监听器，任务创建时执行
     *
     * @param listenerVariable 监听器变量
     */
    public void create(ListenerVariable listenerVariable) {

        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        Task currTask = listenerVariable.getTask();

        log.info("开始创建任务，节点ext信息：{}", ext);

        BaseNodeExtDTO nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(currTask.getNodeCode(), listenerVariable.getInstance().getDefJson());

        if (nodeExtDTO == null) {
            log.warn("节点ext信息为空 nodeCode: {} nodeName: {}", currTask.getNodeCode(), currTask.getNodeName());
            return;
        }

        // 只处理审批节点和执行节点，增加代理人的信息
        if (Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.APPROVER.getCode())
                || Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.EXECUTOR.getCode())) {
            // 此处为nextTask创建的地方，加上代理人的信息
            handleAgentOnCreate(listenerVariable);
        }

        if (Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.CC.getCode())) {
            ccNodeListener.handleCreate(listenerVariable);
        }
    }

    /**
     * 处理代理人的业务逻辑
     */
    private void handleAgentOnCreate(ListenerVariable listenerVariable) {
        Map<String, Object> variable = listenerVariable.getVariable();
        Task task = listenerVariable.getTask();

        Set<Long> approvalUserIds = new HashSet<>();

        for (User user : task.getUserList()) {
            // 只处理审批人类型的用户
            if (!Objects.equals(user.getType(), BpmUserTypeEnum.APPROVAL.getCode())) {
                continue;
            }

            approvalUserIds.add(Long.valueOf(user.getProcessedBy()));
        }

        // 如果没有审批人类型的用户，则直接返回
        if (CollectionUtils.isEmpty(approvalUserIds)) {
            return;
        }

        // todo：确保appId不为空
        Long appId = MapUtils.getLong(variable, BpmConstants.VAR_APP_ID_KEY);

        if (appId == null) {
            return;
        }

        List<BpmFlowAgentDO> activeAgents = agentRepository.findAllActiveAgent(appId, approvalUserIds);

        if (CollectionUtils.isEmpty(activeAgents)) {
            return;
        }

        List<User> agentUsers = new ArrayList<>();

        // 增加代理人信息
        for (BpmFlowAgentDO agent : activeAgents) {
            User user = userService.structureUser(
                    task.getId(),
                    String.valueOf(agent.getAgentId()),
                    BpmUserTypeEnum.AGENT.getCode(),
                    String.valueOf(agent.getPrincipalId())
            );
            agentUsers.add(user);
        }

        // 保存代理用户
        userService.saveBatch(agentUsers);
    }

    private void handleUnOperatorUsersOnAssignment(ListenerVariable listenerVariable) {
        Task currTask = listenerVariable.getTask();
        FlowParams flowParams = listenerVariable.getFlowParams();
        Map<String, Object> flowVariable = listenerVariable.getVariable();

        // 不一定有待办
        if (currTask == null) {
            return;
        }

        // 查找剩余未操作的用户
        List<User> unOperatorUsers = userService.listByAssociatedAndTypes(currTask.getId());

        if (CollectionUtils.isNotEmpty(unOperatorUsers)) {
            String currHandler = flowParams.getHandler();
            String agentId = MapUtils.getString(flowVariable, "agentId");

            Set<String> ccPermissionList = unOperatorUsers.stream()
                .filter(item -> {
                    // 排除自己
                    if (Objects.equals(item.getProcessedBy(), currHandler)) {
                        return false;
                    }

                    // 排除系统用户
                    if (Objects.equals(item.getProcessedBy(), BpmConstants.SYS_USER_ID)) {
                        return false;
                    }

                    // 代理人执行的，排除当前指定被代理人的代理用户
                    if (StringUtils.isNotBlank(agentId)
                            && Objects.equals(item.getType(), BpmUserTypeEnum.AGENT.getCode())
                            && Objects.equals(item.getCreateBy(), currHandler)
                            && Objects.equals(item.getProcessedBy(), agentId)) {
                        return false;
                    }

                    return true;
                })
                .map(User::getProcessedBy)
                .collect(Collectors.toSet());

            // todo：同一个节点，是否会同时有抄送和未操作的用户，确认是否会覆盖
            if (CollectionUtils.isNotEmpty(ccPermissionList)) {
                flowVariable.put(BpmConstants.VAR_CC_USERS_KEY + "_" + currTask.getNodeCode(),
                        JsonUtils.toJsonString(ccPermissionList));
            }
        }
    }
}
