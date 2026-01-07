package com.cmsr.onebase.module.bpm.runtime.service.instance.operator.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentInsRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowCcRecordRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmFlowDefinitionRepositoryExt;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeApproveStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.bpm.runtime.service.common.permission.BpmPermissionResolver;
import com.cmsr.onebase.module.bpm.runtime.service.instance.operator.BpmOperatorRecordService;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmOperatorRecordRespVO;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowTaskRepository;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * @author liyang
 * @date 2025-11-18
 */
@Slf4j
@Service
public class BpmOperatorRecordServiceImpl implements BpmOperatorRecordService {
    @Resource(name = "bpmInsService")
    private InsService insService;

    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource
    private FlowHisTaskRepository hisTaskRepository;

    @Resource
    private FlowTaskRepository taskRepository;

    @Resource(name = "bpmUserService")
    private UserService userService;

    @Resource(name = "bpmNodeService")
    private NodeService nodeService;

    @Resource(name = "bpmDefService")
    private DefService defService;

    @Resource
    private BpmFlowCcRecordRepository ccRecordRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private BpmPermissionResolver permissionResolver;

    @Resource
    private BpmFlowAgentInsRepository agentInsRepository;

    @Resource
    private BpmFlowDefinitionRepositoryExt defExtService;

    public String formatWaitedTime(LocalDateTime startTime) {
        // startTime 是过去的时间（如任务创建时间）
        LocalDateTime now = LocalDateTime.now();

        // 局部常量：只在本方法内使用
        final String WAITED_PREFIX = "已等待";

        // 如果 startTime 在未来（异常情况），视为刚刚
        if (startTime.isAfter(now)) {
            return "刚刚";
        }

        // 注意顺序！
        Duration duration = Duration.between(startTime, now);

        // 不足 1 分钟
        if (duration.getSeconds() < 60) {
            return "刚刚";
        }

        long days = duration.toDays();
        if (days > 0) {
            return WAITED_PREFIX + days + "天";
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return WAITED_PREFIX + hours + "小时";
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return WAITED_PREFIX + minutes + "分钟";
        }

        // 兜底
        return "刚刚";
    }

    private List<HisTask> findAllHisTaskByInsId(Long instanceId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId);
        queryWrapper.orderBy(FlowHisTask::getNodeType, true);
        queryWrapper.orderBy(FlowHisTask::getCreateTime, true);

        List<FlowHisTask> flowHisTasks = hisTaskRepository.list(queryWrapper);

        if (CollectionUtils.isEmpty(flowHisTasks)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(flowHisTasks);
    }

    private List<Task> findAllTaskByInsId(Long instanceId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowTask::getInstanceId, instanceId);
        queryWrapper.orderBy(FlowTask::getNodeType, true);
        queryWrapper.orderBy(FlowTask::getCreateTime, true);

        List<FlowTask> flowTasks = taskRepository.list(queryWrapper);

        if (CollectionUtils.isEmpty(flowTasks)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(flowTasks);
    }

    /**
     * 构建已办历史记录
     */
    private void fillHisTaskRecord(Instance instance, LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
        // 查出已办
        List<HisTask> hisTasks = findAllHisTaskByInsId(instance.getId());

        if (CollectionUtils.isEmpty(hisTasks)) {
            return;
        }

        // 查找代理信息（BpmFlowAgentInsDO 中 principalId 为 String）
        List<BpmFlowAgentInsDO> agentInsDOs = agentInsRepository.findAllByInstanceId(instance.getId());
        Map<String, List<BpmFlowAgentInsDO>> agentExecutorMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(agentInsDOs)) {
            for (BpmFlowAgentInsDO agentInsDO : agentInsDOs) {
                if (!BooleanUtils.toBoolean(agentInsDO.getIsExecutor())) {
                    continue;
                }

                List<BpmFlowAgentInsDO> agentExecutorList = agentExecutorMap.get(agentInsDO.getPrincipalId());

                if (agentExecutorList != null) {
                    agentExecutorList.add(agentInsDO);
                } else {
                    agentExecutorMap.put(agentInsDO.getPrincipalId(),
                            new ArrayList<>(Collections.singletonList(agentInsDO)));
                }
            }
        }

