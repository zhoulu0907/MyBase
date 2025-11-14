package com.cmsr.onebase.module.bpm.runtime.listener;

import cn.hutool.core.map.MapUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentDO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.listener.GlobalListener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Override
    public void start(ListenerVariable listenerVariable) {
        // 获取节点ext信息

        String ext = listenerVariable.getNode().getExt();
        log.info("开始启动流程，节点ext信息：{}", ext);

    }

    public void assignment(ListenerVariable listenerVariable) {
        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        log.info("开始分派任务，节点ext信息：{}", ext);
        List<Task> nextTasks = listenerVariable.getNextTasks();
        Definition definition = listenerVariable.getDefinition();
        Instance instance = listenerVariable.getInstance();

        // 查询发起节点
        List<Node> nodes = nodeService.getFirstBetweenNode(definition.getId(), new HashMap<>());
        String submitNodeCode = nodes.get(0).getNodeCode();

        for (Task flowTask : nextTasks) {
            // todo：需要指定办理人，则直接覆盖办理人

            // 如果是发起节点，则把发起人添加到办理人
            if (flowTask.getNodeCode().equals(submitNodeCode)) {
                flowTask.setPermissionList(List.of(instance.getCreateBy()));
            }
        }
    }

    public void finish(ListenerVariable listenerVariable) {

        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
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
    }

    /**
     * 创建监听器，任务创建时执行
     *
     * @param listenerVariable 监听器变量
     */
    public void create(ListenerVariable listenerVariable) {

        // 获取节点ext信息
        String ext = listenerVariable.getNode().getExt();
        log.info("开始创建任务，节点ext信息：{}", ext);

        // 此处为nextTask创建的地方，此处加上代理人的信息
        handleAgent(listenerVariable);
    }

    /**
     * 处理代理人的业务逻辑
     */
    private void handleAgent(ListenerVariable listenerVariable) {
        FlowParams flowParams = listenerVariable.getFlowParams();
        Map<String, Object> variable = flowParams.getVariable();
        Task task = listenerVariable.getTask();

        // 判断节点类型，只处理审批节点
        String nodeExt = listenerVariable.getNode().getExt();
        BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(nodeExt, BaseNodeExtDTO.class);

        if (nodeExtDTO == null) {
            return;
        }

        // 只处理审批节点和执行节点
        if (!Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.APPROVER.getCode())
            && !Objects.equals(nodeExtDTO.getNodeType(), BpmNodeTypeEnum.EXECUTOR.getCode())) {
            return;
        }

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
        Long appId = MapUtil.getLong(variable, BpmConstants.VAR_APP_ID_KEY);

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
}


