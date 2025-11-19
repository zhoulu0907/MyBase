package com.cmsr.onebase.module.bpm.runtime.service.operator.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowCcRecordRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeApproveStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.bpm.runtime.service.operator.BpmOperatorRecordService;
import com.cmsr.onebase.module.bpm.runtime.service.permission.BpmPermissionResolver;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmOperatorRecordRespVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * @author liyang
 * @date 2025-11-18
 */
@Slf4j
@Service
public class BpmOperatorRecordServiceImpl implements BpmOperatorRecordService {
    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    @Resource
    private HisTaskService hisTaskService;

    @Resource
    private UserService userService;

    @Resource
    private NodeService nodeService;

    @Resource
    private BpmFlowCcRecordRepository ccRecordRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private AppAuthRoleUser appAuthRoleUser;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private BpmPermissionResolver permissionResolver;

    /**
     * 构建已办历史记录
     */
    private void fillHisTaskRecord(Instance instance, LinkedHashMap<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap) {
        // 查出已办
        List<HisTask> hisTasks = hisTaskService.getByInsId(instance.getId());

        if (CollectionUtils.isEmpty(hisTasks)) {
            return;
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
            if (StringUtils.isNotBlank(hisTask.getCollaborator()) && StringUtils.isNotBlank(hisTask.getExt())) {
                Map<String, Object> lastHisExtMap = JsonUtils.parseObject(hisTask.getExt(), new TypeReference<>() {});
                String agentId = MapUtils.getString(lastHisExtMap, "agentId");

                if (StringUtils.isNotBlank(agentId) && !Objects.equals(hisTask.getApprover(), agentId)) {
                    operatorInfo.setAgent(agentId);
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
        List<Task> tasks = taskService.getByInsId(instance.getId());

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
                boolean viewed = user.getUpdateTime().isAfter(user.getUpdateTime());
                operatorInfo.setViewed(viewed);

                // 只要有待办，展示状态与任务状态一致
                record.setDisplayStatus(operatorInfo.getTaskStatus());

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

        while (true) {
            // todo：理论上只会有一条路径，需要结合实体信息预测下一步走向，主要是涉及到条件分支的
            Node nextNode = nodeService.getNextNode(instance.getDefinitionId(), currNodeCode, null, SkipType.PASS.getKey());

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
                Set<Long> userIds = permissionResolver.resolveUserIds(nextNode.getPermissionFlag(), BpmConstants.MAX_NODE_APPROVER_USERS);

                if (CollectionUtils.isNotEmpty(userIds)) {
                    for (Long userId : userIds) {
                        BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                        operatorInfo.setOperator(String.valueOf(userId));
                        operatorInfo.setTaskStatus(BpmNodeApproveStatusEnum.PRE_APPROVAL.getCode());
                        record.getOperators().add(operatorInfo);
                    }
                }

                record.setDisplayStatus(BpmNodeApproveStatusEnum.PRE_APPROVAL.getCode());
            } else if (Objects.equals(bizNodeType, BpmNodeTypeEnum.CC.getCode())) {
                Set<Long> userIds = permissionResolver.resolveUserIds(nextNode.getPermissionFlag(), BpmConstants.MAX_NODE_CC_USERS);

                if (CollectionUtils.isNotEmpty(userIds)) {
                    for (Long userId : userIds) {
                        BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                        operatorInfo.setOperator(String.valueOf(userId));
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