        // 进行组装
        for (HisTask hisTask : hisTasks) {
            Long taskId = hisTask.getTaskId();

            // 跳过非中间节点
            if (!NodeType.isBetween(hisTask.getNodeType())) {
                continue;
            }

            BaseNodeExtDTO extDTO = BpmUtil.getNodeExtDTOByNodeCode(hisTask.getNodeCode(), instance.getDefJson());

            if (extDTO == null) {
                log.warn("获取已办节点扩展数据失败 nodeName: {} nodeCode: {}", hisTask.getNodeCode(), hisTask.getNodeName());
                continue;
            }

            BpmOperatorRecordRespVO.OperatorRecord record = recordMap.get(taskId);

            // 新的节点
            if (record == null) {
                record = new BpmOperatorRecordRespVO.OperatorRecord();
                record.setNodeName(hisTask.getNodeName());
                record.setNodeType(extDTO.getNodeType());

                // 审批节点
                if (extDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
                    record.setApproveMode(approverNodeExtDTO.getApproverConfig().getApprovalMode());
                }

                record.setOperators(new ArrayList<>());
                record.setDisplayTime(hisTask.getUpdateTime());

                recordMap.put(taskId, record);
            }

            // 忽略抄送节点的已办用户数据
            if (Objects.equals(record.getNodeType(), BpmNodeTypeEnum.CC.getCode())) {
                record.setDisplayStatus(hisTask.getFlowStatus());
                continue;
            }

            BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
            operatorInfo.setOperator(hisTask.getApprover());
            operatorInfo.setOperatorTime(hisTask.getUpdateTime());
            operatorInfo.setComment(hisTask.getMessage());
            operatorInfo.setTaskStatus(hisTask.getFlowStatus());

            // 设置代理信息
            List<BpmFlowAgentInsDO> agentInsDOList = agentExecutorMap.get(hisTask.getApprover());

            if (CollectionUtils.isNotEmpty(agentInsDOList)) {
                for (BpmFlowAgentInsDO agentInsDO : agentInsDOList) {
                    if (Objects.equals(agentInsDO.getTaskId(), taskId)) {
                        operatorInfo.setAgent(agentInsDOList.get(0).getAgentId());
                        break;
                    }
                }
            }

            // 已办记录的阅读状态都是true
            operatorInfo.setViewed(true);

            record.getOperators().add(operatorInfo);
            record.setDisplayStatus(hisTask.getFlowStatus());
        }
    }

    private void fillCcRecord(Instance instance, LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
        // 查出当前实例所有抄送信息
        List<BpmFlowCcRecordDO> ccRecords = ccRecordRepository.findAllByInstanceId(instance.getId());

        if (CollectionUtils.isEmpty(ccRecords)) {
            return;
        }

        for (BpmFlowCcRecordDO ccRecord : ccRecords) {
            Long taskId = ccRecord.getTaskId();

            // 全局抄送，可能不绑定任务
            if (taskId == null) {
                continue;
            }

            BpmOperatorRecordRespVO.OperatorRecord record = recordMap.get(taskId);

            // 抄送应该有绑定的节点信息
            if (record == null) {
                log.warn("获取抄送记录失败，taskId: {}", taskId);
                continue;
            }

            BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
            operatorInfo.setOperator(ccRecord.getUserId());
            operatorInfo.setOperatorTime(ccRecord.getUpdateTime());
            operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.POST_AUTO_CC.getCode());
            operatorInfo.setViewed(BooleanUtils.toBoolean(ccRecord.getViewed()));

            record.setDisplayStatus(operatorInfo.getTaskStatus());

            record.getOperators().add(operatorInfo);
        }
    }

    private void fillTaskRecord(Instance instance, LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
        // 查出待办
        List<Task> tasks = findAllTaskByInsId(instance.getId());

        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        // 处理待办数据
        for (Task task : tasks) {
            Long taskId = task.getId();

            BaseNodeExtDTO extDTO = BpmUtil.getNodeExtDTOByNodeCode(task.getNodeCode(), instance.getDefJson());

            if (extDTO == null) {
                log.warn("获取待办节点扩展数据失败 nodeName: {} nodeCode: {}", task.getNodeCode(), task.getNodeName());
                continue;
            }

            BpmOperatorRecordRespVO.OperatorRecord record = recordMap.get(taskId);
            String nodeType = extDTO.getNodeType();

            if (record == null) {
                record = new BpmOperatorRecordRespVO.OperatorRecord();
                record.setNodeName(task.getNodeName());
                record.setNodeType(nodeType);

                if (extDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
                    record.setApproveMode(approverNodeExtDTO.getApproverConfig().getApprovalMode());
                }

                // 待办任务节点是当前节点
                record.setCurrent(true);
                record.setDisplayTime(task.getCreateTime());
                record.setWaitTimeDesc(formatWaitedTime(record.getDisplayTime()));
                record.setOperators(new ArrayList<>());

                recordMap.put(taskId, record);
            }

            // 查找有权限的用户，todo：显示代理人？
            List<User> users = userService.getByAssociateds(List.of(task.getId()),
                    BpmUserTypeEnum.APPROVAL.getCode(),
                    BpmUserTypeEnum.TRANSFER.getCode(),
                    BpmUserTypeEnum.DEPUTE.getCode());

            if (CollectionUtils.isEmpty(users)) {
                continue;
            }

            for (User user : users) {
                BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                operatorInfo.setOperator(user.getProcessedBy());
                operatorInfo.setOperatorTime(task.getUpdateTime());

                // 判断节点类型
                if (Objects.equals(nodeType, BpmNodeTypeEnum.INITIATION.getCode())) {
                    operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.CURR_PENDING_SUBMIT.getCode());
                } else if (Objects.equals(nodeType, BpmNodeTypeEnum.APPROVER.getCode())) {
                    operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.CURR_IN_APPROVAL.getCode());
                } else if (Objects.equals(nodeType, BpmNodeTypeEnum.EXECUTOR.getCode())) {
                    operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.CURR_IN_EXEC.getCode());
                } else {
                    // todo: 其他节点类型
                    operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.CURR_IN_APPROVAL.getCode());
                }

                // 通过user的更新时间来判断，是否已阅
                boolean viewed = user.getUpdateTime().isAfter(user.getCreateTime());
                operatorInfo.setViewed(viewed);

                // 只要有待办，展示状态与任务状态一致
                record.setDisplayStatus(operatorInfo.getTaskStatus());
                record.setDisplayTime(task.getCreateTime());
                record.setWaitTimeDesc(formatWaitedTime(record.getDisplayTime()));

                // 待办任务节点是当前节点
                record.setCurrent(true);

                record.getOperators().add(operatorInfo);
            }
        }
    }

    private void fillPredictRecord(Instance instance, LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
       String currNodeCode = instance.getNodeCode();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        if (NodeType.isEnd(instance.getNodeType())) {
           log.info("当前节点为结束节点");
           return;
        }

        // 获取最新的流程定义
        String definitionUuid = instance.getDefinitionUuid();

        Definition definition = defExtService.getOneByUuid(definitionUuid);
        if (definition == null) {
            log.warn("获取流程定义失败 definitionUuid: {}", definitionUuid);
            return;
        }

        while (true) {
            // todo：理论上只会有一条路径，需要结合实体信息预测下一步走向，主要是涉及到条件分支的
            Node nextNode = nodeService.getNextNode(definition.getId(), currNodeCode, null, SkipType.PASS.getKey());

            if (nextNode == null) {
                log.warn("下一个节点为空");
                break;
            }

            if (NodeType.isEnd(nextNode.getNodeType())) {
                break;
            }

            // 设置下一个节点编码
            currNodeCode = nextNode.getNodeCode();

            BaseNodeExtDTO extDTO = JsonUtils.parseObject(nextNode.getExt(), BaseNodeExtDTO.class);
            if (extDTO == null) {
                log.warn("获取预测节点扩展数据失败 nodeName: {} nodeCode: {}", nextNode.getNodeName(), nextNode.getNodeCode());
                continue;
            }

            // 获取业务节点类型
            String bizNodeType = extDTO.getNodeType();

            // 预测节点无对应任务ID，创建一个
            Long predictTaskId = uidGenerator.getUID();
            BpmOperatorRecordRespVO.OperatorRecord record = new BpmOperatorRecordRespVO.OperatorRecord();
            record.setNodeName(nextNode.getNodeName());
            record.setNodeType(bizNodeType);
            record.setOperators(new ArrayList<>());

            if (extDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
                record.setApproveMode(approverNodeExtDTO.getApproverConfig().getApprovalMode());
            }

            recordMap.put(predictTaskId, record);

            // 发起节点，理论上不会存在该情况，todo：待确认
            if (Objects.equals(bizNodeType, BpmNodeTypeEnum.INITIATION.getCode())) {
                BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                operatorInfo.setOperator(String.valueOf(loginUserId));
                operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.CURR_PENDING_SUBMIT.getCode());
                record.getOperators().add(operatorInfo);

                record.setDisplayStatus(BpmNodeApproveStatusEnum.CURR_PENDING_SUBMIT.getCode());
            } else if (Objects.equals(bizNodeType, BpmNodeTypeEnum.APPROVER.getCode())) {
                // 解析权限标志
                Set<String> userIds = permissionResolver.resolveUserIds(nextNode.getPermissionFlag(), BpmConstants.MAX_NODE_APPROVER_USERS);

                if (CollectionUtils.isNotEmpty(userIds)) {
                    for (String userId : userIds) {
                        BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                        operatorInfo.setOperator(userId);
                        operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.PRE_APPROVAL.getCode());
                        record.getOperators().add(operatorInfo);
                    }
                }

                record.setDisplayStatus(BpmNodeApproveStatusEnum.PRE_APPROVAL.getCode());
            } else if (Objects.equals(bizNodeType, BpmNodeTypeEnum.CC.getCode())) {
                Set<String> userIds = permissionResolver.resolveUserIds(nextNode.getPermissionFlag(), BpmConstants.MAX_NODE_CC_USERS);

                if (CollectionUtils.isNotEmpty(userIds)) {
                    for (String userId : userIds) {
                        BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                        operatorInfo.setOperator(userId);
                        operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.PRE_AUTO_CC.getCode());
                        record.getOperators().add(operatorInfo);
                    }
                }

                record.setDisplayStatus(BpmNodeApproveStatusEnum.PRE_AUTO_CC.getCode());
            } else {
                // todo: 支持更多节点类型
            }
        }
    }

    private void fillEndRecord(Instance instance, LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
        Long endId = uidGenerator.getUID();
        BpmOperatorRecordRespVO.OperatorRecord endRecord = new BpmOperatorRecordRespVO.OperatorRecord();
        endRecord.setNodeName("结束");
        endRecord.setNodeType(BpmNodeTypeEnum.END.getCode());

        // 判断是否结束
        if (CollectionUtils.isEmpty(taskService.getByInsId(instance.getId()))) {
            // 结束流程
            endRecord.setCurrent(true);
        }

        recordMap.put(endId, endRecord);
    }

    private void fillOperatorInfo(LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
        if (recordMap.isEmpty()) {
            return;
        }

        Set<Long> userIds = new HashSet<>();

        for (BpmOperatorRecordRespVO.OperatorRecord operatorRecord : recordMap.values()) {
            if (CollectionUtils.isEmpty(operatorRecord.getOperators())) {
                continue;
            }

            for (BpmOperatorRecordRespVO.OperatorInfo operatorInfo : operatorRecord.getOperators()) {
                userIds.add(Long.parseLong(operatorInfo.getOperator()));

                if (StringUtils.isNotBlank(operatorInfo.getAgent())) {
                    userIds.add(Long.parseLong(operatorInfo.getAgent()));
                }
            }
        }

        CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserList(userIds);

        // todo: 抛出异常？
        if (!result.isSuccess()) {
            log.warn("获取用户列表失败，userIds: {}", userIds);
            return;
        }

        if (CollectionUtils.isEmpty(result.getData())) {
            return;
        }

        List<AdminUserRespDTO> users = result.getData();
        Map<Long, AdminUserRespDTO> userMap = new HashMap<>();

        for (AdminUserRespDTO user : users) {
            userMap.put(user.getId(), user);
        }

        // 填充数据
        for (BpmOperatorRecordRespVO.OperatorRecord operatorRecord : recordMap.values()) {
            if (CollectionUtils.isEmpty(operatorRecord.getOperators())) {
                continue;
            }

            for (BpmOperatorRecordRespVO.OperatorInfo operatorInfo : operatorRecord.getOperators()) {
                AdminUserRespDTO user = userMap.get(Long.parseLong(operatorInfo.getOperator()));

                if (user != null) {
                    operatorInfo.setOperator(user.getNickname());
                    operatorInfo.setAvatar(user.getAvatar());
                } else {
                    // todo: 用户不存在
                    operatorInfo.setOperator("-");
                }

                if (StringUtils.isNotBlank(operatorInfo.getAgent())) {
                    AdminUserRespDTO agentUser = userMap.get(Long.parseLong(operatorInfo.getAgent()));

                    if (agentUser != null) {
                        operatorInfo.setOperator(operatorInfo.getOperator() + "（" + agentUser.getNickname() + "代理）");
                    } else {
                        operatorInfo.setOperator(operatorInfo.getOperator() + "（-代理）");
                    }

                    // 代理人暂时不对前端展示
                    operatorInfo.setAgent(null);
                }
            }
        }
    }

    @Override
    public List<BpmOperatorRecordRespVO.OperatorRecord> getOperatorRecord(Long instanceId) {
        Instance instance = insService.getById(instanceId);

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap = new LinkedHashMap<>();

        // 填充历史记录
        fillHisTaskRecord(instance, recordMap);

        // 填充抄送记录
        fillCcRecord(instance, recordMap);

        // 填充待办信息
        fillTaskRecord(instance, recordMap);

        // 填充预测信息
        fillPredictRecord(instance, recordMap);

        // 加上结束节点
        fillEndRecord(instance, recordMap);

        // 填充用户数据
        fillOperatorInfo(recordMap);

        return recordMap.values().stream().toList();
    }
}
